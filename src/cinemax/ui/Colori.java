package cinemax.ui;

/**
 * Costanti ANSI per la formattazione cromatica dell'output su terminale.
 * Schema minimale orientato alla leggibilità e alla semantica.
 * Supporta la disattivazione runtime per terminali non compatibili (es. console Eclipse legacy).
 * Per disabilitare i colori avviare con: java -Dcinemax.nocolor=true -jar CineMax.jar
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Colori {

    private static final boolean ABILITATI = !"true".equalsIgnoreCase(System.getProperty("cinemax.nocolor"));

    public static final String RESET           = ABILITATI ? "\u001B[0m"    : "";
    public static final String GRASSETTO       = ABILITATI ? "\u001B[1m"    : "";
    public static final String VERDE_GRASSETTO = ABILITATI ? "\u001B[1;32m" : "";
    public static final String ROSSO_GRASSETTO = ABILITATI ? "\u001B[1;31m" : "";
    public static final String GIALLO          = ABILITATI ? "\u001B[33m"   : "";
    public static final String GRIGIO          = ABILITATI ? "\u001B[90m"   : "";

    private Colori() {}
}