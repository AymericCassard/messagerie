package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
  private ArrayList<ServerSocket> serverSocketList;
  private ArrayList<ServerThread> serverThreads;
  private final int THREAD_PORT = 8002;

  private ServerSocket clientServerSocket;
  private Socket clientSocket;
  private ObjectOutputStream clientOutput;
  private ObjectInputStream clientInput;

  //Connection with annuaire
  private Socket annuaireSocket;
  private ObjectOutputStream annuaireOutput;
  private ObjectInputStream annuaireInput;

  //Connection with Citation
  private Socket citationSocket;
  private ObjectOutputStream citationOutput;
  private ObjectInputStream citationInput;

  public Entree(InetAddress addr) throws IOException {
    this.addr = addr;
    //initialize Connection with Annuaire
    this.annuaireSocket = new Socket(addr, 8000);
    this.annuaireOutput = new ObjectOutputStream(this.annuaireSocket.getOutputStream());
    this.annuaireInput = new ObjectInputStream(this.annuaireSocket.getInputStream());
    //initialize Connection with Citation
    this.citationSocket = new Socket(addr, 9000);
    this.citationOutput = new ObjectOutputStream(this.citationSocket.getOutputStream());
    this.citationInput = new ObjectInputStream(this.citationSocket.getInputStream());
    //MUST BE LAST
    //initialize Connection with Client
    this.clientServerSocket = new ServerSocket(8001);
    // this.serverSocketList = new ArrayList<>();
    // for (int i = 0; i < 2; i++) {
    //   this.serverSocketList.add(new ServerSocket((8001 + i), 1));
    //   // System.out.println()
    // }

    // this.clientSocket = clientServerSocket.accept();
    // this.clientInput = new ObjectInputStream(clientSocket.getInputStream());
    // this.clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
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

  public void manageClientConnection() throws IOException, ClassNotFoundException {
    int i = 0;
    while(true) {
      int currentThreadPort = THREAD_PORT + i;
      System.out.println(currentThreadPort);
      clientSocket = clientServerSocket.accept();
      clientInput = new ObjectInputStream(clientSocket.getInputStream());
      clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
      ServerThread serverThread = new ServerThread(
        new ServerSocket(currentThreadPort),
        this.annuaireOutput,
        this.annuaireInput,
        this.citationOutput,
        this.citationInput,
        i
      );
      serverThread.start();
      sendObject(currentThreadPort,this.clientOutput);
      boolean okSignal = (boolean)clientInput.readObject();
      this.clientSocket.close();
      i++;
    }
  }
 
}
