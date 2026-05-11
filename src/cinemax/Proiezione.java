package cinemax;

public class Proiezione {

    private String dataOra;
    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int etaMinima;
    private double prezzo;

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

    public void setDataOra(String dataOra) { this.dataOra = dataOra; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
}