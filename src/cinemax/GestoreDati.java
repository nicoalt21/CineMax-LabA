package cinemax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestoreDati {

    private Map<String, Utente> mappaUtenti;
    private Map<String, Proiezione> mappaProiezioni;
    private List<Prenotazione> listaPrenotazioni;

    private static final int CAPIENZA_MASSIMA = 200;
    //private static final String PERCORSO_UTENTI = ;
    //private static final String PATH_PROIEZIONI = ;
    //private static final String PATH_PRENOTAZIONI = ;

    public GestoreDati() {

        this.mappaUtenti = new HashMap<>();
        //this.mappaProiezioni = new HashMap<>();
        //this.listaPrenotazioni = new ArrayList<>();
    }

    public void caricaDati() {

    }

    private void caricaUtenti() {

    }

    private void caricaProiezioni() {

    }

    private void caricaPrenotazioni() {

    }

    public void salvaDati() {

    }

    private void salvaUtenti() {

    }

    private void salvaProiezioni() {

    }

    private void salvaPrenotazioni() {

    }


    public boolean registraCliente(Utente u) {
        // controllo esistenza e aggiunta alla mappa
        return false;
    }

    //qua lho messo ma bisgona vedere come gestiamo l'hashing per la pw

    public Utente autenticaUtente(String username, String passwordChiara) {
        // ricerca per username e verifica hash password

        // MATO: Se l'utente NON viene autenticato mi aspetto un null di output, in caso di cambiamenti tenere in considerazione, l'if nel metodo login della classe MenuPrinciaple.java
        return null;
    }

    /* MATO: Questo metodo lo inserirei in Proiezione, invocandolo con Proiezione.getPostiLiberi();
    public int getPostiLiberi(String dataOra) {
        // Calcola posti occupati iterando sulle prenotazioni e sottrai a CAPIENZA_MASSIMA
        return 0;
    }*/

   public boolean aggiungiProiezione(Proiezione p) {
        // controllo sovrapposizione date/orari e aggiunt
       return false;
    }

    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        //  controllo su prenotazioni esistenti prima di modificare
        return false;
    }

    public boolean eliminaProiezione(String dataOra) {
        //  controllo su prenotazioni esistenti prima di eliminare
        return false;
    }

    public List<Proiezione> cercaProiezione(String titolo, String genere, String dataInizio, String dataFine, double prezzoMax, double prezzoMin) {
        // filtri di ricerca e i parametri testuali possono essere null o vuoti
        return new ArrayList<>();
    }

    /* le Proiezionni, secondo me, conviene gestirle tramite una Lista non tramite una Map
    public Proiezione ottieniProiezione(String id) {
        // se esiste, restituisce tutte le informazioni su una Proiezione, trovata in base alla PK id

        for (Proiezione p : listaProiezioni) {
            if (p.getId() == Integer.parseInt(id) || p.getTitolo().equalsIgnoreCaeqse(id)) {
                return p;
            }
            return null;
        }

        return null;
    }
     */

    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        // Verifica posti liberi, genera id e aggiunge alla lista
       return null;
    }

    public List<Prenotazione> visualizzaPrenotazioni(Utente u) {
        // Filtra listaPrenotazioni per username
        return new ArrayList<>();
    }

    public boolean modificaPrenotazione(String codice, String nuovaDataOra) {
        // controllo date (entrambe successive a oggi) e aggiornamento
        return false;
    }

    public boolean eliminaPrenotazione(String codice) {
        // controllo data proiezione (deve essere futura) e rimozione
        return false;
    }

    public List<Prenotazione> cercaPrenotazione(String codice, String nomeCliente, String titoloFilm, String dataInizio, String dataFine) {
        // filtri di ricerca per il bigliettaio
       return new ArrayList<>();
    }
}