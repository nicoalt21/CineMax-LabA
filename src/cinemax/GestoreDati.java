package cinemax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe centrale per la gestione dei dati del sistema CineMax.
 * Si occupa del caricamento, salvataggio e manipolazione in memoria delle
 * informazioni relative a utenti, proiezioni e prenotazioni.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class GestoreDati {

    /** Mappa degli utenti registrati, indicizzati per username. */
    private Map<String, Utente> mappaUtenti;
    /** Mappa delle proiezioni, ordinata cronologicamente per data e ora. */
    private Map<String, Proiezione> mappaProiezioni;
    /** Lista di tutte le prenotazioni effettuate nel sistema. */
    private List<Prenotazione> listaPrenotazioni;

    private static final int CAPIENZA_MASSIMA = 200;
    private static final String PERCORSO_UTENTI = "data/utenti.csv";
    private static final String PERCORSO_PROIEZIONI = "data/proiezioni.csv";
    private static final String PERCORSO_PRENOTAZIONI = "data/prenotazioni.csv";

    /**
     * Inizializza il gestore dati creando le strutture per la memorizzazione in RAM.
     */
    public GestoreDati() {
        this.mappaUtenti = new HashMap<>();
        this.mappaProiezioni = new TreeMap<>();
        this.listaPrenotazioni = new ArrayList<>();
    }

    // --- I/O FILE ---

    /**
     * Carica tutti i dati dai file CSV nelle strutture in memoria.
     * L'ordine di caricamento (Utenti -> Proiezioni -> Prenotazioni) è fondamentale
     * per mantenere l'integrità dei riferimenti.
     */
    public void caricaDati() {
        caricaUtenti();
        caricaProiezioni();
        caricaPrenotazioni();
    }

    /**
     * Legge gli utenti dal file CSV.
     * Le password vengono caricate come hash senza subire nuovamente la cifratura.
     */
    private void caricaUtenti() {
        try (BufferedReader lettore = new BufferedReader(new FileReader(PERCORSO_UTENTI))) {
            String riga;
            boolean isIntestazione = true;

            while ((riga = lettore.readLine()) != null) {
                if (isIntestazione) {
                    isIntestazione = false;
                    continue;
                }

                String[] campi = riga.split(",");
                if (campi.length >= 7) {
                    Utente u = new Utente(campi[0], campi[1], campi[2], campi[3], campi[4], campi[5], Ruolo.valueOf(campi[6].toUpperCase()), false);
                    mappaUtenti.put(u.getUsername(), u);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in caricaUtenti: " + e.getMessage());
        }
    }

    /**
     * Legge le proiezioni dal file CSV e le inserisce nella mappa ordinata.
     */
    private void caricaProiezioni() {
        try (BufferedReader lettore = new BufferedReader(new FileReader(PERCORSO_PROIEZIONI))) {
            String riga;
            boolean isIntestazione = true;

            while ((riga = lettore.readLine()) != null) {
                if (isIntestazione) {
                    isIntestazione = false;
                    continue;
                }

                String[] campi = riga.split(",");
                if (campi.length >= 8) {
                    Proiezione p = new Proiezione(campi[0], campi[1], campi[2], campi[3],
                            Integer.parseInt(campi[4]), Integer.parseInt(campi[5]),
                            Integer.parseInt(campi[6]), Double.parseDouble(campi[7]));
                    mappaProiezioni.put(p.getDataOra(), p);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Errore in caricaProiezioni: " + e.getMessage());
        }
    }

    /**
     * Legge le prenotazioni dal file CSV.
     * Collega ogni prenotazione alle istanze esistenti di Utente e Proiezione.
     */
    private void caricaPrenotazioni() {
        try (BufferedReader lettore = new BufferedReader(new FileReader(PERCORSO_PRENOTAZIONI))) {
            String riga;
            boolean isIntestazione = true;

            while ((riga = lettore.readLine()) != null) {
                if (isIntestazione) {
                    isIntestazione = false;
                    continue;
                }

                String[] campi = riga.split(",");
                if (campi.length >= 4) {
                    String codice = campi[0];
                    String username = campi[1];
                    String dataOraProiezione = campi[2];
                    int numeroPosti = Integer.parseInt(campi[3]);

                    Utente cliente = mappaUtenti.get(username);
                    Proiezione proiezione = mappaProiezioni.get(dataOraProiezione);

                    if (cliente != null && proiezione != null) {
                        Prenotazione pren = new Prenotazione(codice, cliente, proiezione, numeroPosti);
                        listaPrenotazioni.add(pren);
                    } else {
                        System.out.println("Errore integrità dati: impossibile caricare prenotazione " + codice);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Errore in caricaPrenotazioni: " + e.getMessage());
        }
    }

    /**
     * Salva lo stato attuale delle strutture dati sui file CSV.
     */
    public void salvaDati() {
        salvaUtenti();
        salvaProiezioni();
        salvaPrenotazioni();
    }

    /**
     * Scrive gli utenti presenti in memoria nel relativo file CSV.
     */
    private void salvaUtenti() {
        try (BufferedWriter scrittore = new BufferedWriter(new FileWriter(PERCORSO_UTENTI))) {
            scrittore.write("nome,cognome,username,password,data_nascita,domicilio,ruolo\n");
            for (Utente u : mappaUtenti.values()) {
                String riga = u.getNome() + "," + u.getCognome() + "," + u.getUsername() + "," +
                        u.getPasswordCifrata() + "," + u.getDataNascita() + "," +
                        u.getDomicilio() + "," + u.getRuolo().name();
                scrittore.write(riga + "\n");
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaUtenti: " + e.getMessage());
        }
    }

    /**
     * Scrive le proiezioni presenti in memoria nel relativo file CSV.
     */
    private void salvaProiezioni() {
        try (BufferedWriter scrittore = new BufferedWriter(new FileWriter(PERCORSO_PROIEZIONI))) {
            scrittore.write("data_ora,titolo,genere,regista,anno,durata,eta_minima,prezzo\n");
            for (Proiezione p : mappaProiezioni.values()) {
                String riga = p.getDataOra() + "," + p.getTitolo() + "," + p.getGenere() + "," +
                        p.getRegista() + "," + p.getAnno() + "," + p.getDurata() + "," +
                        p.getEtaMinima() + "," + p.getPrezzo();
                scrittore.write(riga + "\n");
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaProiezioni: " + e.getMessage());
        }
    }

    /**
     * Scrive le prenotazioni presenti in memoria nel relativo file CSV.
     */
    private void salvaPrenotazioni() {
        try (BufferedWriter scrittore = new BufferedWriter(new FileWriter(PERCORSO_PRENOTAZIONI))) {
            scrittore.write("codice_univoco,username_cliente,data_ora_proiezione,numero_posti\n");
            for (Prenotazione p : listaPrenotazioni) {
                String riga = p.getCodiceUnivoco() + "," + p.getCliente().getUsername() + "," +
                        p.getProiezione().getDataOra() + "," + p.getNumeroPosti();
                scrittore.write(riga + "\n");
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaPrenotazioni: " + e.getMessage());
        }
    }

    // --- AUTENTICAZIONE E UTENTI ---

    /**
     * Registra un nuovo cliente nel sistema.
     * Verifica preventivamente che l'username scelto non sia già presente
     * nella struttura dati per evitare collisioni e garantire l'univocità delle credenziali.
     *
     * @param u L'oggetto Utente da registrare, con la password già sottoposta ad hashing.
     * @return true se la registrazione ha successo, false se l'username è già in uso.
     */
    public boolean registraCliente(Utente u) {
        if(mappaUtenti.containsKey(u.getUsername())) {
            return false;
        }
        mappaUtenti.put(u.getUsername(), u);
        return true;
    }

    /**
     * Autentica un utente nel sistema verificando le sue credenziali.
     * Ricerca l'utente tramite l'username e delega all'istanza trovata
     * il controllo di validità della password in chiaro.
     *
     * @param username L'identificativo univoco dell'utente.
     * @param passwordChiara La password inserita dall'utente nel form di login.
     * @return L'oggetto Utente se l'autenticazione ha successo, null in caso di credenziali non valide.
     */
    public Utente autenticaUtente(String username, String passwordChiara) {
        Utente u = mappaUtenti.get(username);
        if (u != null && u.verificaPassword(passwordChiara)) {
            return u;
        }
        return null;
    }

    // --- GESTIONE PROIEZIONI ---

    /**
     * Calcola dinamicamente il numero di posti ancora disponibili per una specifica proiezione.
     * Iterando sulla lista delle prenotazioni globali, somma i posti già assegnati
     * e li sottrae alla capacità massima della sala.
     *
     * @param dataOra La stringa che identifica univocamente la proiezione (chiave primaria).
     * @return Il numero di posti liberi rimasti.
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
     * Inserisce una nuova proiezione nel sistema.
     * L'inserimento avviene solo se non esiste già una proiezione associata alla stessa data e ora.
     *
     * @param p la proiezione da inserire
     * @return true se l'inserimento è avvenuto con successo, false altrimenti
     */

    public boolean aggiungiProiezione(Proiezione p) {
        if (mappaProiezioni.containsKey(p.getDataOra())) {
        	return false;
        }
        
        mappaProiezioni.put(p.getDataOra(), p);
        return true;
    }

    public boolean modificaProiezione(String dataOraAttuale, String nuovaDataOra) {
        // Controllo su listaPrenotazioni prima di modificare e aggiornamento chiave in mappa
        return false;
    }
    
    /**
     * Elimina una proiezione dal sistema.
     * L'eliminazione è consentita solo se non esistono prenotazioni associate alla proiezione.
     *
     * @param dataOra la data e ora della proiezione da eliminare
     * @return true se la proiezione è stata eliminata, false altrimenti
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
     * Filtra le proiezioni in base ai parametri forniti.
     * Se un parametro è null, il filtro relativo viene ignorato.
     */
    public List<Proiezione> cercaProiezione(String titolo, String genere, String dataInizio, String dataFine, Double prezzoMin, Double prezzoMax) {
        List<Proiezione> risultati = new ArrayList<>();

        for (Proiezione p : mappaProiezioni.values()) {
            boolean corrisponde = true;

            if (titolo != null && !p.getTitolo().toLowerCase().contains(titolo.toLowerCase())) {
                corrisponde = false;
            }
            if (genere != null && !p.getGenere().equalsIgnoreCase(genere)) {
                corrisponde = false;
            }
            if (dataInizio != null && p.getDataOra().compareTo(dataInizio) < 0) {
                corrisponde = false;
            }
            // Aggiungiamo il suffisso per coprire l'intera giornata della data di fine
            if (dataFine != null && p.getDataOra().compareTo(dataFine + " 23:59:59") > 0) {
                corrisponde = false;
            }
            if (prezzoMin != null && p.getPrezzo() < prezzoMin) {
                corrisponde = false;
            }
            if (prezzoMax != null && p.getPrezzo() > prezzoMax) {
                corrisponde = false;
            }

            if (corrisponde) {
                risultati.add(p);
            }
        }
        return risultati;
    }

    /**
     * Recupera una proiezione specifica tramite la sua chiave univoca.
     * * @param dataOra La stringa data_ora da cercare.
     * @return L'oggetto Proiezione trovato o null se inesistente.
     */
    public Proiezione ottieniProiezione(String dataOra) {
        return mappaProiezioni.get(dataOra);
    }

    // --- GESTIONE PRENOTAZIONI ---

    /**
     * Crea una nuova prenotazione nel sistema.
     * Verifica la disponibilità dei posti e genera un codice identificativo univoco.
     *
     * @param u L'utente che effettua l'acquisto.
     * @param p La proiezione desiderata.
     * @param posti Il numero di biglietti da riservare.
     * @return Il codice alfanumerico della prenotazione se avvenuta con successo, null in caso di posti insufficienti o input non validi.
     */
    public String creaPrenotazione(Utente u, Proiezione p, int posti) {
        if (posti <= 0) {
            return null;
        }

        if (calcolaPostiLiberi(p.getDataOra()) < posti) {
            return null;
        }

        String codice = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Prenotazione prenotazione = new Prenotazione(codice, u, p, posti);
        listaPrenotazioni.add(prenotazione);

        return codice;
    }
    
    /**
     * Restituisce l'elenco delle prenotazioni appartenenti a un determinato utente.
     * Il metodo scorre la lista globale delle prenotazioni e seleziona
     * solamente quelle associate all'username del cliente specificato.
     *
     * @param u l'utente di cui visualizzare le prenotazioni
     * @return lista delle prenotazioni dell'utente
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
     * Modifica l'orario di una prenotazione esistente spostandola su una nuova proiezione.
     *
     * @param codice L'identificativo univoco della prenotazione da alterare.
     * @param nuovaDataOra La data e ora della nuova proiezione desiderata.
     * @return true se la modifica ha successo, false se la prenotazione o la nuova proiezione non esistono, oppure in caso di capienza insufficiente.
     */
    public boolean modificaPrenotazione(String codice, String nuovaDataOra) {
        Prenotazione daModificare = null;
        for (Prenotazione p : listaPrenotazioni) {
            if (p.getCodiceUnivoco().equals(codice)) {
                daModificare = p;
                break;
            }
        }

        if (daModificare == null) {
            return false;
        }

        Proiezione nuovaProiezione = mappaProiezioni.get(nuovaDataOra);
        if (nuovaProiezione == null) {
            return false;
        }

        if (calcolaPostiLiberi(nuovaDataOra) < daModificare.getNumeroPosti()) {
            return false;
        }

        daModificare.setProiezione(nuovaProiezione);
        return true;
    }

    /**
     * Elimina fisicamente una prenotazione dal sistema.
     *
     * @param codice L'identificativo alfanumerico della prenotazione da rimuovere.
     * @return true se la cancellazione è avvenuta correttamente, false se il codice non è stato trovato nella lista.
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
     * Filtra le prenotazioni globali applicando criteri multipli a cascata.
     * Se un parametro viene passato come null, il relativo filtro viene ignorato.
     *
     * @param codice Filtro esatto sul codice della prenotazione.
     * @param nomeCliente Filtro parziale (case-insensitive) sulla concatenazione di nome e cognome.
     * @param titoloFilm Filtro parziale (case-insensitive) sul titolo della proiezione associata.
     * @param dataInizio Limite inferiore temporale per la ricerca.
     * @param dataFine Limite superiore temporale (inclusa l'intera giornata).
     * @return Una lista contenente esclusivamente le prenotazioni che soddisfano tutti i criteri impostati.
     */

    public List<Prenotazione> cercaPrenotazione(String codice, String nomeCliente, String titoloFilm, String dataInizio, String dataFine) {
        List<Prenotazione> risultati = new ArrayList<>();

        for (Prenotazione p : listaPrenotazioni) {
            boolean corrisponde = true;

            if (codice != null && !p.getCodiceUnivoco().equalsIgnoreCase(codice)) {
                corrisponde = false;
            }

            if (nomeCliente != null) {
                String nomeCompleto = p.getCliente().getNome() + " " + p.getCliente().getCognome();
                if (!nomeCompleto.toLowerCase().contains(nomeCliente.toLowerCase())) {
                    corrisponde = false;
                }
            }

            if (titoloFilm != null && !p.getProiezione().getTitolo().toLowerCase().contains(titoloFilm.toLowerCase())) {
                corrisponde = false;
            }

            if (dataInizio != null && p.getProiezione().getDataOra().compareTo(dataInizio) < 0) {
                corrisponde = false;
            }

            if (dataFine != null && p.getProiezione().getDataOra().compareTo(dataFine + " 23:59:59") > 0) {
                corrisponde = false;
            }

            if (corrisponde) {
                risultati.add(p);
            }
        }
        return risultati;
    }
}