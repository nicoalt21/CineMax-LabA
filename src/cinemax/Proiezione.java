package cinemax;

/**
 * Rappresenta una singola proiezione cinematografica all'interno del palinsesto.
 * Identificata univocamente dalla data e ora della proiezione.
 *
 * @author Alt Niccolò Jacopo, 762605, VA
 * @author Soldo Mateo, 760762, VA
 * @author Vignati Davide, 761134, VA
 */
public class Proiezione {

    private String dataOra;
    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int etaMinima;
    private double prezzo;

    /**
     * Costruisce una nuova proiezione.
     *
     * @param dataOra La data e l'ora della proiezione (chiave logica).
     * @param titolo Il titolo del film.
     * @param genere Il genere cinematografico.
     * @param regista Il regista del film.
     * @param anno L'anno di uscita del film.
     * @param durata La durata in minuti.
     * @param etaMinima L'età minima richiesta per la visione.
     * @param prezzo Il costo del biglietto.
     */
    public Proiezione(String dataOra, String titolo, String genere, String regista, int anno, int durata, int etaMinima, double prezzo) {
        this.dataOra = dataOra;
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.etaMinima = etaMinima;
        this.prezzo = prezzo;
    }

    public String getDataOra() { return dataOra; }
    public String getTitolo() { return titolo; }
    public String getGenere() { return genere; }
    public String getRegista() { return regista; }
    public int getAnno() { return anno; }
    public int getDurata() { return durata; }
    public int getEtaMinima() { return etaMinima; }
    public double getPrezzo() { return prezzo; }

    /**
     * Aggiorna la data e l'ora della proiezione in caso di rinvio.
     * @param dataOra La nuova stringa contenente data e ora.
     */
    public void setDataOra(String dataOra) { this.dataOra = dataOra; }

    /**
     * Aggiorna il prezzo del biglietto.
     * @param prezzo Il nuovo costo del biglietto.
     */
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
}