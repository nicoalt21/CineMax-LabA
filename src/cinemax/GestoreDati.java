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
            scrittore.write("codice_univoco,username_cliente,data_ora_proiezione,numero_posti,costo_totale\n");
            for (Prenotazione p : listaPrenotazioni) {
                String riga = p.getCodiceUnivoco() + "," + p.getCliente().getUsername() + "," +
                        p.getProiezione().getDataOra() + "," + p.getNumeroPosti() + "," +
                        p.getCostoTotale();
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