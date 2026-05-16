package cinemax.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Classe di utilità per la gestione della crittografia delle password.
 * Utilizza l'algoritmo SHA-256 con encoding UTF-8 per garantire
 * compatibilità multipiattaforma degli hash prodotti.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Cifrario {

    /**
     * Riceve una password in chiaro e restituisce il digest SHA-256 codificato in Base64.
     * L'encoding UTF-8 è forzato esplicitamente per evitare comportamenti
     * dipendenti dalla piattaforma.
     *
     * @param password La password in chiaro da cifrare.
     * @return La stringa cifrata in Base64.
     * @throws RuntimeException Se l'algoritmo SHA-256 non è disponibile nella JVM.
     */
    public static String cifraPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore critico: algoritmo SHA-256 non disponibile", e);
        }
    }
}