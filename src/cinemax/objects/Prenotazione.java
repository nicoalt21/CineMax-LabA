package cinemax.objects;

/**
 * Rappresenta la transazione di acquisto di uno o più biglietti per una proiezione.
 * Agisce come classe di associazione mettendo in relazione un Utente e una Proiezione.
 * Il costo totale viene calcolato dinamicamente e non memorizzato come campo separato
 * per evitare disallineamenti in caso di modifica della proiezione associata.
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

    /**
     * Crea una nuova istanza di prenotazione.
     *
     * @param codiceUnivoco Identificativo alfanumerico univoco generato al momento dell'acquisto.
     * @param cliente       Il riferimento all'oggetto Utente che sta effettuando la prenotazione.
     * @param proiezione    Il riferimento all'oggetto Proiezione per cui si riservano i posti.
     * @param numeroPosti   La quantità di biglietti acquistati in questa singola transazione.
     */
    public Prenotazione(String codiceUnivoco, Utente cliente, Proiezione proiezione, int numeroPosti) {
        this.codiceUnivoco = codiceUnivoco;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroPosti = numeroPosti;
    }

    public String getCodiceUnivoco() { return codiceUnivoco; }
    public Utente getCliente()       { return cliente; }
    public Proiezione getProiezione() { return proiezione; }
    public int getNumeroPosti()      { return numeroPosti; }

    /**
     * Calcola il costo totale al momento della chiamata in base al prezzo corrente della proiezione.
     *
     * @return Costo totale della prenotazione.
     */
    public double getCostoTotale() {
        return numeroPosti * proiezione.getPrezzo();
    }

    /**
     * Aggiorna il riferimento alla proiezione in caso di modifica della prenotazione (cambio data).
     *
     * @param nuovaProiezione L'oggetto Proiezione aggiornato scelto dall'utente.
     */
    public void setProiezione(Proiezione nuovaProiezione) {
        this.proiezione = nuovaProiezione;
    }
}