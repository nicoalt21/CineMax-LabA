package cinemax.controller;

import cinemax.objects.Prenotazione;
import cinemax.objects.Proiezione;
import cinemax.objects.Ruolo;
import cinemax.objects.Utente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Classe dedicata all'I/O sui file CSV.
 * Garantisce la compatibilità multipiattaforma tramite java.nio.file.
 * Separatore di campo: punto e virgola (;) per evitare conflitti con virgole nei dati.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class GestoreCSV {

    private static final Path PERCORSO_UTENTI      = Paths.get("data", "utenti.csv");
    private static final Path PERCORSO_PROIEZIONI  = Paths.get("data", "proiezioni.csv");
    private static final Path PERCORSO_PRENOTAZIONI = Paths.get("data", "prenotazioni.csv");

    private static final String SEPARATORE = ";";

    // --- CARICAMENTO ---

    /**
     * Carica gli utenti dal file CSV nella mappa fornita.
     *
     * @param mappaUtenti La mappa da popolare, indicizzata per username.
     */
    public static void caricaUtenti(Map<String, Utente> mappaUtenti) {
        if (!Files.exists(PERCORSO_UTENTI)) return;

        String riga = null;
        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_UTENTI, StandardCharsets.UTF_8)) {
            boolean intestazione = true;
            while ((riga = lettore.readLine()) != null) {
                if (intestazione) { intestazione = false; continue; }

                String[] campi = riga.split(SEPARATORE, -1);
                if (campi.length >= 7) {
                    Ruolo ruolo = Ruolo.valueOf(campi[6].trim().toUpperCase());
                    Utente u = new Utente(
                            campi[0].trim(), campi[1].trim(), campi[2].trim(),
                            campi[3].trim(), campi[4].trim(), campi[5].trim(), ruolo
                    );
                    mappaUtenti.put(u.getUsername(), u);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Errore in caricaUtenti (riga: \"" + riga + "\"): " + e.getMessage());
        }
    }

    /**
     * Carica le proiezioni dal file CSV nella mappa fornita.
     *
     * @param mappaProiezioni La mappa da popolare, indicizzata per dataOra.
     */
    public static void caricaProiezioni(Map<String, Proiezione> mappaProiezioni) {
        if (!Files.exists(PERCORSO_PROIEZIONI)) return;

        String riga = null;
        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_PROIEZIONI, StandardCharsets.UTF_8)) {
            boolean intestazione = true;
            while ((riga = lettore.readLine()) != null) {
                if (intestazione) { intestazione = false; continue; }

                String[] campi = riga.split(SEPARATORE, -1);
                if (campi.length >= 8) {
                    if (mappaProiezioni.containsKey(campi[0].trim())) {
                        System.err.println("ATTENZIONE: proiezione duplicata per dataOra=" + campi[0].trim() + ", sovrascritta.");
                    }
                    Proiezione p = new Proiezione(
                            campi[0].trim(), campi[1].trim(), campi[2].trim(), campi[3].trim(),
                            Integer.parseInt(campi[4].trim()),
                            Integer.parseInt(campi[5].trim()),
                            Integer.parseInt(campi[6].trim()),
                            Double.parseDouble(campi[7].trim())
                    );
                    mappaProiezioni.put(p.getDataOra(), p);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Errore in caricaProiezioni (riga: \"" + riga + "\"): " + e.getMessage());
        }
    }

    /**
     * Carica le prenotazioni dal file CSV nella lista fornita.
     * Risolve i riferimenti a Utente e Proiezione tramite le mappe già caricate.
     *
     * @param listaPrenotazioni La lista da popolare.
     * @param mappaUtenti       Mappa degli utenti per la risoluzione del riferimento cliente.
     * @param mappaProiezioni   Mappa delle proiezioni per la risoluzione del riferimento proiezione.
     */
    public static void caricaPrenotazioni(List<Prenotazione> listaPrenotazioni,
                                          Map<String, Utente> mappaUtenti,
                                          Map<String, Proiezione> mappaProiezioni) {
        if (!Files.exists(PERCORSO_PRENOTAZIONI)) return;

        String riga = null;
        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_PRENOTAZIONI, StandardCharsets.UTF_8)) {
            boolean intestazione = true;
            while ((riga = lettore.readLine()) != null) {
                if (intestazione) { intestazione = false; continue; }

                String[] campi = riga.split(SEPARATORE, -1);
                if (campi.length >= 4) {
                    String codice           = campi[0].trim();
                    String username         = campi[1].trim();
                    String dataOraProiezione = campi[2].trim();
                    int numeroPosti         = Integer.parseInt(campi[3].trim());

                    Utente cliente      = mappaUtenti.get(username);
                    Proiezione proiezione = mappaProiezioni.get(dataOraProiezione);

                    if (cliente == null) {
                        System.err.println("Errore integrità: utente '" + username + "' non trovato per prenotazione " + codice);
                    } else if (proiezione == null) {
                        System.err.println("Errore integrità: proiezione '" + dataOraProiezione + "' non trovata per prenotazione " + codice);
                    } else {
                        listaPrenotazioni.add(new Prenotazione(codice, cliente, proiezione, numeroPosti));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Errore in caricaPrenotazioni (riga: \"" + riga + "\"): " + e.getMessage());
        }
    }

    // --- SALVATAGGIO ---

    /**
     * Serializza la mappa degli utenti sul file CSV.
     *
     * @param mappaUtenti La mappa da serializzare.
     */
    public static void salvaUtenti(Map<String, Utente> mappaUtenti) {
        try {
            Files.createDirectories(PERCORSO_UTENTI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_UTENTI, StandardCharsets.UTF_8)) {
                scrittore.write("nome;cognome;username;password;data_nascita;domicilio;ruolo");
                scrittore.newLine();
                for (Utente u : mappaUtenti.values()) {
                    scrittore.write(
                            u.getNome()             + SEPARATORE +
                                    u.getCognome()          + SEPARATORE +
                                    u.getUsername()         + SEPARATORE +
                                    u.getPasswordCifrata()  + SEPARATORE +
                                    u.getDataNascita()      + SEPARATORE +
                                    u.getDomicilio()        + SEPARATORE +
                                    u.getRuolo().name()
                    );
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Errore I/O in salvaUtenti: " + e.getMessage());
        }
    }

    /**
     * Serializza la mappa delle proiezioni sul file CSV.
     *
     * @param mappaProiezioni La mappa da serializzare.
     */
    public static void salvaProiezioni(Map<String, Proiezione> mappaProiezioni) {
        try {
            Files.createDirectories(PERCORSO_PROIEZIONI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_PROIEZIONI, StandardCharsets.UTF_8)) {
                scrittore.write("data_ora;titolo;genere;regista;anno;durata_minuti;eta_minima;prezzo_biglietto");
                scrittore.newLine();
                for (Proiezione p : mappaProiezioni.values()) {
                    scrittore.write(
                            p.getDataOra()  + SEPARATORE +
                                    p.getTitolo()   + SEPARATORE +
                                    p.getGenere()   + SEPARATORE +
                                    p.getRegista()  + SEPARATORE +
                                    p.getAnno()     + SEPARATORE +
                                    p.getDurata()   + SEPARATORE +
                                    p.getEtaMinima() + SEPARATORE +
                                    p.getPrezzo()
                    );
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Errore I/O in salvaProiezioni: " + e.getMessage());
        }
    }

    /**
     * Serializza la lista delle prenotazioni sul file CSV.
     *
     * @param listaPrenotazioni La lista da serializzare.
     */
    public static void salvaPrenotazioni(List<Prenotazione> listaPrenotazioni) {
        try {
            Files.createDirectories(PERCORSO_PRENOTAZIONI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_PRENOTAZIONI, StandardCharsets.UTF_8)) {
                scrittore.write("codice_univoco;username_cliente;data_ora_proiezione;numero_posti");
                scrittore.newLine();
                for (Prenotazione p : listaPrenotazioni) {
                    scrittore.write(
                            p.getCodiceUnivoco()            + SEPARATORE +
                                    p.getCliente().getUsername()    + SEPARATORE +
                                    p.getProiezione().getDataOra()  + SEPARATORE +
                                    p.getNumeroPosti()
                    );
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Errore I/O in salvaPrenotazioni: " + e.getMessage());
        }
    }
}