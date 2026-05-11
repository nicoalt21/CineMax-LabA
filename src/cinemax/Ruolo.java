package cinemax;

/**
 * Definisce i livelli di autorizzazione e i permessi degli utenti nel sistema CineMax.
 * Utilizzato per il controllo degli accessi nei vari menu dell'applicazione.
 */
public enum Ruolo {

    /** * Utente standard. Permette l'acquisto, la visualizzazione e la modifica dei propri biglietti.
     */
    CLIENTE,

    /** * Membro dello staff front-office. Gestisce le ricerche e le verifiche delle prenotazioni di tutti i clienti.
     */
    BIGLIETTAIO,

    /** * Amministratore di sistema. Gestisce il palinsesto, aggiungendo, modificando o eliminando le proiezioni.
     */
    PROIEZIONISTA
}