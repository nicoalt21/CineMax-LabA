package cinemax;

public class CineMax {
    public static void main(String[] args) {
        GestoreDati gestore = new GestoreDati();

        // gestore.caricaDati(); // Decommentare quando I/O file è pronto

        MenuPrincipale menu = new MenuPrincipale(gestore);
        menu.avvia();

        // gestore.salvaDati(); // Decommentare quando I/O file è pronto
    }
}