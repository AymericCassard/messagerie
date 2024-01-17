package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client {

  private Scanner sc;
  private InetAddress addr;
  // public enum Request
  //Connection with Entree port Manager
  private Socket portManagerSocket;
  private ObjectOutputStream portOutput;
  private ObjectInputStream portInput;

  //Connection with Entree
  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;

  public Client() throws IOException {
    this.addr = InetAddress.getLocalHost();
    this.sc = new Scanner(System.in);
  }

  public void shutdownConnection() throws IOException {
    System.out.println("Fermeture...");
    this.socket.close();
  }

  public void connect() throws IOException, ClassNotFoundException {

    //Get available port From Entree
    this.portManagerSocket = new Socket(addr, 8001);
    this.portOutput = new ObjectOutputStream(portManagerSocket.getOutputStream());
    this.portInput = new ObjectInputStream(portManagerSocket.getInputStream());
    int availablePort = (int)portInput.readObject();
    //OK SIGNAL
    this.portOutput.writeObject(true);

    this.socket = new Socket(addr, availablePort);
    
    System.out.println(this.socket.toString());
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.input = new ObjectInputStream(this.socket.getInputStream());
    System.out.print("Entrez votre nom d'utilisateur :");
    output.writeObject(sc.next());  
    boolean response = (boolean)input.readObject();
    System.out.print(response);
    if (response) {
      String ConnectionMessage = (String)input.readObject();
      System.out.println(ConnectionMessage);
    } else {
      System.out.println("Echec");
      shutdownConnection();
    }
  }

  public void sendMessage() throws IOException {
    System.out.print("Quel est le destinataire ?");
    output.writeObject(sc.next());
  }
  

}
