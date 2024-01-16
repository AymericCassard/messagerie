package com.lajol.metier;

import java.io.Serializable;

public class Contact implements Serializable {

  private String nom;
  private String ip;
  private boolean isConnected;

  public Contact(String nom, String ip) {
    this.nom = nom;
    this.ip = ip;
    this.isConnected = false;
  }

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getIp() {
    return this.ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public boolean getIsConnected() {
    return this.isConnected;
  }

  public void setIsConnected(boolean isConnected) {
    this.isConnected = isConnected;
  }

  public String toString() {
    return "Contact={" + " nom=" + this.nom + " / ip=" + this.ip +  " / isConnected=" + this.isConnected + " }";
  }
}
