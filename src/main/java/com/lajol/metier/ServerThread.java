package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerThread extends Thread {

  // for client
  private ServerSocket serverSocket;

  private Socket annuaireSocket;
  private ObjectOutputStream annuaireOutput;
  private ObjectInputStream annuaireInput;

  private ObjectOutputStream citationOutput;
  private ObjectInputStream citationInput;

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

  public ServerThread(
      ServerSocket serverSocket,
      ObjectOutputStream annuaireOutput,
      ObjectInputStream annuaireInput,
      ObjectOutputStream citationOutput,
      ObjectInputStream citationInput,
      int ThreadCount) throws IOException {
    this.serverSocket = serverSocket;
    this.setName("Thread n°" + String.valueOf(ThreadCount));
    this.annuaireOutput = annuaireOutput;
    this.annuaireInput = annuaireInput;
    this.citationOutput = citationOutput;
    this.citationInput = citationInput;
  }

  public void shutdownAnnuaireConnection() throws IOException {
    System.out.println("Fermeture connexion annuaire...");
    this.annuaireSocket.close();
  }

  private void sendRequestCode(Request request, ObjectOutputStream output) {
    try {
      output.writeObject(request.getCode());
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  private void sendObject(Object toSend, ObjectOutputStream output) {
    try {
      output.writeObject(toSend);
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public Contact askAnnuaireForContact(String nom) throws IOException, ClassNotFoundException {
    sendRequestCode(Request.getOneContact, annuaireOutput);
    sendObject(nom, annuaireOutput);
    return (Contact) this.annuaireInput.readObject();
  }

  public boolean askAnnuaireToConnect(Contact toConnect) throws ClassNotFoundException, IOException {
    sendRequestCode(Request.setContactConnected, annuaireOutput);
    sendObject(toConnect, annuaireOutput);
    return (boolean) annuaireInput.readObject();
  }

  public String askCitationFromServer() throws IOException, ClassNotFoundException {
    sendObject(true, citationOutput);
    return (String) citationInput.readObject();
  }

  public boolean needCitation(Contact toCheck) {
    if (toCheck.getLastConnection() == null) {
      return true;
    } else {
      //8.64 * 10^7 = ms in a day
      return (new Date().getTime() - toCheck.getLastConnection().getTime() > 8.64 * Math.pow(10, 7));
    }
  }

  public Contact connectClient() throws IOException, ClassNotFoundException {
    System.out.println(this.getName() + " is launched");
    System.out.println(serverSocket);
    // Initialize client connection
    Socket clientSocket = serverSocket.accept();
    System.out.println(clientSocket);
    System.out.println(this.getName() + " accepted connection");
    System.out.println(serverSocket);
    ObjectInputStream clientInput = new ObjectInputStream(clientSocket.getInputStream());
    ObjectOutputStream clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());

    // Start business Logic
    String clientName = (String) clientInput.readObject();
    Contact client = askAnnuaireForContact(clientName);
    System.out.println(client.toString());
    System.out.println(!(client == null));

    boolean connectionStatus = askAnnuaireToConnect(client);
    System.out.println(connectionStatus);
    sendObject(connectionStatus, clientOutput);
    String connectionMesssage = "[SERVEUR] - ";
    if(needCitation(client)) {
     connectionMesssage = connectionMesssage + askCitationFromServer(); 
    } else {
      connectionMesssage = connectionMesssage + "connexion réussie";
    }
    sendObject(connectionMesssage, clientOutput);
    return client;
  }

  @Override
  public void run() {
    try {
      connectClient();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println(e);
      System.out.println(this.toString());
    }

  }

}
