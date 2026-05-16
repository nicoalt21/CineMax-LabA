package cinemax;

/**
 * Rappresenta un utente registrato nel sistema CineMax.
 * Gestisce le informazioni anagrafiche, il ruolo e la sicurezza della password.
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
	 *
	 * @param nome Nome dell'utente.
	 * @param cognome Cognome dell'utente.
	 * @param username Identificativo univoco per il login.
	 * @param passwordCifrata Hash della password calcolato tramite Cifrario.
	 * @param dataNascita Data di nascita dell'utente.
	 * @param domicilio Indirizzo o città di residenza.
	 * @param ruolo Livello di accesso.
	 */
	public Utente(String nome, String cognome, String username, String passwordCifrata, String dataNascita, String domicilio, Ruolo ruolo) {
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.passwordCifrata = passwordCifrata;
		this.dataNascita = dataNascita;
		this.domicilio = domicilio;
		this.ruolo = ruolo;
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
	 *
	 * @param password La password in chiaro inserita nel form di login.
	 * @return true se l'hash dell'input coincide con l'hash memorizzato, false altrimenti.
	 */
	public boolean verificaPassword(String password) {
		return this.passwordCifrata.equals(Cifrario.cifraPassword(password));
	}
}