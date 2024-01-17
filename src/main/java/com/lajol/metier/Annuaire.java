package com.lajol.metier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class Annuaire {

	private ServerSocket serverSocket;
  private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	public enum requestType {
		error,
		getOneContact,
		setContactConnected;
	}
	private ArrayList<Contact> contactList;

	//Initialize and wait for Client connection
  public Annuaire() throws IOException {
		this.serverSocket = new ServerSocket(8000);
		this.socket = serverSocket.accept();

		//once socket is initialized, open I / O
		this.input = new ObjectInputStream(socket.getInputStream());
		this.output = new ObjectOutputStream(socket.getOutputStream());

		this.contactList = new ArrayList<>();

		//Static List for now
		this.contactList.add(new Contact("test", "127.0.0.1"));
		this.contactList.add(new Contact("admin", "192.168.1.33"));
	}

	private requestType requestTypeFromResponse(int i) {
		requestType type;
		switch(i) {
			case 1:
				type = requestType.getOneContact;
				break;
			case 2:
				type = requestType.setContactConnected;
				break;
			default:
				type = requestType.error;
				break;
			}
		return type;
	}

	private requestType getResponseFromClient() throws IOException {
		try {
			int response = (int) input.readObject();
			return requestTypeFromResponse(response);
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Erreur dans la réponse");
			System.out.println(e);
			System.out.println("Fermeture...");
			this.socket.close();
		}
		return requestType.error;
	}

	private Contact getOneContact(String name) {
		final String localName = name;
		return this.contactList.stream()
		.filter(contact -> contact.getNom().equals(localName))
		.findAny()
		.orElse(null);
	}

	private void actOnRequest(requestType request) {
		switch (request) {
    	case getOneContact:
    		sendAskedContactInfo();
    		break;
    	case setContactConnected:
    		setContactConnected();
    		break;
    	default:
    		break;
    	}
	}

	private void sendAskedContactInfo() {
		try {
			String askedName = (String)input.readObject();
			System.out.println("asked name = " + String.valueOf(askedName));
			Contact askedContact = getOneContact(askedName);
			output.writeObject(askedContact);
		} catch (ClassNotFoundException | IOException e) {
			System.out.println(e);
		}
	}


	private void setContactConnected() {
		try {
 			Contact toConnect = (Contact)input.readObject();
			System.out.println("asked to connect name = " + String.valueOf(toConnect));
			for(Contact c : contactList) {
				if(c.getNom().equals(toConnect.getNom())) {
					c.setIsConnected(true);
					c.setLastConnection(new Date());
					System.out.println("asked to connect name in Loop = " + String.valueOf(c));
					output.writeObject(true);
					return;
				}
			}
			output.writeObject(false);
		} catch (ClassNotFoundException | IOException e) {
			System.out.println(e);
		}
	}

	public void startListening() throws IOException {
		while(!socket.isClosed()) {
			actOnRequest(getResponseFromClient());
		}
	}
}
