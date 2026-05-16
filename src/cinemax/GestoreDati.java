package cinemax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe centrale per la gestione dei dati in memoria e della logica applicativa.
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

    /**
     * Inizializza le strutture dati in memoria per utenti, proiezioni e prenotazioni.
     */
    public GestoreDati() {
        this.mappaUtenti = new HashMap<>();
        this.mappaProiezioni = new TreeMap<>();
        this.listaPrenotazioni = new ArrayList<>();
    }

    /**
     * Delega al GestoreCSV il caricamento dei dati dai file fisici alle collezioni in RAM.
     */
    public void caricaDati() {
        GestoreCSV.caricaUtenti(mappaUtenti);
        GestoreCSV.caricaProiezioni(mappaProiezioni);
        GestoreCSV.caricaPrenotazioni(listaPrenotazioni, mappaUtenti, mappaProiezioni);
    }

    /**
     * Delega al GestoreCSV il salvataggio dei dati correnti dalle collezioni in RAM ai file fisici.
     */
    public void salvaDati() {
        GestoreCSV.salvaUtenti(mappaUtenti);
        GestoreCSV.salvaProiezioni(mappaProiezioni);
        GestoreCSV.salvaPrenotazioni(listaPrenotazioni);
    }

    /**
     * Inserisce un nuovo utente nel sistema.
     *
     * @param u L'oggetto Utente da registrare.
     * @return true se l'inserimento ha successo, false se l'username è già presente.
     */
    public boolean registraCliente(Utente u) {
        if(mappaUtenti.containsKey(u.getUsername())) {
            return false;
        }
        mappaUtenti.put(u.getUsername(), u);
        return true;
    }

    /**
     * Verifica le credenziali di accesso di un utente.
     *
     * @param username L'identificativo dell'utente.
     * @param passwordChiara La password inserita in chiaro.
     * @return L'istanza dell'Utente se le credenziali sono corrette, null altrimenti.
     */
    public Utente autenticaUtente(String username, String passwordChiara) {
        Utente u = mappaUtenti.get(username);
        if (u != null && u.verificaPassword(passwordChiara)) {
            return u;
        }
        return null;
    }

    /**
     * Calcola il numero di posti ancora disponibili per una determinata proiezione.
     *
     * @param dataOra Identificativo univoco della proiezione.
     * @return Il numero di posti liberi.
     */
    public int calcolaPostiLiberi(String dataOra) {
        int postiOccupati = 0;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getProiezione().getDataOra().equals(dataOra)) {
                postiOccupati += p.getNumeroPosti();
            }
        }
        return CAPIENZA_MASSIMA - postiOccupati;
    }

    /**
     * Inserisce una nuova proiezione nel palinsesto.
     *
     * @param p L'oggetto Proiezione da aggiungere.
     * @return true se l'inserimento ha successo, false se esiste già una proiezione per quella data e ora.
     */
    public boolean aggiungiProiezione(Proiezione p) {
        if (mappaProiezioni.containsKey(p.getDataOra())) {
            return false;
        }
        mappaProiezioni.put(p.getDataOra(), p);
        return true;
    }

    /**
     * Modifica la data e l'ora di una proiezione esistente.
     *
     * @param dataOraAttuale La chiave di ricerca attuale.
     * @param nuovaDataOra La nuova stringa data e ora da assegnare.
     * @return true se la modifica ha successo, false se la proiezione originale non esiste o il nuovo orario è già occupato.
     */
    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        if (!mappaProiezioni.containsKey(dataOraAttuale)) {
            return false;
        }
        if (mappaProiezioni.containsKey(nuovaDataOra)) {
            return false;
        }

        Proiezione p = mappaProiezioni.remove(dataOraAttuale);
        p.setDataOra(nuovaDataOra);
        mappaProiezioni.put(nuovaDataOra, p);

        return true;
    }

    /**
     * Elimina una proiezione dal palinsesto.
     *
     * @param dataOra L'identificativo della proiezione da rimuovere.
     * @return true se l'eliminazione ha successo, false se non esiste o se ci sono prenotazioni attive associate.
     */
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

    /**
     * Filtra le proiezioni in base a parametri di ricerca multipli.
     * Se un parametro è null, il filtro corrispondente viene ignorato.
     *
     * @param titolo Titolo parziale o completo.
     * @param genere Genere cinematografico.
     * @param dataInizio Limite temporale inferiore.
     * @param dataFine Limite temporale superiore.
     * @param prezzoMin Costo minimo del biglietto.
     * @param prezzoMax Costo massimo del biglietto.
     * @return Lista delle proiezioni che soddisfano i criteri.
     */
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

    /**
     * Recupera una singola proiezione tramite la sua chiave primaria.
     *
     * @param dataOra La chiave di ricerca.
     * @return L'oggetto Proiezione trovato o null se inesistente.
     */
    public Proiezione ottieniProiezione(String dataOra) {
        return mappaProiezioni.get(dataOra);
    }

    /**
     * Registra una nuova prenotazione scalando i posti disponibili.
     *
     * @param u L'utente acquirente.
     * @param p La proiezione selezionata.
     * @param posti Il numero di posti da riservare.
     * @return Il codice alfanumerico della prenotazione o null se i posti sono insufficienti o l'input errato.
     */
    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        if (posti <= 0 || calcolaPostiLiberi(p.getDataOra()) < posti) {
            return null;
        }
        String codice = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Prenotazione prenotazione = new Prenotazione(codice, u, p, posti);
        listaPrenotazioni.add(prenotazione);
        return codice;
    }

    /**
     * Restituisce lo storico delle prenotazioni di un singolo utente.
     *
     * @param u L'utente di cui visualizzare le prenotazioni.
     * @return Lista delle prenotazioni associate all'utente.
     */
    public List<Prenotazione> visualizzaPrenotazioni(Utente u) {
        List<Prenotazione> prenotazioniUtente = new ArrayList<>();
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCliente().getUsername().equals(u.getUsername())) {
                prenotazioniUtente.add(p);
            }
        }
        return prenotazioniUtente;
    }

    /**
     * Sposta una prenotazione su una diversa proiezione, previa verifica della disponibilità.
     *
     * @param codice Il codice univoco della prenotazione.
     * @param nuovaDataOra L'orario della nuova proiezione.
     * @return true se la modifica ha successo, false in caso di errori o indisponibilità di posti.
     */
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

    /**
     * Cancella una prenotazione dal sistema.
     *
     * @param codice Il codice della prenotazione da eliminare.
     * @return true se l'eliminazione va a buon fine, false se il codice non esiste.
     */
    public boolean eliminaPrenotazione(String codice) {
        for (int i = 0; i < listaPrenotazioni.size(); i++) {
            if (listaPrenotazioni.get(i).getCodiceUnivoco().equals(codice)) {
                listaPrenotazioni.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Ricerca prenotazioni nel sistema tramite filtri multipli.
     *
     * @param codice Identificativo esatto della prenotazione.
     * @param nomeCliente Nome o cognome (parziale) del cliente.
     * @param titoloFilm Titolo (parziale) del film.
     * @param dataInizio Limite temporale inferiore.
     * @param dataFine Limite temporale superiore.
     * @return Lista delle prenotazioni che soddisfano i criteri.
     */
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