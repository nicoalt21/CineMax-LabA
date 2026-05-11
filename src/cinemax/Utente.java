package cinemax;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utente {

	private String nome;
	private String cognome;
	private String username;
	private String passwordCifrata;
	private String dataNascita;
	private String domicilio;
	private Ruolo ruolo;

	public Utente(String nome, String cognome, String username, String password, String dataNascita, String domicilio, Ruolo ruolo, boolean daCifrare) {
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.dataNascita = dataNascita;
		this.domicilio = domicilio;
		this.ruolo = ruolo;

		if (daCifrare) {
			this.passwordCifrata = eseguiHashing(password);
		} else {
			this.passwordCifrata = password;
		}
	}

	public String getNome() { return nome; }
	public String getCognome() { return cognome; }
	public String getUsername() { return username; }
	public String getDataNascita() { return dataNascita; }
	public String getDomicilio() { return domicilio; }
	public Ruolo getRuolo() { return ruolo; }

	public boolean verificaPassword(String passwordChiara) {
		return this.passwordCifrata.equals(eseguiHashing(passwordChiara));
	}

	private String eseguiHashing(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Errore di sistema: algoritmo SHA-256 mancante.", e);
		}
	}
}