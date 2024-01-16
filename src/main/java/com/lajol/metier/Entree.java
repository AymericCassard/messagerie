package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Entree {

  private InetAddress addr;
	public enum Request {
		error(0),
    getOneContact(1),
    setContactConnected(2);

    public int code;

    private Request(int code) {
      this.code = code;
    }

    public int getCode() {
      return this.code;
    }
	}

  //Connection with Client
  private ServerSocket clientServerSocket;
  private Socket clientSocket;
  private ObjectOutputStream clientOutput;
  private ObjectInputStream clientInput;

  //Connection with annuaire
  private Socket annuaireSocket;
  private ObjectOutputStream annuaireOutput;
  private ObjectInputStream annuaireInput;

  public Entree(InetAddress addr) throws IOException {
    this.addr = addr;
    //initialize Connection with Annuaire
    this.annuaireSocket = new Socket(addr, 8000);
    this.annuaireOutput = new ObjectOutputStream(this.annuaireSocket.getOutputStream());
    this.annuaireInput = new ObjectInputStream(this.annuaireSocket.getInputStream());
    //MUST BE LAST
    //initialize Connection with Client
    this.clientServerSocket = new ServerSocket(8001);
    this.clientSocket = clientServerSocket.accept();
    this.clientInput = new ObjectInputStream(clientSocket.getInputStream());
    this.clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
  }

  public void shutdownAnnuaireConnection() throws IOException {
    System.out.println("Fermeture connexion annuaire...");
    this.annuaireSocket.close();
  }

  private void sendRequestCode(Request request, ObjectOutputStream output) {
    try {
      output.writeObject(request.getCode());
    } catch(IOException e) {
      System.out.println(e);
    }
  }

  private void sendObject(Object toSend, ObjectOutputStream output) {
    try {
      output.writeObject(toSend);
    } catch(IOException e) {
      System.out.println(e);
    }   
  }

  public Contact askAnnuaireForContact(String nom) throws IOException, ClassNotFoundException {
    sendRequestCode(Request.getOneContact, annuaireOutput);
    sendObject(nom, annuaireOutput);
    return (Contact) annuaireInput.readObject();
  }

  public boolean askAnnuaireToConnect(Contact toConnect) throws ClassNotFoundException, IOException {
    sendRequestCode(Request.setContactConnected, annuaireOutput);
    sendObject(toConnect, annuaireOutput);
    return (boolean) annuaireInput.readObject();
  }

  public Contact processClientConnection() throws ClassNotFoundException, IOException {
    String clientName = (String)clientInput.readObject();

    //Returns null if client doesn't exist
    Contact client = askAnnuaireForContact(clientName);
    System.out.println(client.toString());
    System.out.println(!(client == null));

    //Si l'annuaire parvient à connecter le client, on renvoie
    //Ok au client
    boolean connectionStatus = askAnnuaireToConnect(client);
    System.out.println(connectionStatus);
    sendObject(connectionStatus, clientOutput);

    return client;
  }
}
