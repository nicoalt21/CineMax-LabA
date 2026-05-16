package cinemax.ui;

import cinemax.controller.Cifrario;
import cinemax.controller.GestoreDati;
import cinemax.objects.Proiezione;
import cinemax.objects.Ruolo;
import cinemax.objects.Utente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class MenuPrincipale {
    private GestoreDati gestore;
    private BufferedReader in;

    public MenuPrincipale(GestoreDati gestore) {
        this.gestore = gestore;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    private String leggiInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("\nErrore di lettura dell'input. Riprova.\n");
            return "";
        }
    }

    public void avvia() {
        String scelta;
        do {
            System.out.println("\n=================================");
            System.out.println("    *** BENVENUTO IN CINEMAX *** ");
            System.out.println("=================================");
            System.out.println("1. Login (Clienti, Staff)");
            System.out.println("2. Registrati (Nuovo Cliente)");
            System.out.println("3. Accesso Guest (Cerca Proiezioni)");
            System.out.println("0. Esci dall'applicazione");
            System.out.print("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": login(); break;
                case "2": registraCliente(); break;
                case "3": menuGuest(); break;
                case "0": System.out.println("Chiusura in corso. A presto!"); break;
                default: System.out.println("Opzione non valida. Riprova.");
            }
        } while (!scelta.equals("0"));
    }

    // Login e smistamento ruoli
    private void login() {
        System.out.println("\n--- LOGIN ---\n\nDigita 'back' per tornare al menù principale.\n");

        while(true) {
            System.out.print("Username: ");
            String username = leggiInput();
            if(username.equals("back")){return;}

            System.out.print("Password: ");
            String password = leggiInput();
            if(password.equals("back")){return;}

            Utente utenteAutenticato = gestore.autenticaUtente(username, password);

            if(utenteAutenticato != null) {
                switch (utenteAutenticato.getRuolo()){
                    case CLIENTE: menuCliente(username); break;
                    case BIGLIETTAIO: menuBigliettaio(username); break;
                    case PROIEZIONISTA: menuProiezionista(username); break;
                    default: System.out.println("Errore inaspettato: " + username + " ha un ruolo imprevisto.");
                }
                return; // Esce dal loop infinito dopo il logout dal sottomenu
            } else {
                System.out.println("Username o Password non validi. Riprova.\n");
            }
        }
    }

    // Registrazione
    private void registraCliente() {
        System.out.println("\n--- REGISTRAZIONE NUOVO CLIENTE ---\nI campi con il carattere * sono obbligatori.\nDigita 'back' per tornare al menù principale.\n");

        System.out.print("*Nome: "); String nome = leggiInput();
        if(nome.equals("back")){return;}
        System.out.print("*Cognome: "); String cognome = leggiInput();
        if(cognome.equals("back")){return;}
        System.out.print("Data di Nascita: "); String dataNascita = leggiInput();
        if(dataNascita.equals("back")){return;}
        System.out.print("*Luogo domicilio: "); String dom = leggiInput();
        if(dom.equals("back")){return;}
        System.out.print("*Username: "); String user = leggiInput();
        if(user.equals("back")){return;}
        System.out.print("*Password: "); String passwordInChiaro = leggiInput(); // MATO: qua vorrei tanto mettere un Cifraio.cifraPassword(leggiInput()); così che la password in chiaro non sia MAI salvata come variabile, neanche durante la registrazione o il login...
        if(passwordInChiaro.equals("back")){return;}

        try {
            Utente nuovoUtente = new Utente(nome, cognome, user, Cifrario.cifraPassword(passwordInChiaro), dataNascita, dom, Ruolo.CLIENTE);

            if(gestore.registraCliente(nuovoUtente)) {
                System.out.println("Registrazione effettuata con successo. Ora puoi effettuare il Login.\n1. login\n2. registra cliente\n3. menu principale\n\nSeleziona un'opzione: ");
                String scelta = leggiInput();

                switch (scelta) {
                    case "1": login(); break;
                    case "2": registraCliente(); break;
                    case "3": return;
                    default: return;
                }
            } else {
                System.out.println("Errore: Username già in uso.");
            }
        } catch (Exception e) {
            System.out.println("\nRegistrazione fallita: " + e.getMessage());
            registraCliente();
        }
    }

    // Area Guest
    private void menuGuest() {
        String scelta;

        String titolo = null;
        String genere = null;
        String dataInizio = null;
        String dataFine = null;
        Double prezzoMin = null;
        Double prezzoMax = null;

        do {
            System.out.println("\n--- MENU RICERCA PROIEZIONI ---");
            System.out.println("Filtri attuali:");
            System.out.println("- Titolo: " + (titolo != null ? titolo : "Qualsiasi"));
            System.out.println("- Genere: " + (genere != null ? genere : "Qualsiasi"));
            System.out.println("- Date: " + (dataInizio != null ? dataInizio : "Qualsiasi") + " -> " + (dataFine != null ? dataFine : "Qualsiasi"));
            System.out.println("- Costo: " + (prezzoMin != null ? prezzoMin + "€" : "Qualsiasi") + " -> " + (prezzoMax != null ? prezzoMax + "€" : "Qualsiasi"));
            System.out.println("-------------------------------");

            System.out.println("1. Imposta/Modifica Titolo");
            System.out.println("2. Imposta/Modifica Genere");
            System.out.println("3. Imposta/Modifica Intervallo di Date");
            System.out.println("4. Imposta/Modifica Costo del biglietto");
            System.out.println("5. ---> ESEGUI RICERCA <---");
            System.out.println("6. Azzera tutti i filtri");
            System.out.println("7. Visualizza Dettagli Proiezione (per Data/Ora)");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    System.out.print("Inserisci titolo (o premi Invio per rimuovere il filtro): ");
                    String inputTitolo = leggiInput();
                    titolo = inputTitolo.isEmpty() ? null : inputTitolo;
                    break;
                case "2":
                    System.out.print("Inserisci genere (o premi Invio per rimuovere il filtro): ");
                    String inputGenere = leggiInput();
                    genere = inputGenere.isEmpty() ? null : inputGenere;
                    break;
                case "3":
                    System.out.print("Inserisci data di Inizio (es. 2026-05-15) o Invio per saltare: ");
                    String inputInizio = leggiInput();
                    dataInizio = inputInizio.isEmpty() ? null : inputInizio;

                    System.out.print("Inserisci data fine (es. 2026-05-29) o Invio per saltare: ");
                    String inputFine = leggiInput();
                    dataFine = inputFine.isEmpty() ? null : inputFine;
                    break;
                case "4":
                    System.out.print("Inserire costo Minimo o Invio per saltare: ");
                    String inputMin = leggiInput();
                    prezzoMin = inputMin.isEmpty() ? null : Double.parseDouble(inputMin);

                    System.out.print("Inserire costo Massimo o Invio per saltare: ");
                    String inputMax = leggiInput();
                    prezzoMax = inputMax.isEmpty() ? null : Double.parseDouble(inputMax);
                    break;
                case "5":
                    System.out.println("\nEsecuzione ricerca in corso...\n");
                    gestore.cercaProiezione(titolo, genere, dataInizio, dataFine, prezzoMin, prezzoMax);
                    break;
                case "6":
                    titolo = null; genere = null; dataInizio = null; dataFine = null; prezzoMin = null; prezzoMax = null;
                    System.out.println("Filtri azzerati!");
                    break;
                case "7":
                    System.out.print("Inserisci la data e ora della proiezione da visualizzare (es. 2026-05-20 21:00:00): ");
                    String dataOraProiezione = leggiInput();
                    visualizzaDettagli(dataOraProiezione);
                    break;

                case "0":
                    break;

                default:
                    System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    private void visualizzaDettagli(String dataOra) {
        System.out.println("\nRicerca dettagli in corso per: " + dataOra + "...");

        try {
            Proiezione p = gestore.ottieniProiezione(dataOra);

            if (p != null) {
                System.out.println("\n=================================");
                System.out.println("      DETTAGLI PROIEZIONE        ");
                System.out.println("=================================");
                System.out.println("Titolo Film     : " + p.getTitolo());
                System.out.println("Regista         : " + p.getRegista());
                System.out.println("Genere          : " + p.getGenere());
                System.out.println("Anno            : " + p.getAnno());
                System.out.println("Durata          : " + p.getDurata() + " min");
                System.out.println("Data e Ora      : " + p.getDataOra());
                System.out.println("Costo Biglietto : " + p.getPrezzo() + " €");
                System.out.println("Età minima      : " + p.getEtaMinima() + " anni");
                System.out.println("Posti liberi    : " + gestore.calcolaPostiLiberi(p.getDataOra()));
                System.out.println("=================================\n");
            } else {
                System.out.println("\nNessuna proiezione trovata per la data: " + dataOra);
            }
        } catch (Exception e) {
            System.out.println("\nSi è verificato un errore durante la ricerca: " + e.getMessage());
        }
    }

    // Menu Cliente
    private void menuCliente(String username) {
        String scelta;
        do {
            System.out.println("\n--- AREA PERSONALE CLIENTE: " + username + " ---");
            System.out.println("1. Inserisci una prenotazione");
            System.out.println("2. Visualizza le tue prenotazioni");
            System.out.println("3. Modifica data prenotazione");
            System.out.println("4. Cancella una prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    System.out.println("Inserimento prenotazione in corso...");
                    break;
                case "2":
                    System.out.println("Elenco prenotazioni:");
                    break;
                case "3":
                    System.out.println("Modifica data prenotazione:");
                    break;
                case "4":
                    System.out.println("Cancellazione prenotazione:");
                    break;
                case "0": System.out.println("Logout effettuato."); break;
                default: System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    // Menu Proiezionista
    private void menuProiezionista(String username) {
        String scelta;
        do {
            System.out.println("\n--- PANNELLO DI CONTROLLO PROIEZIONISTA: " + username + " ---");
            System.out.println("1. Inserisci Film e Proiezione");
            System.out.println("2. Modifica data proiezione");
            System.out.println("3. Elimina proiezione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    System.out.println("\n--- Inserimento Nuova Proiezione ---");

                    System.out.print("Titolo del film: ");
                    String titolo = leggiInput();
                    System.out.print("Genere: ");
                    String genere = leggiInput();
                    System.out.print("Regista: ");
                    String regista = leggiInput();
                    System.out.print("Anno di uscita: ");
                    String anno = leggiInput();
                    System.out.print("Durata (minuti): ");
                    String durata = leggiInput();
                    System.out.print("Età minima pubblico: ");
                    String etaMinima = leggiInput();

                    System.out.print("Data e ora (es. 2026-05-20 21:00:00): ");
                    String dataOra = leggiInput();
                    System.out.print("Costo del biglietto (€): ");
                    String costo = leggiInput();

                    System.out.println("Salvataggio in corso...");

                    try {
                        Proiezione p = new Proiezione(
                                dataOra, titolo, genere, regista,
                                Integer.parseInt(anno), Integer.parseInt(durata),
                                Integer.parseInt(etaMinima), Double.parseDouble(costo.replace(",", "."))
                        );
                        // gestore.aggiungiProiezione(p);
                    } catch (NumberFormatException e) {
                        System.out.println("Errore: Assicurati di inserire valori numerici validi per anno, durata, età e costo.");
                    }
                    break;
                case "2":
                    System.out.println("\n--- Modifica Data Proiezione ---");

                    System.out.print("Inserisci la data/ora attuale della proiezione da modificare: ");
                    String dataOraAttuale = leggiInput();
                    System.out.print("Inserisci la NUOVA data e ora: ");
                    String nuovaDataOra = leggiInput();

                    System.out.println("Verifica prenotazioni e modifica in corso...");
                    // gestore.modificaProiezione(dataOraAttuale, nuovaDataOra);
                    break;
                case "3":
                    System.out.println("\n--- Eliminazione Proiezione ---");
                    System.out.print("Inserisci la data/ora della proiezione da eliminare: ");
                    String idElimina = leggiInput();

                    System.out.println("Eliminazione in corso...");
                    // gestore.eliminaProiezione(idElimina);
                    break;

                case "0":
                    System.out.println("Logout effettuato.");
                    break;

                default:
                    System.out.println("Opzione errata.");
            }
        } while (!scelta.equals("0"));
    }

    // Menu Bigliettaio
    private void menuBigliettaio(String username) {
        String scelta;
        do {
            System.out.println("\n--- TERMINALE BIGLIETTERIA: " + username + " ---");
            System.out.println("1. Visualizza prenotazioni odierne");
            System.out.println("2. Cerca prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    System.out.println("\n--- Prenotazioni Odierne ---");
                    System.out.println("Recupero dati in corso...");
                    break;
                case "2":
                    System.out.println("\n--- Ricerca Prenotazione ---");
                    System.out.println("Scegli il criterio di ricerca:");
                    System.out.println("1. Per codice prenotazione");
                    System.out.println("2. Per nome e cognome del cliente");
                    System.out.println("3. Per titolo (anche parziale) del film");
                    System.out.println("4. Per intervallo di date");
                    System.out.print("Criterio scelto: ");

                    String criterio = leggiInput();

                    // Dichiarazione variabili fuori dallo switch per renderle visibili al metodo di ricerca successivo
                    String codice = null;
                    String nomeCliente = null;
                    String cognomeCliente = null;
                    String titolo = null;
                    String dataInizio = null;
                    String dataFine = null;

                    switch (criterio) {
                        case "1":
                            System.out.print("Inserisci il codice univoco della prenotazione: ");
                            codice = leggiInput();
                            break;
                        case "2":
                            System.out.print("Inserisci il nome del cliente: ");
                            nomeCliente = leggiInput();
                            System.out.print("Inserisci il cognome del cliente: ");
                            cognomeCliente = leggiInput();
                            break;
                        case "3":
                            System.out.print("Inserisci il titolo (anche parziale) del film: ");
                            titolo = leggiInput();
                            break;
                        case "4":
                            System.out.print("Inserisci la data di inizio (es. 2026-05-20): ");
                            dataInizio = leggiInput();
                            System.out.print("Inserisci la data di fine (es. 2026-05-29): ");
                            dataFine = leggiInput();
                            break;
                        default:
                            System.out.println("Criterio di ricerca non valido.");
                    }

                    System.out.print("\nVuoi selezionare e visualizzare i dettagli di una prenotazione? (Y/N): ");
                    String risposta = leggiInput();
                    if (risposta.equalsIgnoreCase("Y")) {
                        System.out.print("Inserisci il codice della prenotazione da visualizzare: ");
                        String codPrenotazione = leggiInput();
                        System.out.println("Caricamento dettagli completi...");
                    }
                    break;
                case "0":
                    System.out.println("Logout effettuato.");
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }
}