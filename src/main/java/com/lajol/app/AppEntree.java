package com.lajol.app;

import java.io.IOException;
import java.net.InetAddress;

import com.lajol.metier.Contact;
import com.lajol.metier.Entree;

/**
 * Hello world!
 *
 */
public class AppEntree {

	public static void main(String[] args) {
        try {
            Entree entree = new Entree(InetAddress.getLocalHost());
            entree.manageClientConnection();
            System.out.println("?");
            // entree.shutdownAnnuaireConnection();  
        } catch(IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
	}
}
