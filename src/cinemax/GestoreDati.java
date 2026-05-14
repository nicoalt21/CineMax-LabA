package cinemax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe centrale per la gestione dei dati in memoria e della logica.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class GestoreDati {

    private Map<String, Utente> mappaUtenti;
    private Map<String, Proiezione> mappaProiezioni;
    private List<Prenotazione> listaPrenotazioni;

    private static final int CAPIENZA_MASSIMA = 200;

    public GestoreDati() {
        this.mappaUtenti = new HashMap<>();
        this.mappaProiezioni = new TreeMap<>();
        this.listaPrenotazioni = new ArrayList<>();
    }

    // Gestione dei file I/O

    public void caricaDati() {
        GestoreCSV.caricaUtenti(mappaUtenti);
        GestoreCSV.caricaProiezioni(mappaProiezioni);
        // Per le prenotazioni passiamo le altre mappe per ricreare i riferimenti
        GestoreCSV.caricaPrenotazioni(listaPrenotazioni, mappaUtenti, mappaProiezioni);
    }

    public void salvaDati() {
        GestoreCSV.salvaUtenti(mappaUtenti);
        GestoreCSV.salvaProiezioni(mappaProiezioni);
        GestoreCSV.salvaPrenotazioni(listaPrenotazioni);
    }

    // Autenticazione e Utenti
    public boolean registraNuovoCliente(String nome, String cognome, String username, String passwordCifrata, String dataNascita, String domicilio) {
        if (mappaUtenti.containsKey(username)) {
            return false;
        }
        Utente nuovoUtente = new Utente(nome, cognome, username, passwordCifrata, dataNascita, domicilio, Ruolo.CLIENTE);

        mappaUtenti.put(username, nuovoUtente);
        return true;
    }

    public boolean registraCliente(Utente u) {
        if(mappaUtenti.containsKey(u.getUsername())) {
            return false;
        }
        mappaUtenti.put(u.getUsername(), u);
        return true;
    }

    public Utente autenticaUtente(String username, String passwordChiara) {
        Utente u = mappaUtenti.get(username); // NB. assicurarsi che all'interno di Utente.verificaPassword() ci sia una chiamata a Cifrario.cifraPassword(passwordChiara) per comparare gli hash
        if (u != null && u.verificaPassword(passwordChiara)) {
            return u;
        }
        return null;
    }

    // Gestione Proiezioni
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
        if (mappaProiezioni.containsKey(p.getDataOra())) {
            return false;
        }
        mappaProiezioni.put(p.getDataOra(), p);
        return true;
    }

    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        // Logica da implementare come indicato nel tuo snippet
        return false;
    }

    public boolean eliminaProiezione(String dataOra) {
        if (!mappaProiezioni.containsKey(dataOra)) {
            return false;
        }
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getProiezione().getDataOra().equals(dataOra)) {
                return false;
            }
        }
        mappaProiezioni.remove(dataOra);
        return true;
    }

    public List<Proiezione> cercaProiezione(String titolo, String genere, String dataInizio, String dataFine, Double prezzoMin, Double prezzoMax) {
        List<Proiezione> risultati = new ArrayList<>();
        for (Proiezione p : mappaProiezioni.values()) {
            boolean corrisponde = true;

            if (titolo != null && !p.getTitolo().toLowerCase().contains(titolo.toLowerCase())) corrisponde = false;
            if (genere != null && !p.getGenere().equalsIgnoreCase(genere)) corrisponde = false;
            if (dataInizio != null && p.getDataOra().compareTo(dataInizio) < 0) corrisponde = false;
            if (dataFine != null && p.getDataOra().compareTo(dataFine + " 23:59:59") > 0) corrisponde = false;
            if (prezzoMin != null && p.getPrezzo() < prezzoMin) corrisponde = false;
            if (prezzoMax != null && p.getPrezzo() > prezzoMax) corrisponde = false;

            if (corrisponde) risultati.add(p);
        }
        return risultati;
    }

    public Proiezione ottieniProiezione(String dataOra) {
        return mappaProiezioni.get(dataOra);
    }

    // --- GESTIONE PRENOTAZIONI ---

    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        if (posti <= 0 || calcolaPostiLiberi(p.getDataOra()) < posti) {
            return null;
        }
        String codice = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Prenotazione prenotazione = new Prenotazione(codice, u, p, posti);
        listaPrenotazioni.add(prenotazione);
        return codice;
    }

    public List<Prenotazione> visualizzaPrenotazioni(Utente u) {
        List<Prenotazione> prenotazioniUtente = new ArrayList<>();
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCliente().getUsername().equals(u.getUsername())) {
                prenotazioniUtente.add(p);
            }
        }
        return prenotazioniUtente;
    }

    public boolean modificaPrenotazione(String codice, String nuovaDataOra) {
        Prenotazione daModificare = null;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCodiceUnivoco().equals(codice)) {
                daModificare = p;
                break;
            }
        }

        if (daModificare == null) return false;

        Proiezione nuovaProiezione = mappaProiezioni.get(nuovaDataOra);
        if (nuovaProiezione == null || calcolaPostiLiberi(nuovaDataOra) < daModificare.getNumeroPosti()) {
            return false;
        }

        daModificare.setProiezione(nuovaProiezione);
        return true;
    }

    public boolean eliminaPrenotazione(String codice) {
        for (int i = 0; i < listaPrenotazioni.size(); i++) {
            if (listaPrenotazioni.get(i).getCodiceUnivoco().equals(codice)) {
                listaPrenotazioni.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<Prenotazione> cercaPrenotazione(String codice, String nomeCliente, String titoloFilm, String dataInizio, String dataFine) {
        List<Prenotazione> risultati = new ArrayList<>();
        for (Prenotazione p : listaPrenotazioni) {
            boolean corrisponde = true;

            if (codice != null && !p.getCodiceUnivoco().equalsIgnoreCase(codice)) corrisponde = false;

            if (nomeCliente != null) {
                String nomeCompleto = p.getCliente().getNome() + " " + p.getCliente().getCognome();
                if (!nomeCompleto.toLowerCase().contains(nomeCliente.toLowerCase())) corrisponde = false;
            }

            if (titoloFilm != null && !p.getProiezione().getTitolo().toLowerCase().contains(titoloFilm.toLowerCase())) corrisponde = false;
            if (dataInizio != null && p.getProiezione().getDataOra().compareTo(dataInizio) < 0) corrisponde = false;
            if (dataFine != null && p.getProiezione().getDataOra().compareTo(dataFine + " 23:59:59") > 0) corrisponde = false;

            if (corrisponde) risultati.add(p);
        }
        return risultati;
    }
}