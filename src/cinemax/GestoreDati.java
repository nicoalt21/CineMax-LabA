package cinemax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GestoreDati {

    private Map<String, Utente> mappaUtenti;
    private Map<String, Proiezione> mappaProiezioni;
    private List<Prenotazione> listaPrenotazioni;

    private static final int CAPIENZA_MASSIMA = 200;
    private static final String PERCORSO_UTENTI = "data/utenti.csv";
    private static final String PERCORSO_PROIEZIONI = "data/proiezioni.csv";
    private static final String PERCORSO_PRENOTAZIONI = "data/prenotazioni.csv";

    public GestoreDati() {
        this.mappaUtenti = new HashMap<>();
        // TreeMap mantiene automaticamente le proiezioni in ordine cronologico basandosi sulla stringa dataOra
        this.mappaProiezioni = new TreeMap<>();
        this.listaPrenotazioni = new ArrayList<>();
    }

    // --- I/O FILE (Da implementare) ---

    public void caricaDati() {
        // Invocherà i tre metodi privati sottostanti
    }

    private void caricaUtenti() {}
    private void caricaProiezioni() {}
    private void caricaPrenotazioni() {}

    public void salvaDati() {
        // Invocherà i tre metodi privati sottostanti
    }

    private void salvaUtenti() {}
    private void salvaProiezioni() {}
    private void salvaPrenotazioni() {}

    // --- AUTENTICAZIONE E UTENTI ---

    public boolean registraCliente(Utente u) {
        // Controllo esistenza chiave (username) e aggiunta alla mappa
        return false;
    }

    public Utente autenticaUtente(String username, String passwordChiara) {
        // Ricerca in mappaUtenti e chiamata a u.verificaPassword()
        return null;
    }

    // --- GESTIONE PROIEZIONI ---

    public int calcolaPostiLiberi(String dataOra) {
        int postiOccupati = 0;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getProiezione().getDataOra().equals(dataOra)) {
                postiOccupati += p.getNumeroPosti();
            }
        }
        return CAPIENZA_MASSIMA - postiOccupati;
    }

    public boolean aggiungiProiezione(Proiezione p) {
        // Controllo sovrapposizione chiavi (dataOra) e inserimento
        return false;
    }

    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        // Controllo su listaPrenotazioni prima di modificare e aggiornamento chiave in mappa
        return false;
    }

    public boolean eliminaProiezione(String dataOra) {
        // Controllo su listaPrenotazioni prima di eliminare e rimozione dalla mappa
        return false;
    }

    // Usato Double invece di double per permettere parametri nulli dal menu in caso di assenza di filtri
    public List<Proiezione> cercaProiezione(String titolo, String genere, String dataInizio, String dataFine, Double prezzoMin, Double prezzoMax) {
        return new ArrayList<>();
    }

    public Proiezione ottieniProiezione(String dataOra) {
        // Ricerca diretta sulla mappa tramite la chiave primaria logica
        return null;
    }

    // --- GESTIONE PRENOTAZIONI ---

    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        // Verifica posti liberi, genera codice univoco, crea oggetto e aggiunge alla lista
        return null;
    }

    public List<Prenotazione> visualizzaPrenotazioni(Utente u) {
        // Filtra listaPrenotazioni per username
        return new ArrayList<>();
    }

    public boolean modificaPrenotazione(String codice, String nuovaDataOra) {
        // Controllo date (entrambe successive a oggi) e aggiornamento riferimento proiezione
        return false;
    }

    public boolean eliminaPrenotazione(String codice) {
        // Controllo data proiezione (deve essere futura) e rimozione da lista
        return false;
    }

    public List<Prenotazione> cercaPrenotazione(String codice, String nomeCliente, String titoloFilm, String dataInizio, String dataFine) {
        // Filtri di ricerca per il terminale biglietteria
        return new ArrayList<>();
    }
}