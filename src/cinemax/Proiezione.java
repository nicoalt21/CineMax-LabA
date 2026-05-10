package cinemax;

public class Proiezione {

    private int id; // Da rendere autoincrementale
    private int postiMassimi;
    private int postiDisponibili;
    private String dataOra;
    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durata;
    private int etaMinima;
    private double prezzo;

    public Proiezione(String dataOra, String titolo, String genere, String regista, int anno, int durata, int etaMinima, double prezzo) { //da inserire postiMassimi nella lista degli attributi
        this.dataOra = dataOra;
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durata = durata;
        this.etaMinima = etaMinima;
        this.prezzo = prezzo;
        // this.postiMassimi = postiMassimi;
        // postiDisponibili =postiMassimi;
    }

    public int getId() {return id; }
    public String getDataOra() { return dataOra; }
    public String getTitolo() { return titolo; }
    public String getGenere() { return genere; }
    public String getRegista() { return regista; }
    public int getAnno() { return anno; }
    public int getDurata() { return durata; }
    public int getEtaMinima() { return etaMinima; }
    public double getPrezzo() { return prezzo; }
    public int getPostiDisponibili() {
        return postiDisponibili;
    };
    public boolean effettuaPrenotazione(int numeroBiglietti){
        if(numeroBiglietti>postiDisponibili)return false;
        postiDisponibili=-numeroBiglietti;
        return true;
    }
    public void liberaPosti(int numeroBiglietti){
        postiDisponibili+=numeroBiglietti;
    }

    public void setDataOra(String dataOra) { this.dataOra = dataOra; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }
}