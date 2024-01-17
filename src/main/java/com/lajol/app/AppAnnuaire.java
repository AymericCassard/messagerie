package com.lajol.app;

import java.io.IOException;

import com.lajol.metier.Annuaire;
import com.lajol.metier.Citation;

/**
 * Hello world!
 *
 */
public class AppAnnuaire {

	public static void main(String[] args) {
		try {
			// CitationDuJour citationDuJour= new CitationDuJour();
			// Citation citation = new Citation();
			// citation = citationDuJour.obtenirCitationDuJour();
			// System.out.println(citation.getContent());


			Annuaire annuaire = new Annuaire();    
			annuaire.startListening();
      System.out.println("?");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
