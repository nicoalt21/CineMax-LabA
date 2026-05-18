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
import java.time.format.DateTimeParseException;
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

    private static final String SEP       = "=========================================";
    private static final String SEP_CORTO = "-----------------------------------------";

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
            String s = in.readLine();
            return (s == null) ? "" : s;
        } catch (IOException e) {
            errore("Errore di lettura dell'input. Riprova.");
            return "";
        }
    }

    private void ok(String msg)      { System.out.println(Colori.VERDE_GRASSETTO + msg + Colori.RESET); }
    private void errore(String msg)  { System.out.println(Colori.ROSSO_GRASSETTO + msg + Colori.RESET); }
    private void titolo(String msg)  { System.out.println(Colori.GRASSETTO + msg + Colori.RESET); }
    private void voce(String msg)    { System.out.println(Colori.GIALLO + msg + Colori.RESET); }
    private void prompt(String msg)  { System.out.print(Colori.GIALLO + msg + Colori.RESET); }
    private void campo(String etichetta, String valore) {
        System.out.println(Colori.GRIGIO + etichetta + Colori.RESET + valore);
    }

    /**
     * Avvia il loop principale dell'interfaccia testuale.
     * Rimane attivo fino a quando l'utente non seleziona l'opzione di uscita.
     */
    public void avvia() {
        String scelta;
        do {
            System.out.println("\n" + SEP);
            titolo("              CINEMAX");
            System.out.println(SEP);
            voce("  1.  Login");
            voce("  2.  Registrazione");
            voce("  3.  Accesso Guest");
            voce("  0.  Esci");
            prompt("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": login(); break;
                case "2": registraCliente(); break;
                case "3": ingressoGuest(); break;
                case "0": ok("Chiusura in corso."); break;
                default:  errore("Opzione non valida.");
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
        System.out.println("\n" + SEP);
        titolo("  LOGIN");
        System.out.println(SEP);
        System.out.println(Colori.GRIGIO + "Digita 'back' per tornare al menu principale." + Colori.RESET);

        while (true) {
            prompt("\nUsername: ");
            String username = leggiInput();
            if (username.equals("back")) return;

            prompt("Password: ");
            String password = leggiInput();
            if (password.equals("back")) return;

            Utente utenteAutenticato = gestore.autenticaUtente(username, password);

            if (utenteAutenticato != null) {
                ok("\nAccesso effettuato. Benvenuto/a, " + utenteAutenticato.getNome() + ".");
                switch (utenteAutenticato.getRuolo()) {
                    case CLIENTE:       menuCliente(utenteAutenticato); break;
                    case BIGLIETTAIO:   menuBigliettaio(username); break;
                    case PROIEZIONISTA: menuProiezionista(username); break;
                    default: errore("Ruolo non riconosciuto.");
                }
                return;
            } else {
                errore("Credenziali non valide. Riprova.");
            }
        }
    }

    /**
     * Gestisce la registrazione di un nuovo cliente tramite loop iterativo.
     * Evita la ricorsione per prevenire stack overflow su errori ripetuti.
     */
    private void registraCliente() {
        while (true) {
            System.out.println("\n" + SEP);
            titolo("  REGISTRAZIONE NUOVO CLIENTE");
            System.out.println(SEP);
            System.out.println(Colori.GRIGIO + "I campi con * sono obbligatori. Digita 'back' per tornare." + Colori.RESET);
            System.out.println();

            prompt("* Nome: ");
            String nome = leggiInput();
            if (nome.equals("back")) return;

            prompt("* Cognome: ");
            String cognome = leggiInput();
            if (cognome.equals("back")) return;

            prompt("  Data di nascita (yyyy-MM-dd): ");
            String dataNascita = leggiInput();
            if (dataNascita.equals("back")) return;

            prompt("* Domicilio: ");
            String dom = leggiInput();
            if (dom.equals("back")) return;

            prompt("* Username: ");
            String user = leggiInput();
            if (user.equals("back")) return;

            prompt("* Password: ");
            String passwordInChiaro = leggiInput();
            if (passwordInChiaro.equals("back")) return;

            try {
                Utente nuovoUtente = new Utente(
                        nome, cognome, user,
                        Cifrario.cifraPassword(passwordInChiaro),
                        dataNascita, dom, Ruolo.CLIENTE
                );

                if (gestore.registraCliente(nuovoUtente)) {
                    ok("\nRegistrazione completata.");
                    System.out.println();
                    voce("  1.  Effettua il login");
                    voce("  2.  Registra un altro cliente");
                    voce("  3.  Torna al menu principale");
                    prompt("\nSeleziona un'opzione: ");
                    switch (leggiInput()) {
                        case "1": login(); return;
                        case "2": continue;
                        default:  return;
                    }
                } else {
                    errore("Username già in uso.");
                }
            } catch (IllegalArgumentException e) {
                errore("Registrazione fallita: " + e.getMessage());
            }
        }
    }

    // ============================================================
    // MENU GUEST
    // ============================================================

    /**
     * Ingresso guest come da specifica: chiede prima un nome di film (anche parziale).
     * Se il titolo è fornito, mostra le proiezioni corrispondenti.
     * In ogni caso prosegue verso il menu di ricerca completo.
     */
    private void ingressoGuest() {
        System.out.println("\n" + SEP);
        titolo("  ACCESSO GUEST");
        System.out.println(SEP);
        System.out.println(Colori.GRIGIO + "Inserisci il nome (anche parziale) di un film, o Invio per saltare." + Colori.RESET);
        prompt("\nTitolo: ");
        String titoloIniziale = leggiInput();

        if (!titoloIniziale.isEmpty()) {
            try {
                List<Proiezione> trovate = gestore.cercaProiezione(titoloIniziale, null, null, null, null, null);
                stampaListaProiezioni(trovate);
            } catch (DateTimeParseException e) {
                errore("Errore nella ricerca: " + e.getMessage());
            }
        }
        menuGuest();
    }

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
            System.out.println("\n" + SEP);
            titolo("  RICERCA PROIEZIONI");
            System.out.println(SEP);
            System.out.println(Colori.GRIGIO + "Filtri attivi:" + Colori.RESET);
            campo("  Titolo : ", titolo  != null ? titolo  : Colori.GRIGIO + "qualsiasi" + Colori.RESET);
            campo("  Genere : ", genere  != null ? genere  : Colori.GRIGIO + "qualsiasi" + Colori.RESET);
            campo("  Da     : ", dataInizio != null ? dataInizio : Colori.GRIGIO + "qualsiasi" + Colori.RESET);
            campo("  A      : ", dataFine   != null ? dataFine   : Colori.GRIGIO + "qualsiasi" + Colori.RESET);
            campo("  Prezzo : ",
                    (prezzoMin != null ? prezzoMin + " €" : Colori.GRIGIO + "qualsiasi" + Colori.RESET) +
                            Colori.GRIGIO + "  ->  " + Colori.RESET +
                            (prezzoMax != null ? prezzoMax + " €" : Colori.GRIGIO + "qualsiasi" + Colori.RESET));
            System.out.println(SEP_CORTO);
            voce("  1.  Imposta titolo");
            voce("  2.  Imposta genere");
            voce("  3.  Imposta intervallo date");
            voce("  4.  Imposta intervallo prezzo");
            voce("  5.  Esegui ricerca");
            voce("  6.  Azzera filtri");
            voce("  7.  Visualizza dettagli proiezione");
            voce("  0.  Torna indietro");
            prompt("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    prompt("Titolo (Invio per rimuovere): ");
                    String t = leggiInput();
                    titolo = t.isEmpty() ? null : t;
                    break;
                case "2":
                    prompt("Genere (Invio per rimuovere): ");
                    String g = leggiInput();
                    genere = g.isEmpty() ? null : g;
                    break;
                case "3":
                    prompt("Data inizio (yyyy-MM-dd, Invio per saltare): ");
                    String di = leggiInput();
                    dataInizio = di.isEmpty() ? null : di;
                    prompt("Data fine   (yyyy-MM-dd, Invio per saltare): ");
                    String df = leggiInput();
                    dataFine = df.isEmpty() ? null : df;
                    break;
                case "4":
                    while (true) {
                        prompt("Prezzo minimo (Invio per saltare): ");
                        String pMin = leggiInput();
                        if (pMin.isEmpty()) { prezzoMin = null; break; }
                        try { prezzoMin = Double.parseDouble(pMin.replace(",", ".")); break; }
                        catch (NumberFormatException e) { errore("Valore non valido."); }
                    }
                    while (true) {
                        prompt("Prezzo massimo (Invio per saltare): ");
                        String pMax = leggiInput();
                        if (pMax.isEmpty()) { prezzoMax = null; break; }
                        try { prezzoMax = Double.parseDouble(pMax.replace(",", ".")); break; }
                        catch (NumberFormatException e) { errore("Valore non valido."); }
                    }
                    break;
                case "5":
                    try {
                        List<Proiezione> risultati = gestore.cercaProiezione(
                                titolo, genere, dataInizio, dataFine, prezzoMin, prezzoMax);
                        stampaListaProiezioni(risultati);
                    } catch (DateTimeParseException e) {
                        errore("Formato data non valido. Usare yyyy-MM-dd.");
                    }
                    break;
                case "6":
                    titolo = null; genere = null;
                    dataInizio = null; dataFine = null;
                    prezzoMin = null; prezzoMax = null;
                    ok("Filtri azzerati.");
                    break;
                case "7":
                    prompt("Data e ora (yyyy-MM-dd HH:mm:ss): ");
                    visualizzaDettagliProiezione(leggiInput());
                    break;
                case "0":
                    break;
                default:
                    errore("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Stampa una lista compatta di proiezioni con posti liberi.
     *
     * @param lista La lista di proiezioni da visualizzare.
     */
    private void stampaListaProiezioni(List<Proiezione> lista) {
        System.out.println();
        if (lista == null || lista.isEmpty()) {
            errore("Nessuna proiezione trovata.");
            return;
        }
        titolo("Trovate " + lista.size() + " proiezioni:");
        System.out.println(SEP_CORTO);
        for (Proiezione p : lista) {
            System.out.printf("  [%s]  %s  %s%s%s  %.2f €  %sposti liberi: %s%d%n",
                    p.getDataOra(),
                    p.getTitolo(),
                    Colori.GRIGIO, p.getGenere(), Colori.RESET,
                    p.getPrezzo(),
                    Colori.GRIGIO, Colori.RESET,
                    gestore.calcolaPostiLiberi(p.getDataOra()));
        }
        System.out.println(SEP_CORTO);
    }

    /**
     * Visualizza il dettaglio completo di una proiezione identificata per data e ora.
     *
     * @param dataOra La chiave di ricerca nel formato yyyy-MM-dd HH:mm:ss.
     */
    private void visualizzaDettagliProiezione(String dataOra) {
        Proiezione p = gestore.ottieniProiezione(dataOra);
        if (p == null) {
            errore("Nessuna proiezione trovata per: " + dataOra);
            return;
        }
        System.out.println("\n" + SEP);
        titolo("  DETTAGLI PROIEZIONE");
        System.out.println(SEP);
        campo("  Titolo     : ", p.getTitolo());
        campo("  Regista    : ", p.getRegista());
        campo("  Genere     : ", p.getGenere());
        campo("  Anno       : ", String.valueOf(p.getAnno()));
        campo("  Durata     : ", p.getDurata() + " min");
        campo("  Data e Ora : ", p.getDataOra());
        campo("  Prezzo     : ", String.format("%.2f €", p.getPrezzo()));
        campo("  Età minima : ", p.getEtaMinima() + " anni");
        campo("  Posti lib. : ", String.valueOf(gestore.calcolaPostiLiberi(p.getDataOra())));
        System.out.println(SEP);
    }

    /**
     * Visualizza il dettaglio completo di una prenotazione identificata per codice univoco.
     *
     * @param codiceUnivoco Il codice alfanumerico della prenotazione.
     */
    private void visualizzaDettagliPrenotazione(String codiceUnivoco) {
        Prenotazione p = gestore.ottieniPrenotazione(codiceUnivoco);
        if (p == null) {
            errore("Nessuna prenotazione trovata con il codice: " + codiceUnivoco);
            return;
        }
        System.out.println("\n" + SEP);
        titolo("  DETTAGLI PRENOTAZIONE");
        System.out.println(SEP);
        campo("  Codice     : ", p.getCodiceUnivoco());
        campo("  Cliente    : ", p.getCliente().getNome() + " " + p.getCliente().getCognome());
        campo("  Username   : ", p.getCliente().getUsername());
        campo("  Film       : ", p.getProiezione().getTitolo());
        campo("  Data e Ora : ", p.getProiezione().getDataOra());
        campo("  Biglietti  : ", String.valueOf(p.getNumeroPosti()));
        campo("  Unitario   : ", String.format("%.2f €", p.getProiezione().getPrezzo()));
        campo("  Totale     : ", String.format("%.2f €", p.getCostoTotale()));
        System.out.println(SEP);
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
            System.out.println("\n" + SEP);
            titolo("  AREA CLIENTE - " + cliente.getUsername());
            System.out.println(SEP);
            voce("  1.  Cerca proiezioni");
            voce("  2.  Nuova prenotazione");
            voce("  3.  Le mie prenotazioni");
            voce("  4.  Modifica prenotazione");
            voce("  5.  Cancella prenotazione");
            voce("  0.  Logout");
            prompt("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": menuGuest(); break;
                case "2": creaPrenotazione(cliente); break;
                case "3": visualizzaPrenotazioniCliente(cliente); break;
                case "4": modificaPrenotazioneCliente(); break;
                case "5": eliminaPrenotazioneCliente(); break;
                case "0": ok("Logout effettuato."); break;
                default:  errore("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Guida il cliente nella creazione di una nuova prenotazione.
     *
     * @param cliente L'utente che sta effettuando la prenotazione.
     */
    private void creaPrenotazione(Utente cliente) {
        System.out.println("\n" + SEP);
        titolo("  NUOVA PRENOTAZIONE");
        System.out.println(SEP);
        prompt("Data e ora proiezione (yyyy-MM-dd HH:mm:ss): ");
        String dataOra = leggiInput();

        Proiezione p = gestore.ottieniProiezione(dataOra);
        if (p == null) {
            errore("Proiezione non trovata.");
            return;
        }

        System.out.println(SEP_CORTO);
        campo("  Film       : ", p.getTitolo());
        campo("  Data e Ora : ", p.getDataOra());
        campo("  Posti lib. : ", String.valueOf(gestore.calcolaPostiLiberi(dataOra)));
        campo("  Prezzo     : ", String.format("%.2f €", p.getPrezzo()));
        System.out.println(SEP_CORTO);
        prompt("Numero posti: ");

        int posti;
        try {
            posti = Integer.parseInt(leggiInput());
        } catch (NumberFormatException e) {
            errore("Numero non valido.");
            return;
        }
        if (posti <= 0) {
            errore("Il numero di posti deve essere positivo.");
            return;
        }

        String codice = gestore.creaPrenotazione(cliente, p, posti);
        if (codice == null) {
            errore("Prenotazione fallita: posti insufficienti, proiezione già passata o età minima non rispettata.");
        } else {
            ok("\nPrenotazione confermata.");
            campo("  Codice     : ", codice);
            campo("  Totale     : ", String.format("%.2f €", posti * p.getPrezzo()));
        }
    }

    /**
     * Mostra tutte le prenotazioni attive del cliente autenticato.
     *
     * @param cliente L'utente di cui visualizzare le prenotazioni.
     */
    private void visualizzaPrenotazioniCliente(Utente cliente) {
        List<Prenotazione> prenotazioni = gestore.visualizzaPrenotazioni(cliente);
        System.out.println("\n" + SEP);
        titolo("  LE MIE PRENOTAZIONI");
        System.out.println(SEP);
        if (prenotazioni.isEmpty()) {
            errore("Nessuna prenotazione attiva.");
            return;
        }
        for (Prenotazione p : prenotazioni) {
            stampaPrenotazione(p);
        }
        System.out.println(SEP_CORTO);
    }

    /**
     * Guida il cliente nella modifica della data di una prenotazione esistente.
     */
    private void modificaPrenotazioneCliente() {
        System.out.println("\n" + SEP);
        titolo("  MODIFICA PRENOTAZIONE");
        System.out.println(SEP);
        prompt("Codice prenotazione: ");
        String codice = leggiInput();
        prompt("Nuova data e ora (yyyy-MM-dd HH:mm:ss): ");
        String nuovaDataOra = leggiInput();

        if (gestore.modificaPrenotazione(codice, nuovaDataOra)) {
            ok("Prenotazione modificata con successo.");
        } else {
            errore("Modifica fallita: codice errato, date non future, proiezione inesistente o posti insufficienti.");
        }
    }

    /**
     * Guida il cliente nell'eliminazione di una prenotazione futura.
     */
    private void eliminaPrenotazioneCliente() {
        System.out.println("\n" + SEP);
        titolo("  CANCELLA PRENOTAZIONE");
        System.out.println(SEP);
        prompt("Codice prenotazione: ");
        String codice = leggiInput();

        if (gestore.eliminaPrenotazione(codice)) {
            ok("Prenotazione eliminata con successo.");
        } else {
            errore("Eliminazione fallita: codice errato o proiezione già passata.");
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
            System.out.println("\n" + SEP);
            titolo("  PANNELLO PROIEZIONISTA - " + username);
            System.out.println(SEP);
            voce("  1.  Inserisci nuova proiezione");
            voce("  2.  Modifica data proiezione");
            voce("  3.  Elimina proiezione");
            voce("  0.  Logout");
            prompt("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": aggiungiProiezione(); break;
                case "2": modificaProiezione(); break;
                case "3": eliminaProiezione(); break;
                case "0": ok("Logout effettuato."); break;
                default:  errore("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Guida il proiezionista nell'inserimento di una nuova proiezione nel palinsesto.
     */
    private void aggiungiProiezione() {
        System.out.println("\n" + SEP);
        titolo("  NUOVA PROIEZIONE");
        System.out.println(SEP);
        prompt("Titolo: ");          String titolo = leggiInput();
        prompt("Genere: ");          String genere = leggiInput();
        prompt("Regista: ");         String regista = leggiInput();
        prompt("Anno: ");            String anno = leggiInput();
        prompt("Durata (min): ");    String durata = leggiInput();
        prompt("Età minima: ");      String etaMinima = leggiInput();
        prompt("Data e ora (yyyy-MM-dd HH:mm:ss): "); String dataOra = leggiInput();
        prompt("Prezzo (€): ");      String costo = leggiInput();

        try {
            Proiezione p = new Proiezione(
                    dataOra, titolo, genere, regista,
                    Integer.parseInt(anno),
                    Integer.parseInt(durata),
                    Integer.parseInt(etaMinima),
                    Double.parseDouble(costo.replace(",", "."))
            );
            if (gestore.aggiungiProiezione(p)) {
                ok("Proiezione aggiunta con successo.");
            } else {
                errore("Inserimento fallito: orario già occupato o sovrapposizione con altra proiezione in sala.");
            }
        } catch (NumberFormatException e) {
            errore("Valori numerici non validi per anno, durata, età o prezzo.");
        } catch (IllegalArgumentException e) {
            errore(e.getMessage());
        }
    }

    /**
     * Guida il proiezionista nella modifica della data di una proiezione senza prenotazioni.
     */
    private void modificaProiezione() {
        System.out.println("\n" + SEP);
        titolo("  MODIFICA DATA PROIEZIONE");
        System.out.println(SEP);
        prompt("Data e ora attuale (yyyy-MM-dd HH:mm:ss): ");
        String dataOraAttuale = leggiInput();
        prompt("Nuova data e ora  (yyyy-MM-dd HH:mm:ss): ");
        String nuovaDataOra = leggiInput();

        if (gestore.modificaProiezione(dataOraAttuale, nuovaDataOra)) {
            ok("Proiezione modificata con successo.");
        } else {
            errore("Modifica fallita: proiezione inesistente, orario occupato, sovrapposizione o prenotazioni attive.");
        }
    }

    /**
     * Guida il proiezionista nell'eliminazione di una proiezione senza prenotazioni.
     */
    private void eliminaProiezione() {
        System.out.println("\n" + SEP);
        titolo("  ELIMINA PROIEZIONE");
        System.out.println(SEP);
        prompt("Data e ora (yyyy-MM-dd HH:mm:ss): ");
        String idElimina = leggiInput();

        if (gestore.eliminaProiezione(idElimina)) {
            ok("Proiezione eliminata con successo.");
        } else {
            errore("Eliminazione fallita: proiezione inesistente o prenotazioni attive.");
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
            System.out.println("\n" + SEP);
            titolo("  TERMINALE BIGLIETTERIA - " + username);
            System.out.println(SEP);
            voce("  1.  Prenotazioni odierne");
            voce("  2.  Cerca prenotazione");
            voce("  0.  Logout");
            prompt("\nSeleziona un'opzione: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1": visualizzaPrenotazioniOdierne(); break;
                case "2": cercaPrenotazione(); break;
                case "0": ok("Logout effettuato."); break;
                default:  errore("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }

    /**
     * Mostra tutte le prenotazioni con proiezione nella data odierna.
     */
    private void visualizzaPrenotazioniOdierne() {
        List<Prenotazione> oggi = gestore.ottieniPrenotazioniOggi();
        System.out.println("\n" + SEP);
        titolo("  PRENOTAZIONI ODIERNE");
        System.out.println(SEP);
        if (oggi.isEmpty()) {
            errore("Nessuna prenotazione per oggi.");
            return;
        }
        titolo("  Totale: " + oggi.size());
        System.out.println(SEP_CORTO);
        for (Prenotazione p : oggi) {
            stampaPrenotazione(p);
        }
        System.out.println(SEP_CORTO);
    }

    /**
     * Guida il bigliettaio nella ricerca di prenotazioni tramite criteri multipli.
     */
    private void cercaPrenotazione() {
        System.out.println("\n" + SEP);
        titolo("  RICERCA PRENOTAZIONE");
        System.out.println(SEP);
        voce("  1.  Per codice");
        voce("  2.  Per nome e cognome cliente");
        voce("  3.  Per titolo film");
        voce("  4.  Per intervallo di date");
        prompt("\nSeleziona un'opzione: ");
        String criterio = leggiInput();

        String codice = null, nomeCompleto = null, titolo = null;
        String dataInizio = null, dataFine = null;

        switch (criterio) {
            case "1":
                prompt("Codice: ");
                codice = leggiInput();
                break;
            case "2":
                prompt("Nome: ");
                String nome = leggiInput();
                prompt("Cognome: ");
                String cognome = leggiInput();
                nomeCompleto = (nome + " " + cognome).trim();
                break;
            case "3":
                prompt("Titolo (parziale): ");
                titolo = leggiInput();
                break;
            case "4":
                prompt("Data inizio (yyyy-MM-dd): ");
                dataInizio = leggiInput();
                prompt("Data fine   (yyyy-MM-dd): ");
                dataFine = leggiInput();
                break;
            default:
                errore("Criterio non valido.");
                return;
        }

        List<Prenotazione> risultati;
        try {
            risultati = gestore.cercaPrenotazione(codice, nomeCompleto, titolo, dataInizio, dataFine);
        } catch (DateTimeParseException e) {
            errore("Formato data non valido. Usare yyyy-MM-dd.");
            return;
        }

        if (risultati.isEmpty()) {
            errore("Nessuna prenotazione trovata.");
            return;
        }
        System.out.println();
        titolo("  Risultati: " + risultati.size());
        System.out.println(SEP_CORTO);
        for (Prenotazione p : risultati) {
            stampaPrenotazione(p);
        }
        System.out.println(SEP_CORTO);

        prompt("\nVisualizza dettagli completi? (Y/N): ");
        if (leggiInput().equalsIgnoreCase("Y")) {
            prompt("Codice prenotazione: ");
            visualizzaDettagliPrenotazione(leggiInput());
        }
    }

    /**
     * Stampa una riga compatta con i dati principali di una prenotazione.
     *
     * @param p La prenotazione da stampare.
     */
    private void stampaPrenotazione(Prenotazione p) {
        System.out.printf("  [%s]  %s %s  %s%s%s  %s%s%s  posti: %d  %.2f €%n",
                p.getCodiceUnivoco(),
                p.getCliente().getNome(),
                p.getCliente().getCognome(),
                Colori.GRIGIO, p.getProiezione().getTitolo(), Colori.RESET,
                Colori.GRIGIO, p.getProiezione().getDataOra(), Colori.RESET,
                p.getNumeroPosti(),
                p.getCostoTotale());
    }
}