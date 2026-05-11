package cinemax;

public class Prenotazione {

    private String codiceUnivoco;
    private Utente cliente;
    private Proiezione proiezione;
    private int numeroPosti;
    private double costoTotale;

    public Prenotazione(String codiceUnivoco, Utente cliente, Proiezione proiezione, int numeroPosti) {
        this.codiceUnivoco = codiceUnivoco;
        this.cliente = cliente;
        this.proiezione = proiezione;
        this.numeroPosti = numeroPosti;
        // Il costo totale viene calcolato automaticamente senza doverlo passare al costruttore
        this.costoTotale = numeroPosti * proiezione.getPrezzo();
    }

    public String getCodiceUnivoco() { return codiceUnivoco; }
    public Utente getCliente() { return cliente; }
    public Proiezione getProiezione() { return proiezione; }
    public int getNumeroPosti() { return numeroPosti; }
    public double getCostoTotale() { return costoTotale; }

    public void setProiezione(Proiezione proiezione) {
        this.proiezione = proiezione;
    }
}