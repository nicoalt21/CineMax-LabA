package cinemax;

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
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class GestoreCSV {

    private static final Path PERCORSO_UTENTI = Paths.get("data", "utenti.csv");
    private static final Path PERCORSO_PROIEZIONI = Paths.get("data", "proiezioni.csv");
    private static final Path PERCORSO_PRENOTAZIONI = Paths.get("data", "prenotazioni.csv");

    // --- CARICAMENTO DATI ---

    public static void caricaUtenti(Map<String, Utente> mappaUtenti) {
        if (!Files.exists(PERCORSO_UTENTI)) return;

        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_UTENTI, StandardCharsets.UTF_8)) {
            String riga;
            boolean isIntestazione = true;

            while ((riga = lettore.readLine()) != null) {
                if (isIntestazione) {
                    isIntestazione = false;
                    continue;
                }

                String[] campi = riga.split(",");
                if (campi.length >= 7) {
                    Utente u = new Utente(campi[0], campi[1], campi[2], campi[3], campi[4], campi[5], Ruolo.valueOf(campi[6].toUpperCase()));
                    mappaUtenti.put(u.getUsername(), u);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in caricaUtenti: " + e.getMessage());
        }
    }

    public static void caricaProiezioni(Map<String, Proiezione> mappaProiezioni) {
        if (!Files.exists(PERCORSO_PROIEZIONI)) return;

        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_PROIEZIONI, StandardCharsets.UTF_8)) {
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

    public static void caricaPrenotazioni(List<Prenotazione> listaPrenotazioni, Map<String, Utente> mappaUtenti, Map<String, Proiezione> mappaProiezioni) {
        if (!Files.exists(PERCORSO_PRENOTAZIONI)) return;

        try (BufferedReader lettore = Files.newBufferedReader(PERCORSO_PRENOTAZIONI, StandardCharsets.UTF_8)) {
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

    // --- SALVATAGGIO DATI ---

    public static void salvaUtenti(Map<String, Utente> mappaUtenti) {
        try {
            Files.createDirectories(PERCORSO_UTENTI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_UTENTI, StandardCharsets.UTF_8)) {
                scrittore.write("nome,cognome,username,password,data_nascita,domicilio,ruolo");
                scrittore.newLine();
                for (Utente u : mappaUtenti.values()) {
                    String riga = u.getNome() + "," + u.getCognome() + "," + u.getUsername() + "," +
                            u.getPasswordCifrata() + "," + u.getDataNascita() + "," +
                            u.getDomicilio() + "," + u.getRuolo().name();
                    scrittore.write(riga);
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaUtenti: " + e.getMessage());
        }
    }

    public static void salvaProiezioni(Map<String, Proiezione> mappaProiezioni) {
        try {
            Files.createDirectories(PERCORSO_PROIEZIONI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_PROIEZIONI, StandardCharsets.UTF_8)) {
                scrittore.write("data_ora,titolo,genere,regista,anno,durata,eta_minima,prezzo");
                scrittore.newLine();
                for (Proiezione p : mappaProiezioni.values()) {
                    String riga = p.getDataOra() + "," + p.getTitolo() + "," + p.getGenere() + "," +
                            p.getRegista() + "," + p.getAnno() + "," + p.getDurata() + "," +
                            p.getEtaMinima() + "," + p.getPrezzo();
                    scrittore.write(riga);
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaProiezioni: " + e.getMessage());
        }
    }

    public static void salvaPrenotazioni(List<Prenotazione> listaPrenotazioni) {
        try {
            Files.createDirectories(PERCORSO_PRENOTAZIONI.getParent());
            try (BufferedWriter scrittore = Files.newBufferedWriter(PERCORSO_PRENOTAZIONI, StandardCharsets.UTF_8)) {
                scrittore.write("codice_univoco,username_cliente,data_ora_proiezione,numero_posti");
                scrittore.newLine();
                for (Prenotazione p : listaPrenotazioni) {
                    String riga = p.getCodiceUnivoco() + "," + p.getCliente().getUsername() + "," +
                            p.getProiezione().getDataOra() + "," + p.getNumeroPosti();
                    scrittore.write(riga);
                    scrittore.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in salvaPrenotazioni: " + e.getMessage());
        }
    }
}