package cinemax.ui;

import cinemax.controller.Cifrario;
import cinemax.controller.GestoreDati;
import cinemax.objects.Prenotazione;
import cinemax.objects.Proiezione;
import cinemax.objects.Ruolo;
import cinemax.objects.Utente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * Gestisce l'interfaccia testuale dell'applicazione CineMax.
 * Smista i flussi in base al ruolo dell'utente autenticato e coordina
 * le interazioni tra utente e GestoreDati.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class MenuPrincipale {

    private GestoreDati gestore;
    private BufferedReader in;

    /**
     * Costruisce il menu principale legandolo all'istanza di GestoreDati.
     *
     * @param gestore Riferimento al gestore dei dati in memoria.
     */
    public MenuPrincipale(GestoreDati gestore) {
        this.gestore = gestore;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Legge una riga di input da stdin.
     * In caso di errore I/O restituisce stringa vuota per evitare NullPointerException a monte.
     *
     * @return La stringa inserita dall'utente o "" in caso di errore.
     */
    private String leggiInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.out.println("\nErrore di lettura dell'input. Riprova.\n");
            return "";
        }
    }

    /**
     * Avvia il loop principale dell'interfaccia testuale.
     * Rimane attivo fino a quando l'utente non seleziona l'opzione di uscita.
     */
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
                default:  System.out.println("Opzione non valida. Riprova.");
            }
        } while (!scelta.equals("0"));
    }

    // ============================================================
    // LOGIN E REGISTRAZIONE
    // ============================================================

    /**
     * Gestisce il flusso di autenticazione e smista l'utente nel menu corretto in base al ruolo.
     */
    private void login() {
        System.out.println("\n--- LOGIN ---\n\nDigita 'back' per tornare al menù principale.\n");

        while (true) {
            System.out.print("Username: ");
            String username = leggiInput();
            if (username.equals("back")) return;

            System.out.print("Password: ");
            String password = leggiInput();
            if (password.equals("back")) return;

            Utente utenteAutenticato = gestore.autenticaUtente(username, password);

            if (utenteAutenticato != null) {
                switch (utenteAutenticato.getRuolo()) {
                    case CLIENTE:       menuCliente(utenteAutenticato); break;
                    case BIGLIETTAIO:   menuBigliettaio(username); break;
                    case PROIEZIONISTA: menuProiezionista(username); break;
                    default: System.out.println("Errore inaspettato: ruolo imprevisto.");
                }
                return;
            } else {
                System.out.println("Username o Password non validi. Riprova.\n");
            }
        }
    }

    /**
     * Gestisce la registrazione di un nuovo cliente tramite loop iterativo.
     * Evita la ricorsione per prevenire stack overflow su errori ripetuti.
     */
    private void registraCliente() {
        while (true) {
            System.out.println("\n--- REGISTRAZIONE NUOVO CLIENTE ---");
            System.out.println("I campi con * sono obbligatori. Digita 'back' per tornare al menù principale.\n");

            System.out.print("*Nome: ");                        String nome = leggiInput();
            if (nome.equals("back")) return;
            System.out.print("*Cognome: ");                     String cognome = leggiInput();
            if (cognome.equals("back")) return;
            System.out.print("Data di Nascita (yyyy-MM-dd): "); String dataNascita = leggiInput();
            if (dataNascita.equals("back")) return;
            System.out.print("*Luogo domicilio: ");             String dom = leggiInput();
            if (dom.equals("back")) return;
            System.out.print("*Username: ");                    String user = leggiInput();
            if (user.equals("back")) return;
            System.out.print("*Password: ");                    String passwordInChiaro = leggiInput();
            if (passwordInChiaro.equals("back")) return;

            try {
                Utente nuovoUtente = new Utente(
                        nome, cognome, user,
                        Cifrario.cifraPassword(passwordInChiaro),
                        dataNascita, dom, Ruolo.CLIENTE
                );

                if (gestore.registraCliente(nuovoUtente)) {
                    System.out.println("\nRegistrazione effettuata con successo.");
                    System.out.println("1. Effettua il login");
                    System.out.println("2. Registra un altro cliente");
                    System.out.println("3. Torna al menu principale");
                    System.out.print("\nSeleziona un'opzione: ");
                    String scelta = leggiInput();
                    switch (scelta) {
                        case "1": login(); return;
                        case "2": continue;
                        default:  return;
                    }
                } else {
                    System.out.println("Errore: Username già in uso. Riprova.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("\nRegistrazione fallita: " + e.getMessage());
            }
        }
    }

    // ============================================================
    // MENU GUEST
    // ============================================================

    /**
     * Espone le funzionalità di ricerca proiezioni accessibili senza autenticazione.
     * Mantiene i filtri attivi tra una ricerca e l'altra fino all'azzeramento esplicito.
     */
    private void menuGuest() {
        String scelta;
        String titolo = null, genere = null;
        String dataInizio = null, dataFine = null;
        Double prezzoMin = null, prezzoMax = null;

        do {
            System.out.println("\n--- MENU RICERCA PROIEZIONI ---");
            System.out.println("Filtri attuali:");
            System.out.println("- Titolo : " + (titolo != null ? titolo : "Qualsiasi"));
            System.out.println("- Genere : " + (genere != null ? genere : "Qualsiasi"));
            System.out.println("- Date   : " + (dataInizio != null ? dataInizio : "Qualsiasi") +
                    " -> " + (dataFine != null ? dataFine : "Qualsiasi"));
            System.out.println("- Costo  : " + (prezzoMin != null ? prezzoMin + "€" : "Qualsiasi") +
                    " -> " + (prezzoMax != null ? prezzoMax + "€" : "Qualsiasi"));
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
                    System.out.print("Inserisci titolo (Invio per rimuovere filtro): ");
                    String inputTitolo = leggiInput();
                    titolo = inputTitolo.isEmpty() ? null : inputTitolo;
                    break;
                case "2":
                    System.out.print("Inserisci genere (Invio per rimuovere filtro): ");
                    String inputGenere = leggiInput();
                    genere = inputGenere.isEmpty() ? null : inputGenere;
                    break;
                case "3":
                    System.out.print("Data inizio (yyyy-MM-dd) o Invio per saltare: ");
                    String inputInizio = leggiInput();
                    dataInizio = inputInizio.isEmpty() ? null : inputInizio;
                    System.out.print("Data fine (yyyy-MM-dd) o Invio per saltare: ");
                    String inputFine = leggiInput();
                    dataFine = inputFine.isEmpty() ? null : inputFine;
                    break;
                case "4":
                    while (true) {
                        System.out.print("Costo minimo o Invio per saltare: ");
                        String inputMin = leggiInput();
                        if (inputMin.isEmpty()) { prezzoMin = null; break; }
                        try { prezzoMin = Double.parseDouble(inputMin.replace(",", ".")); break; }
                        catch (NumberFormatException e) { System.out.println("Valore non valido. Riprova."); }
                    }
                    while (true) {
                        System.out.print("Costo massimo o Invio per saltare: ");
                        String inputMax = leggiInput();
                        if (inputMax.isEmpty()) { prezzoMax = null; break; }
                        try { prezzoMax = Double.parseDouble(inputMax.replace(",", ".")); break; }
                        catch (NumberFormatException e) { System.out.println("Valore non valido. Riprova."); }
                    }
                    break;
                case "5":
                    List<Proiezione> risultati = gestore.cercaProiezione(
                            titolo, genere, dataInizio, dataFine, prezzoMin, prezzoMax
                    );
                    stampaListaProiezioni(risultati);
                    break;
                case "6":
                    titolo = null; genere = null;
                    dataInizio = null; dataFine = null;
                    prezzoMin = null; prezzoMax = null;
                    System.out.println("Filtri azzerati.");
                    break;
                case "7":
                    System.out.print("Data e ora proiezione (yyyy-MM-dd HH:mm:ss): ");
                    visualizzaDettagliProiezione(leggiInput());
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Stampa una lista compatta di proiezioni con posti liberi.
     *
     * @param lista La lista di proiezioni da visualizzare.
     */
    private void stampaListaProiezioni(List<Proiezione> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nessuna proiezione trovata con i filtri selezionati.");
            return;
        }
        System.out.println("\nTrovate " + lista.size() + " proiezioni:");
        System.out.println("---------------------------------------------------------------");
        for (Proiezione p : lista) {
            System.out.printf("[%s] %s | %s | %.2f€ | Posti liberi: %d%n",
                    p.getDataOra(), p.getTitolo(), p.getGenere(),
                    p.getPrezzo(), gestore.calcolaPostiLiberi(p.getDataOra()));
        }
        System.out.println("---------------------------------------------------------------");
    }

    /**
     * Visualizza il dettaglio completo di una proiezione identificata per data e ora.
     *
     * @param dataOra La chiave di ricerca nel formato yyyy-MM-dd HH:mm:ss.
     */
    private void visualizzaDettagliProiezione(String dataOra) {
        Proiezione p = gestore.ottieniProiezione(dataOra);
        if (p == null) {
            System.out.println("\nNessuna proiezione trovata per: " + dataOra);
            return;
        }
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
    }

    /**
     * Visualizza il dettaglio completo di una prenotazione identificata per codice univoco.
     *
     * @param codiceUnivoco Il codice alfanumerico della prenotazione.
     */
    private void visualizzaDettagliPrenotazione(String codiceUnivoco) {
        Prenotazione p = gestore.ottieniPrenotazione(codiceUnivoco);
        if (p == null) {
            System.out.println("\nNessuna prenotazione trovata con il codice: " + codiceUnivoco);
            return;
        }
        System.out.println("\n=================================");
        System.out.println("      DETTAGLI PRENOTAZIONE      ");
        System.out.println("=================================");
        System.out.println("Codice          : " + p.getCodiceUnivoco());
        System.out.println("Cliente         : " + p.getCliente().getNome() + " " + p.getCliente().getCognome());
        System.out.println("Username        : " + p.getCliente().getUsername());
        System.out.println("Film            : " + p.getProiezione().getTitolo());
        System.out.println("Data e Ora      : " + p.getProiezione().getDataOra());
        System.out.println("Numero biglietti: " + p.getNumeroPosti());
        System.out.printf ("Costo unitario  : %.2f €%n", p.getProiezione().getPrezzo());
        System.out.printf ("Costo totale    : %.2f €%n", p.getCostoTotale());
        System.out.println("=================================\n");
    }

    // ============================================================
    // MENU CLIENTE
    // ============================================================

    /**
     * Espone le funzionalità riservate ai clienti autenticati.
     *
     * @param cliente L'oggetto Utente autenticato.
     */
    private void menuCliente(Utente cliente) {
        String scelta;
        do {
            System.out.println("\n--- AREA PERSONALE CLIENTE: " + cliente.getUsername() + " ---");
            System.out.println("1. Cerca proiezioni");
            System.out.println("2. Crea una prenotazione");
            System.out.println("3. Visualizza le tue prenotazioni");
            System.out.println("4. Modifica data prenotazione");
            System.out.println("5. Cancella una prenotazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": menuGuest(); break;
                case "2": creaPrenotazione(cliente); break;
                case "3": visualizzaPrenotazioniCliente(cliente); break;
                case "4": modificaPrenotazioneCliente(); break;
                case "5": eliminaPrenotazioneCliente(); break;
                case "0": System.out.println("Logout effettuato."); break;
                default:  System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Guida il cliente nella creazione di una nuova prenotazione.
     *
     * @param cliente L'utente che sta effettuando la prenotazione.
     */
    private void creaPrenotazione(Utente cliente) {
        System.out.println("\n--- NUOVA PRENOTAZIONE ---");
        System.out.print("Data e ora della proiezione (yyyy-MM-dd HH:mm:ss): ");
        String dataOra = leggiInput();

        Proiezione p = gestore.ottieniProiezione(dataOra);
        if (p == null) {
            System.out.println("Proiezione non trovata.");
            return;
        }

        System.out.println("Proiezione    : " + p.getTitolo() + " - " + p.getDataOra());
        System.out.println("Posti liberi  : " + gestore.calcolaPostiLiberi(dataOra));
        System.out.printf ("Prezzo        : %.2f €%n", p.getPrezzo());
        System.out.print("Numero di posti: ");

        int posti;
        try {
            posti = Integer.parseInt(leggiInput());
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido.");
            return;
        }

        String codice = gestore.creaPrenotazione(cliente, p, posti);
        if (codice == null) {
            System.out.println("Prenotazione fallita: posti insufficienti, numero non valido o età minima non rispettata.");
        } else {
            System.out.println("\nPrenotazione effettuata con successo.");
            System.out.println("Codice univoco : " + codice);
            System.out.printf ("Totale         : %.2f €%n", posti * p.getPrezzo());
        }
    }

    /**
     * Mostra tutte le prenotazioni attive del cliente autenticato.
     *
     * @param cliente L'utente di cui visualizzare le prenotazioni.
     */
    private void visualizzaPrenotazioniCliente(Utente cliente) {
        List<Prenotazione> prenotazioni = gestore.visualizzaPrenotazioni(cliente);
        if (prenotazioni.isEmpty()) {
            System.out.println("Non hai prenotazioni attive.");
            return;
        }
        System.out.println("\n--- LE TUE PRENOTAZIONI ---");
        for (Prenotazione p : prenotazioni) {
            System.out.printf("Codice: %s | Film: %s | Data: %s | Posti: %d | Totale: %.2f€%n",
                    p.getCodiceUnivoco(),
                    p.getProiezione().getTitolo(),
                    p.getProiezione().getDataOra(),
                    p.getNumeroPosti(),
                    p.getCostoTotale());
        }
    }

    /**
     * Guida il cliente nella modifica della data di una prenotazione esistente.
     */
    private void modificaPrenotazioneCliente() {
        System.out.println("\n--- MODIFICA PRENOTAZIONE ---");
        System.out.print("Codice prenotazione: ");
        String codice = leggiInput();
        System.out.print("Nuova data e ora proiezione (yyyy-MM-dd HH:mm:ss): ");
        String nuovaDataOra = leggiInput();

        if (gestore.modificaPrenotazione(codice, nuovaDataOra)) {
            System.out.println("Prenotazione modificata con successo.");
        } else {
            System.out.println("Modifica fallita: codice errato, date non future o posti insufficienti.");
        }
    }

    /**
     * Guida il cliente nell'eliminazione di una prenotazione futura.
     */
    private void eliminaPrenotazioneCliente() {
        System.out.println("\n--- ELIMINAZIONE PRENOTAZIONE ---");
        System.out.print("Codice prenotazione: ");
        String codice = leggiInput();

        if (gestore.eliminaPrenotazione(codice)) {
            System.out.println("Prenotazione eliminata con successo.");
        } else {
            System.out.println("Eliminazione fallita: codice errato o data proiezione già passata.");
        }
    }

    // ============================================================
    // MENU PROIEZIONISTA
    // ============================================================

    /**
     * Espone le funzionalità riservate al proiezionista per la gestione del palinsesto.
     *
     * @param username Username del proiezionista autenticato.
     */
    private void menuProiezionista(String username) {
        String scelta;
        do {
            System.out.println("\n--- PANNELLO PROIEZIONISTA: " + username + " ---");
            System.out.println("1. Inserisci nuova proiezione");
            System.out.println("2. Modifica data proiezione");
            System.out.println("3. Elimina proiezione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": aggiungiProiezione(); break;
                case "2": modificaProiezione(); break;
                case "3": eliminaProiezione(); break;
                case "0": System.out.println("Logout effettuato."); break;
                default:  System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Guida il proiezionista nell'inserimento di una nuova proiezione nel palinsesto.
     */
    private void aggiungiProiezione() {
        System.out.println("\n--- INSERIMENTO NUOVA PROIEZIONE ---");
        System.out.print("Titolo del film: ");             String titolo = leggiInput();
        System.out.print("Genere: ");                      String genere = leggiInput();
        System.out.print("Regista: ");                     String regista = leggiInput();
        System.out.print("Anno di uscita: ");              String anno = leggiInput();
        System.out.print("Durata (minuti): ");             String durata = leggiInput();
        System.out.print("Età minima pubblico: ");         String etaMinima = leggiInput();
        System.out.print("Data e ora (yyyy-MM-dd HH:mm:ss): "); String dataOra = leggiInput();
        System.out.print("Costo del biglietto (€): ");     String costo = leggiInput();

        try {
            Proiezione p = new Proiezione(
                    dataOra, titolo, genere, regista,
                    Integer.parseInt(anno),
                    Integer.parseInt(durata),
                    Integer.parseInt(etaMinima),
                    Double.parseDouble(costo.replace(",", "."))
            );
            if (gestore.aggiungiProiezione(p)) {
                System.out.println("Proiezione aggiunta con successo.");
            } else {
                System.out.println("Inserimento fallito: esiste già una proiezione per quella data e ora.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Errore: valori numerici non validi per anno, durata, età o costo.");
        } catch (IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    /**
     * Guida il proiezionista nella modifica della data di una proiezione senza prenotazioni.
     */
    private void modificaProiezione() {
        System.out.println("\n--- MODIFICA DATA PROIEZIONE ---");
        System.out.print("Data e ora attuale (yyyy-MM-dd HH:mm:ss): ");
        String dataOraAttuale = leggiInput();
        System.out.print("Nuova data e ora (yyyy-MM-dd HH:mm:ss): ");
        String nuovaDataOra = leggiInput();

        if (gestore.modificaProiezione(dataOraAttuale, nuovaDataOra)) {
            System.out.println("Proiezione modificata con successo.");
        } else {
            System.out.println("Modifica fallita: proiezione inesistente, nuova data già occupata o prenotazioni attive.");
        }
    }

    /**
     * Guida il proiezionista nell'eliminazione di una proiezione senza prenotazioni.
     */
    private void eliminaProiezione() {
        System.out.println("\n--- ELIMINAZIONE PROIEZIONE ---");
        System.out.print("Data e ora proiezione (yyyy-MM-dd HH:mm:ss): ");
        String idElimina = leggiInput();

        if (gestore.eliminaProiezione(idElimina)) {
            System.out.println("Proiezione eliminata con successo.");
        } else {
            System.out.println("Eliminazione fallita: proiezione inesistente o prenotazioni attive.");
        }
    }

    // ============================================================
    // MENU BIGLIETTAIO
    // ============================================================

    /**
     * Espone le funzionalità riservate al bigliettaio per la gestione delle prenotazioni.
     *
     * @param username Username del bigliettaio autenticato.
     */
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
                case "1": visualizzaPrenotazioniOdierne(); break;
                case "2": cercaPrenotazione(); break;
                case "0": System.out.println("Logout effettuato."); break;
                default:  System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Mostra tutte le prenotazioni con proiezione nella data odierna.
     */
    private void visualizzaPrenotazioniOdierne() {
        List<Prenotazione> oggi = gestore.ottieniPrenotazioniOggi();
        if (oggi.isEmpty()) {
            System.out.println("Nessuna prenotazione per oggi.");
            return;
        }
        System.out.println("\n--- PRENOTAZIONI ODIERNE (" + oggi.size() + ") ---");
        for (Prenotazione p : oggi) {
            stampaPrenotazione(p);
        }
    }

    /**
     * Guida il bigliettaio nella ricerca di prenotazioni tramite criteri multipli.
     */
    private void cercaPrenotazione() {
        System.out.println("\n--- RICERCA PRENOTAZIONE ---");
        System.out.println("Scegli il criterio di ricerca:");
        System.out.println("1. Per codice prenotazione");
        System.out.println("2. Per nome e cognome del cliente");
        System.out.println("3. Per titolo (anche parziale) del film");
        System.out.println("4. Per intervallo di date");
        System.out.print("Criterio scelto: ");
        String criterio = leggiInput();

        String codice = null, nomeCompleto = null, titolo = null;
        String dataInizio = null, dataFine = null;

        switch (criterio) {
            case "1":
                System.out.print("Codice univoco: ");
                codice = leggiInput();
                break;
            case "2":
                System.out.print("Nome del cliente: ");
                String nome = leggiInput();
                System.out.print("Cognome del cliente: ");
                String cognome = leggiInput();
                nomeCompleto = (nome + " " + cognome).trim();
                break;
            case "3":
                System.out.print("Titolo (anche parziale): ");
                titolo = leggiInput();
                break;
            case "4":
                System.out.print("Data inizio (yyyy-MM-dd): ");
                dataInizio = leggiInput();
                System.out.print("Data fine (yyyy-MM-dd): ");
                dataFine = leggiInput();
                break;
            default:
                System.out.println("Criterio non valido.");
                return;
        }

        List<Prenotazione> risultati = gestore.cercaPrenotazione(codice, nomeCompleto, titolo, dataInizio, dataFine);
        if (risultati.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata.");
            return;
        }
        System.out.println("\n--- RISULTATI (" + risultati.size() + ") ---");
        for (Prenotazione p : risultati) {
            stampaPrenotazione(p);
        }

        System.out.print("\nVuoi visualizzare i dettagli completi di una prenotazione? (Y/N): ");
        if (leggiInput().equalsIgnoreCase("Y")) {
            System.out.print("Codice prenotazione: ");
            visualizzaDettagliPrenotazione(leggiInput());
        }
    }

    /**
     * Stampa una riga compatta con i dati principali di una prenotazione.
     *
     * @param p La prenotazione da stampare.
     */
    private void stampaPrenotazione(Prenotazione p) {
        System.out.printf("Codice: %s | Cliente: %s %s | Film: %s | Data: %s | Posti: %d | Totale: %.2f€%n",
                p.getCodiceUnivoco(),
                p.getCliente().getNome(),
                p.getCliente().getCognome(),
                p.getProiezione().getTitolo(),
                p.getProiezione().getDataOra(),
                p.getNumeroPosti(),
                p.getCostoTotale());
    }
}