package cinemax;

import java.time.LocalDate;

public class Utente {
	private String nome;
    private String cognome;
    private String username;
    private String password;
    private LocalDate dataNascita;
    private String domicilio;
    private Ruolo ruolo;
    
    public Utente(String nome, String cognome, String username, String password, LocalDate dataNascita, String domicilio, Ruolo ruolo) {
    	this.nome = nome;
    	this.cognome = cognome;
    	this.username = username;
    	this.password = password;
    	this.dataNascita = dataNascita;
    	this.domicilio = domicilio;
    	this.ruolo = ruolo;
    }
    
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}  
    
}
