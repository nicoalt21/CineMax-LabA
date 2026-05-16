package cinemax.ui;

/**
 * Costanti ANSI per la formattazione cromatica dell'output su terminale.
 * Schema minimale orientato alla leggibilità e alla semantica:
 * - GRASSETTO       : titoli e intestazioni
 * - VERDE_GRASSETTO : conferme e operazioni completate con successo
 * - ROSSO_GRASSETTO : errori e operazioni fallite
 * - GIALLO          : prompt e voci di menu interattive
 * - GRIGIO          : etichette di campo e testo descrittivo
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Colori {

    public static final String RESET           = "\u001B[0m";
    public static final String GRASSETTO       = "\u001B[1m";
    public static final String VERDE_GRASSETTO = "\u001B[1;32m";
    public static final String ROSSO_GRASSETTO = "\u001B[1;31m";
    public static final String GIALLO          = "\u001B[33m";
    public static final String GRIGIO          = "\u001B[90m";

    private Colori() {}
}