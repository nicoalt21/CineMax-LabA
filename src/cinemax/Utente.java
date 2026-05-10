package cinemax;

public class Utente {

	private String nome;
    private String cognome;
    private String username;
    private String password;
    private String dataNascita;
    private String domicilio;
    private Ruolo ruolo;
    
    public Utente(String nome, String cognome, String username, String password, String dataNascita, String domicilio, Ruolo ruolo) {

		this.nome = nome;
    	this.cognome = cognome;
    	this.username = username; // MATO: da aggiungere un controllo: (username != back), la parola back la uso in MenuPrinciaple per muovermi fra le pagine, quindi non è ammessa come username.
    	this.password = password;
    	this.dataNascita = dataNascita;
    	this.domicilio = domicilio;
    	this.ruolo = ruolo;
    }
    
    public String getNome() {
		return nome;
	}
	public String getCognome() {
		return cognome;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getDataNascita() {
		return dataNascita;
	}
	public String getDomicilio() {
		return domicilio;
	}
	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}
}