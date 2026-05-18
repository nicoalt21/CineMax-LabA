package cinemax.controller;

import cinemax.objects.Prenotazione;
import cinemax.objects.Proiezione;
import cinemax.objects.Utente;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe centrale per la gestione dei dati in memoria e della logica applicativa.
 * Coordina le operazioni su utenti, proiezioni e prenotazioni garantendo
 * il rispetto dei vincoli definiti dalle specifiche.
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
    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
     * Invariante: deve essere chiamato sempre dopo qualsiasi modifica alle proiezioni
     * per garantire la consistenza delle chiavi di join nel CSV delle prenotazioni.
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
        if (mappaUtenti.containsKey(u.getUsername())) return false;
        mappaUtenti.put(u.getUsername(), u);
        return true;
    }

    /**
     * Verifica le credenziali di accesso di un utente.
     *
     * @param username       L'identificativo dell'utente.
     * @param passwordChiara La password inserita in chiaro.
     * @return L'istanza dell'Utente se le credenziali sono corrette, null altrimenti.
     */
    public Utente autenticaUtente(String username, String passwordChiara) {
        Utente u = mappaUtenti.get(username);
        if (u != null && u.verificaPassword(passwordChiara)) return u;
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
     * Verifica se due intervalli temporali si sovrappongono nella sala.
     * Due proiezioni si sovrappongono se l'inizio di una cade prima della fine dell'altra
     * e viceversa.
     *
     * @param inizioA Inizio della proiezione A.
     * @param durataA Durata in minuti della proiezione A.
     * @param inizioB Inizio della proiezione B.
     * @param durataB Durata in minuti della proiezione B.
     * @return true se le due proiezioni si sovrappongono.
     */
    private boolean siSovrappongono(LocalDateTime inizioA, int durataA, LocalDateTime inizioB, int durataB) {
        LocalDateTime fineA = inizioA.plusMinutes(durataA);
        LocalDateTime fineB = inizioB.plusMinutes(durataB);
        return inizioA.isBefore(fineB) && inizioB.isBefore(fineA);
    }

    /**
     * Inserisce una nuova proiezione nel palinsesto.
     * Verifica che non vi siano sovrapposizioni temporali con altre proiezioni
     * esistenti, considerando la durata di entrambe.
     *
     * @param p L'oggetto Proiezione da aggiungere.
     * @return true se l'inserimento ha successo, false se la chiave esiste già o
     *         se la proiezione si sovrappone temporalmente con un'altra in sala.
     */
    public boolean aggiungiProiezione(Proiezione p) {
        if (mappaProiezioni.containsKey(p.getDataOra())) return false;

        LocalDateTime inizioNuova;
        try {
            inizioNuova = LocalDateTime.parse(p.getDataOra(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return false;
        }

        for (Proiezione esistente : mappaProiezioni.values()) {
            try {
                LocalDateTime inizioEsistente = LocalDateTime.parse(esistente.getDataOra(), FORMATO_DATA);
                if (siSovrappongono(inizioNuova, p.getDurata(), inizioEsistente, esistente.getDurata())) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                System.err.println("dataOra non valida in mappaProiezioni: " + esistente.getDataOra());
            }
        }

        mappaProiezioni.put(p.getDataOra(), p);
        return true;
    }

    /**
     * Modifica la data e l'ora di una proiezione esistente.
     * La modifica è consentita solo se non ci sono prenotazioni attive per quella proiezione
     * e se il nuovo orario non si sovrappone con altre proiezioni esistenti.
     *
     * @param dataOraAttuale La chiave di ricerca attuale.
     * @param nuovaDataOra   La nuova stringa data e ora da assegnare.
     * @return true se la modifica ha successo, false altrimenti.
     */
    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        if (!mappaProiezioni.containsKey(dataOraAttuale)) return false;
        if (mappaProiezioni.containsKey(nuovaDataOra)) return false;

        for (Prenotazione p : listaPrenotazioni) {
            if (p.getProiezione().getDataOra().equals(dataOraAttuale)) return false;
        }

        Proiezione proiezione = mappaProiezioni.get(dataOraAttuale);

        LocalDateTime inizioNuovo;
        try {
            inizioNuovo = LocalDateTime.parse(nuovaDataOra, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return false;
        }

        for (Proiezione altra : mappaProiezioni.values()) {
            if (altra.getDataOra().equals(dataOraAttuale)) continue;
            try {
                LocalDateTime inizioAltra = LocalDateTime.parse(altra.getDataOra(), FORMATO_DATA);
                if (siSovrappongono(inizioNuovo, proiezione.getDurata(), inizioAltra, altra.getDurata())) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                System.err.println("dataOra non valida in mappaProiezioni: " + altra.getDataOra());
            }
        }

        mappaProiezioni.remove(dataOraAttuale);
        proiezione.setDataOra(nuovaDataOra);
        mappaProiezioni.put(nuovaDataOra, proiezione);
        return true;
    }

    /**
     * Elimina una proiezione dal palinsesto.
     * La cancellazione è consentita solo se non ci sono prenotazioni attive associate.
     *
     * @param dataOra L'identificativo della proiezione da rimuovere.
     * @return true se l'eliminazione ha successo, false altrimenti.
     */
    public boolean eliminaProiezione(String dataOra) {
        if (!mappaProiezioni.containsKey(dataOra)) return false;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getProiezione().getDataOra().equals(dataOra)) return false;
        }
        mappaProiezioni.remove(dataOra);
        return true;
    }

    /**
     * Filtra le proiezioni in base a parametri di ricerca multipli.
     * I parametri null vengono ignorati.
     *
     * @param titolo     Titolo parziale o completo.
     * @param genere     Genere cinematografico.
     * @param dataInizio Limite temporale inferiore (formato yyyy-MM-dd).
     * @param dataFine   Limite temporale superiore (formato yyyy-MM-dd).
     * @param prezzoMin  Costo minimo del biglietto.
     * @param prezzoMax  Costo massimo del biglietto.
     * @return Lista delle proiezioni che soddisfano i criteri, mai null.
     * @throws DateTimeParseException Se dataInizio o dataFine non sono nel formato yyyy-MM-dd.
     */
    public List<Proiezione> cercaProiezione(String titolo, String genere, String dataInizio,
                                            String dataFine, Double prezzoMin, Double prezzoMax) {
        List<Proiezione> risultati = new ArrayList<>();

        LocalDateTime inizio = (dataInizio != null) ? LocalDate.parse(dataInizio).atStartOfDay() : null;
        LocalDateTime fine = (dataFine != null) ? LocalDate.parse(dataFine).atTime(23, 59, 59) : null;

        for (Proiezione p : mappaProiezioni.values()) {
            boolean corrisponde = true;

            if (titolo != null && !p.getTitolo().toLowerCase().contains(titolo.toLowerCase()))
                corrisponde = false;
            if (genere != null && !p.getGenere().equalsIgnoreCase(genere))
                corrisponde = false;

            try {
                LocalDateTime dataP = LocalDateTime.parse(p.getDataOra(), FORMATO_DATA);
                if (inizio != null && dataP.isBefore(inizio)) corrisponde = false;
                if (fine != null && dataP.isAfter(fine)) corrisponde = false;
            } catch (DateTimeParseException e) {
                System.err.println("dataOra non valida per proiezione: " + p.getDataOra());
                corrisponde = false;
            }

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
     * Recupera una singola prenotazione tramite il suo codice univoco.
     *
     * @param codiceUnivoco Il codice alfanumerico della prenotazione.
     * @return L'oggetto Prenotazione trovato o null se inesistente.
     */
    public Prenotazione ottieniPrenotazione(String codiceUnivoco) {
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCodiceUnivoco().equalsIgnoreCase(codiceUnivoco)) return p;
        }
        return null;
    }

    /**
     * Registra una nuova prenotazione scalando i posti disponibili.
     * La prenotazione è consentita solo se la proiezione è futura, il numero di posti
     * è positivo e non supera la disponibilità, e il cliente soddisfa l'età minima.
     *
     * @param u     L'utente acquirente.
     * @param p     La proiezione selezionata.
     * @param posti Il numero di posti da riservare.
     * @return Il codice alfanumerico della prenotazione o null se la prenotazione non è ammissibile.
     */
    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        if (posti <= 0 || calcolaPostiLiberi(p.getDataOra()) < posti) return null;

        try {
            LocalDateTime dataProiezione = LocalDateTime.parse(p.getDataOra(), FORMATO_DATA);
            if (!dataProiezione.isAfter(LocalDateTime.now())) return null;
        } catch (DateTimeParseException e) {
            return null;
        }

        int eta = u.calcolaEta();
        if (eta != -1 && eta < p.getEtaMinima()) return null;

        String codice = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        listaPrenotazioni.add(new Prenotazione(codice, u, p, posti));
        return codice;
    }

    /**
     * Restituisce lo storico delle prenotazioni di un singolo utente.
     *
     * @param u L'utente di cui visualizzare le prenotazioni.
     * @return Lista delle prenotazioni associate all'utente, mai null.
     */
    public List<Prenotazione> visualizzaPrenotazioni(Utente u) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCliente().getUsername().equals(u.getUsername())) risultato.add(p);
        }
        return risultato;
    }

    /**
     * Sposta una prenotazione su una diversa proiezione.
     * La modifica è consentita solo se sia la vecchia che la nuova data sono successive a oggi
     * e se sulla nuova proiezione ci sono abbastanza posti liberi.
     *
     * @param codice       Il codice univoco della prenotazione.
     * @param nuovaDataOra L'orario della nuova proiezione.
     * @return true se la modifica ha successo, false altrimenti.
     */
    public boolean modificaPrenotazione(String codice, String nuovaDataOra) {
        Prenotazione daModificare = null;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCodiceUnivoco().equals(codice)) { daModificare = p; break; }
        }
        if (daModificare == null) return false;

        try {
            LocalDateTime ora = LocalDateTime.now();
            LocalDateTime dataVecchia = LocalDateTime.parse(daModificare.getProiezione().getDataOra(), FORMATO_DATA);
            LocalDateTime dataNuova = LocalDateTime.parse(nuovaDataOra, FORMATO_DATA);
            if (!dataVecchia.isAfter(ora) || !dataNuova.isAfter(ora)) return false;
        } catch (DateTimeParseException e) {
            return false;
        }

        Proiezione nuovaProiezione = mappaProiezioni.get(nuovaDataOra);
        if (nuovaProiezione == null) return false;

        int postiLiberi = calcolaPostiLiberi(nuovaDataOra);
        if (daModificare.getProiezione().getDataOra().equals(nuovaDataOra)) return false;
        if (postiLiberi < daModificare.getNumeroPosti()) return false;

        daModificare.setProiezione(nuovaProiezione);
        return true;
    }

    /**
     * Cancella una prenotazione dal sistema.
     * La cancellazione è consentita solo se la data di proiezione è successiva a oggi.
     * (Interpretazione coerente con modificaPrenotazione, in luogo del refuso "precedente"
     * presente nelle specifiche originali; vedi manuale tecnico.)
     *
     * @param codice Il codice della prenotazione da eliminare.
     * @return true se l'eliminazione va a buon fine, false altrimenti.
     */
    public boolean eliminaPrenotazione(String codice) {
        LocalDateTime ora = LocalDateTime.now();
        for (int i = 0; i < listaPrenotazioni.size(); i++) {
            Prenotazione p = listaPrenotazioni.get(i);
            if (p.getCodiceUnivoco().equals(codice)) {
                try {
                    LocalDateTime dataProiezione = LocalDateTime.parse(p.getProiezione().getDataOra(), FORMATO_DATA);
                    if (!dataProiezione.isAfter(ora)) return false;
                } catch (DateTimeParseException e) {
                    return false;
                }
                listaPrenotazioni.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Ricerca prenotazioni nel sistema tramite filtri multipli.
     * I parametri null vengono ignorati.
     *
     * @param codice      Identificativo esatto della prenotazione.
     * @param nomeCliente Nome o cognome (parziale) del cliente.
     * @param titoloFilm  Titolo (parziale) del film.
     * @param dataInizio  Limite temporale inferiore (formato yyyy-MM-dd).
     * @param dataFine    Limite temporale superiore (formato yyyy-MM-dd).
     * @return Lista delle prenotazioni che soddisfano i criteri, mai null.
     * @throws DateTimeParseException Se dataInizio o dataFine non sono nel formato yyyy-MM-dd.
     */
    public List<Prenotazione> cercaPrenotazione(String codice, String nomeCliente,
                                                String titoloFilm, String dataInizio, String dataFine) {
        List<Prenotazione> risultati = new ArrayList<>();

        LocalDateTime inizio = (dataInizio != null) ? LocalDate.parse(dataInizio).atStartOfDay() : null;
        LocalDateTime fine = (dataFine != null) ? LocalDate.parse(dataFine).atTime(23, 59, 59) : null;

        for (Prenotazione p : listaPrenotazioni) {
            boolean corrisponde = true;

            if (codice != null && !p.getCodiceUnivoco().equalsIgnoreCase(codice))
                corrisponde = false;

            if (nomeCliente != null) {
                String nomeCompleto = p.getCliente().getNome() + " " + p.getCliente().getCognome();
                if (!nomeCompleto.toLowerCase().contains(nomeCliente.toLowerCase()))
                    corrisponde = false;
            }

            if (titoloFilm != null && !p.getProiezione().getTitolo().toLowerCase()
                    .contains(titoloFilm.toLowerCase()))
                corrisponde = false;

            try {
                LocalDateTime dataP = LocalDateTime.parse(p.getProiezione().getDataOra(), FORMATO_DATA);
                if (inizio != null && dataP.isBefore(inizio)) corrisponde = false;
                if (fine != null && dataP.isAfter(fine)) corrisponde = false;
            } catch (DateTimeParseException e) {
                System.err.println("dataOra non valida per prenotazione: " + p.getCodiceUnivoco());
                corrisponde = false;
            }

            if (corrisponde) risultati.add(p);
        }
        return risultati;
    }

    /**
     * Restituisce tutte le prenotazioni con proiezione nella data odierna.
     *
     * @return Lista delle prenotazioni odierne, mai null.
     */
    public List<Prenotazione> ottieniPrenotazioniOggi() {
        List<Prenotazione> risultati = new ArrayList<>();
        LocalDateTime inizioGiornata = LocalDate.now().atStartOfDay();
        LocalDateTime fineGiornata = LocalDate.now().atTime(23, 59, 59);

        for (Prenotazione p : listaPrenotazioni) {
            try {
                LocalDateTime data = LocalDateTime.parse(p.getProiezione().getDataOra(), FORMATO_DATA);
                if (!data.isBefore(inizioGiornata) && !data.isAfter(fineGiornata)) {
                    risultati.add(p);
                }
            } catch (DateTimeParseException e) {
                System.err.println("dataOra non valida in ottieniPrenotazioniOggi: " + p.getProiezione().getDataOra());
            }
        }
        return risultati;
    }
}