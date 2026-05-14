package cinemax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Classe dedicata all'I/O sui file CSV.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class GestoreCSV {

    private static final String PERCORSO_UTENTI = "data/utenti.csv";
    private static final String PERCORSO_PROIEZIONI = "data/proiezioni.csv";
    private static final String PERCORSO_PRENOTAZIONI = "data/prenotazioni.csv";

    // Carica i dati
    public static void caricaUtenti(Map<String, Utente> mappaUtenti) {
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
                    String nome = campi[0];
                    String cognome = campi[1];
                    String username = campi[2];
                    String passwordCifrata = campi[3];
                    String dataNascita = campi[4];
                    String domicilio = campi[5];
                    Ruolo ruolo = Ruolo.valueOf(campi[6].toUpperCase());

                    Utente u = new Utente(nome, cognome, username, passwordCifrata, dataNascita, domicilio, ruolo);
                    mappaUtenti.put(u.getUsername(), u);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore I/O in caricaUtenti: " + e.getMessage());
        }
    }
    public static void caricaProiezioni(Map<String, Proiezione> mappaProiezioni) {
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

    public static void caricaPrenotazioni(List<Prenotazione> listaPrenotazioni, Map<String, Utente> mappaUtenti, Map<String, Proiezione> mappaProiezioni) {
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

    // Salva i dati
    public static void salvaUtenti(Map<String, Utente> mappaUtenti) {
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

    public static void salvaProiezioni(Map<String, Proiezione> mappaProiezioni) {
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

    public static void salvaPrenotazioni(List<Prenotazione> listaPrenotazioni) {
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
}