package cinemax;

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


    // --- 1. LOGIN E SMISTAMENTO RUOLI ---
    private void login() {
        System.out.println("\n--- LOGIN ---\n0. Per andare indietro");
        System.out.print("Username: ");
        String user = leggiInput();
        System.out.print("Password: ");
        String pswd = leggiInput();

        // If(gestore.verificaLogin(user, pswd);) switch(utente.trova(user).ruolo;){
        //      case "Cliente": menuCliente();
        //      case "Proiezionista": menuProiezionista();
        //      case "Bigliettaio" : menuBiglettaio();
        // }

    }

    // --- REGISTRAZIONE ---
    private void registraCliente() {
        System.out.println("\n--- REGISTRAZIONE NUOVO CLIENTE ---");
        System.out.print("Nome: "); String nome = leggiInput();
        System.out.print("Cognome: "); String cognome = leggiInput();
        System.out.print("Username: "); String user = leggiInput();
        System.out.print("Password: "); String pass = leggiInput();
        System.out.print("Data di Nascita (opzionale): "); String dataNascita = leggiInput();
        System.out.print("Luogo domicilio: "); String dom = leggiInput();

        // try{
        //      gestore.registraCliente(nome, cognome, user, pass, dataNascita, dom);
        // } catch (IOException e) {
        //            System.out.println("\nErrore di lettura dell'input. Riprova.\n");
        //            return "";
        System.out.println("Registrazione completata con successo! Ora puoi effettuare il login.");
    }

    // --- AREA GUEST ---
    private void menuGuest() {
        String scelta;
        do {
            System.out.println("\n--- MENU RICERCA PROIEZIONI ---");
            System.out.println("1. Nuova ricerca per Titolo");
            System.out.println("2. Filtra per Genere");
            System.out.println("3. Filtra per Intervallo di Date");
            System.out.println("4. Filtra per CostoSeleziona e Visualizza Dettagli Proiezione");
            System.out.println("5. Visualizza Dettagli Proiezione");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scelta: ");

            scelta = leggiInput();

            switch (scelta) {
                case "1":
                    System.out.print("Inserisci titolo: ");
                    // gestore.cercaProiezionePerTitolo(leggiInput());
                    break;
                case "2":
                    System.out.print("Inserisci genere: ");
                    // gestore.cercaProiezionePerGenere(leggiInput());
                    break;
                case "3":
                    System.out.println("Funzionalità: Filtro Date (da implementare in GestoreDati)");
                    break;
                case "4":
                    System.out.println("Funzionalità: Filtro Costo (da implementare in GestoreDati)");
                    break;
                case "5":
                    System.out.print("Inserisci l'ID o il nome della proiezione da visualizzare: ");
                    // gestore.visualizzaProiezione(leggiInput());
                    break;
                case "0": break;
                default: System.out.println("Opzione non valida.");
            }
        } while (!scelta.equals("0"));
    }
    // --- MENU CLIENTE ---
    private void menuCliente(String username) {}

    // --- MENU PROIEZIONISTA ---
    private void menuProiezionista() {}

    // --- MENU BIGLIETTAIO ---
    private void menuBigliettaio() {    }

}