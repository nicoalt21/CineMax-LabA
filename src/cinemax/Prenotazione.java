package cinemax;

/**
 * Rappresenta la transazione di acquisto di uno o più biglietti per una proiezione.
 * Agisce come classe di associazione mettendo in relazione un Utente e una Proiezione.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Prenotazione {

    private String codiceUnivoco;
    private Utente cliente;
    private Proiezione proiezione;
    private int numeroPosti;
    private double costoTotale;

    /**
     * Crea una nuova istanza di prenotazione.
     * Il costo totale viene calcolato dinamicamente moltiplicando il numero di posti richiesti
     * per il prezzo base definito nella proiezione.
     *
     * @param codiceUnivoco Identificativo alfanumerico univoco generato al momento dell'acquisto.
     * @param cliente Il riferimento all'oggetto Utente che sta effettuando la prenotazione.
     * @param proiezione Il riferimento all'oggetto Proiezione per cui si riservano i posti.
     * @param numeroPosti La quantità di biglietti acquistati in questa singola transazione.
     */
    public Prenotazione(String codiceUnivoco, Utente cliente, Proiezione proiezione, int numeroPosti) {
        this.codiceUnivoco = codiceUnivoco;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroPosti = numeroPosti;
        // Il costo totale si ricava direttamente dai dati dell'oggetto proiezione
        this.costoTotale = numeroPosti * proiezione.getPrezzo();
    }

    public String getCodiceUnivoco() { return codiceUnivoco; }
    public Utente getCliente() { return cliente; }
    public Proiezione getProiezione() { return proiezione; }
    public int getNumeroPosti() { return numeroPosti; }
    public double getCostoTotale() { return costoTotale; }

    /**
     * Aggiorna il riferimento alla proiezione in caso di modifica della prenotazione (cambio data).
     * Ricalcola contestualmente il costo totale nel caso il nuovo film abbia un prezzo differente.
     *
     * @param nuovaProiezione L'oggetto Proiezione aggiornato scelto dall'utente.
     */
    public void setProiezione(Proiezione nuovaProiezione) {
        this.proiezione = nuovaProiezione;
        this.costoTotale = this.numeroPosti * nuovaProiezione.getPrezzo();
    }
}