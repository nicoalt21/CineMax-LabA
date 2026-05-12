package cinemax;

/**
 * Classe entry point del sistema CineMax.
 * Gestisce l'inizializzazione del gestore dati, l'avvio dell'interfaccia utente e il salvataggio finale.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class CineMax {

    /**
     * Metodo principale che avvia l'applicazione.
     *
     * @param args Argomenti da riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        GestoreDati gestore = new GestoreDati();
        gestore.caricaDati();

        MenuPrincipale menu = new MenuPrincipale(gestore);
        menu.avvia();

        gestore.salvaDati();
        System.out.println("Sistema CineMax chiuso correttamente. Dati salvati.");
    }
}



//-----------!!!!!!!!!!!!!!!!!!!!!! NON AVVIATE PER IL MOMENTO !!!!!!!!!!!!!!!!!!!!!!!!!!!!------------------------------