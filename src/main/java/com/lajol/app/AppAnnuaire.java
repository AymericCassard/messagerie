package com.lajol.app;

import java.io.IOException;

import com.lajol.metier.Annuaire;

/**
 * Hello world!
 *
 */
public class AppAnnuaire {

	public static void main(String[] args) {
		try {
			Annuaire annuaire = new Annuaire();    
			annuaire.startListening();
      System.out.println("?");
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
