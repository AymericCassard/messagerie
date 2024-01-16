package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  private Scanner sc;
  private InetAddress addr;
  // public enum Request

  //Connection with Entree
  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;

  public Client() throws IOException {
    this.addr = InetAddress.getLocalHost();
    this.sc = new Scanner(System.in);
    this.socket = new Socket(addr,8001);
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.input = new ObjectInputStream(this.socket.getInputStream());
  }

  public void shutdownConnection() throws IOException {
    System.out.println("Fermeture...");
    this.socket.close();
  }

  public void connect() throws IOException, ClassNotFoundException {
    System.out.print("Entrez votre nom d'utilisateur :");
    output.writeObject(sc.next());  
    boolean response = (boolean)input.readObject();
    System.out.print(response);
    // response ? System.out.println("Success") : System.out.println("Cet utilisateur n'existe pas");
    if (response) {
      System.out.println("Succès");
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
