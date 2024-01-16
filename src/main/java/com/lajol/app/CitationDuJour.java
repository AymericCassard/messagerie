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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author major
 */
public class CitationDuJour {

    public Citation obtenirCitationDuJour() throws IOException {

        Citation lacitation = new Citation();

        URL url = new URL("https://api.quotable.io/random");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Utilisation de Jackson pour mapper le JSON vers l'objet Citation
        ObjectMapper objectMapper = new ObjectMapper();
        Citation citationAPI = objectMapper.readValue(conn.getInputStream(), Citation.class);

        System.out.println(citationAPI.getContent());
        System.out.println("Une citation de "+citationAPI.getAuthor());
        return lacitation;
    }
}