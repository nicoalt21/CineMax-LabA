package cinemax;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Rappresenta un utente registrato nel sistema CineMax.
 * Gestisce le informazioni anagrafiche, il ruolo e la sicurezza della password
 * tramite l'algoritmo di hashing SHA-256.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Utente {

	private String nome;
	private String cognome;
	private String username;
	private String passwordCifrata;
	private String dataNascita;
	private String domicilio;
	private Ruolo ruolo;

	/**
	 * Crea un nuovo utente.
	 * * @param nome Nome dell'utente.
	 * @param cognome Cognome dell'utente.
	 * @param username Identificativo univoco per il login.
	 * @param password Password in chiaro (da cifrare) o hash già esistente.
	 * @param dataNascita Data di nascita dell'utente.
	 * @param domicilio Indirizzo o città di residenza.
	 * @param ruolo Livello di accesso (CLIENTE, BIGLIETTAIO, PROIEZIONISTA).
	 * @param daCifrare Se true, la password viene passata all'algoritmo di hash.
	 * Se false, viene salvata direttamente (uso per lettura da file).
	 */
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
	public String getPasswordCifrata() { return passwordCifrata; }
	public String getDataNascita() { return dataNascita; }
	public String getDomicilio() { return domicilio; }
	public Ruolo getRuolo() { return ruolo; }

	/**
	 * Verifica se una password fornita in input corrisponde a quella salvata.
	 * * @param passwordChiara La password inserita nel form di login.
	 * @return true se l'hash dell'input coincide con l'hash memorizzato, false altrimenti.
	 */
	public boolean verificaPassword(String passwordChiara) {
		return this.passwordCifrata.equals(eseguiHashing(passwordChiara));
	}

	/**
	 * Genera l'hash SHA-256 di una stringa.
	 * * @param input La stringa da cifrare.
	 * @return La rappresentazione esadecimale dell'hash.
	 */
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
			throw new RuntimeException("Errore critico: algoritmo di hashing non trovato.", e);
		}
	}
}