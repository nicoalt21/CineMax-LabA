package cinemax;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Classe di utilità per la gestione della crittografia.
 */
public class Cifrario {

    /**
     * Riceve una password in chiaro e restituisce il digest cifrato in SHA-256 (codificato in Base64).
     *
     * @author Alt Niccolò Jacopo, 762605, VA
     * @author Soldo Mateo, 760762, VA
     * @author Vignati Davide, 761134, VA
     */
    public static String cifraPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore critico: Algoritmo di cifratura non trovato", e);
        }
    }
}