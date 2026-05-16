package cinemax.objects;

/**
 * Rappresenta una singola proiezione cinematografica all'interno del palinsesto.
 * Identificata univocamente dalla data e ora della proiezione.
 * Invariante: per un cinema monosala la combinazione dataOra è necessariamente unica.
 * Il salvataggio delle prenotazioni deve sempre avvenire dopo qualsiasi modifica
 * alle proiezioni per garantire la consistenza delle chiavi di join nel CSV.
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
     * Costruisce una nuova proiezione con validazione dei campi numerici.
     *
     * @param dataOra   La data e l'ora della proiezione nel formato yyyy-MM-dd HH:mm:ss (chiave logica).
     * @param titolo    Il titolo del film.
     * @param genere    Il genere cinematografico.
     * @param regista   Il regista del film.
     * @param anno      L'anno di uscita del film (1888-2100).
     * @param durata    La durata in minuti (maggiore di zero).
     * @param etaMinima L'età minima richiesta per la visione (non negativa).
     * @param prezzo    Il costo del biglietto (non negativo).
     */
    public Proiezione(String dataOra, String titolo, String genere, String regista,
                      int anno, int durata, int etaMinima, double prezzo) {
        if (dataOra == null || dataOra.isBlank())
            throw new IllegalArgumentException("dataOra non valida");
        if (titolo == null || titolo.isBlank())
            throw new IllegalArgumentException("titolo non valido");
        if (anno < 1888 || anno > 2100)
            throw new IllegalArgumentException("anno non valido: " + anno);
        if (durata <= 0)
            throw new IllegalArgumentException("durata non valida: " + durata);
        if (etaMinima < 0)
            throw new IllegalArgumentException("eta minima non valida: " + etaMinima);
        if (prezzo < 0)
            throw new IllegalArgumentException("prezzo non valido: " + prezzo);

        this.dataOra = dataOra;
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.etaMinima = etaMinima;
        this.prezzo = prezzo;
    }

    public String getDataOra()  { return dataOra; }
    public String getTitolo()   { return titolo; }
    public String getGenere()   { return genere; }
    public String getRegista()  { return regista; }
    public int getAnno()        { return anno; }
    public int getDurata()      { return durata; }
    public int getEtaMinima()   { return etaMinima; }
    public double getPrezzo()   { return prezzo; }

    /**
     * Aggiorna la data e l'ora della proiezione in caso di rinvio.
     * Deve essere chiamato solo tramite GestoreDati.modificaProiezione per
     * garantire la consistenza della mappa e delle prenotazioni associate.
     *
     * @param dataOra La nuova stringa contenente data e ora.
     */
    public void setDataOra(String dataOra) { this.dataOra = dataOra; }

    /**
     * Aggiorna il prezzo del biglietto.
     *
     * @param prezzo Il nuovo costo del biglietto.
     */
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
}