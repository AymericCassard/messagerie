/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lajol.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lajol.metier.Citation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author major
 */
public class CitationDuJour {

  private ServerSocket serverSocket;
  private Socket socket;
  private ObjectInputStream input;
  private ObjectOutputStream output;

  public CitationDuJour() throws IOException {
    this.serverSocket = new ServerSocket(9000);
    this.socket = serverSocket.accept();

    // once socket is initialized, open I / O
    this.input = new ObjectInputStream(socket.getInputStream());
    this.output = new ObjectOutputStream(socket.getOutputStream());
  }

  public void startListening() throws IOException, ClassNotFoundException {
    while (!this.socket.isClosed()) {
      boolean signal = (boolean) input.readObject();
      output.writeObject(obtenirCitationDuJour());
    }
  }

  public String obtenirCitationDuJour() throws IOException {

    Citation lacitation = new Citation();

    URL url = new URL("https://api.quotable.io/random");

    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.connect();

    // Utilisation de Jackson pour mapper le JSON vers l'objet Citation
    ObjectMapper objectMapper = new ObjectMapper();
    Citation citationAPI = objectMapper.readValue(conn.getInputStream(), Citation.class);

    return citationAPI.getContent();

    // System.out.println(citationAPI.getContent());
    // System.out.println("Une citation de " + citationAPI.getAuthor());
    // return lacitation;
  }

  public static void main(String[] args) {
    try {
      CitationDuJour citationServer = new CitationDuJour();
      citationServer.startListening();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println(e);
    }
  }
}
