package com.lajol.app;

import java.io.IOException;

import com.lajol.metier.Client;

public class AppClient {
  public static void main(String[] args) {
      try {
        Client client = new Client();
        client.connect();
        client.shutdownConnection();
        System.out.println("?");
      } catch(IOException | ClassNotFoundException e) {
        System.out.println(e);
    }
  }
}

