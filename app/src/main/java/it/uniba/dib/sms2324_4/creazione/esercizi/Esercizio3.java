package it.uniba.dib.sms2324_4.creazione.esercizi;

public class Esercizio3 {
    String UriImage_corretta, UriImage_sbagliata , parola_immagine , id_esercizio;
    int conta_assegnazioni = 0;
    int esperienza = 50;
    int monete = 10;

    public boolean isEsito() {
        return esito;
    }

    public void setEsito(boolean esito) {
        this.esito = esito;
    }

    public String getParola_immagine() {
        return parola_immagine;
    }

    public void setParola_immagine(String parola_immagine) {
        this.parola_immagine = parola_immagine;
    }

    public Esercizio3(String uriImage_corretta, String uriImage_sbagliata, String id_esercizio,
                      int conta_assegnazioni, int esperienza, int monete,
                      boolean esito, String parola_immagine) {
        UriImage_corretta = uriImage_corretta;
        UriImage_sbagliata = uriImage_sbagliata;
        this.id_esercizio = id_esercizio;
        this.conta_assegnazioni = conta_assegnazioni;
        this.esperienza = esperienza;
        this.monete = monete;
        this.esito = esito;
        this.parola_immagine = parola_immagine;
    }

    boolean esito;



    public Esercizio3(){
        //Costruttore Vuoto
    }

    public String getUriImage_corretta() {
        return UriImage_corretta;
    }

    public void setUriImage_corretta(String uriImage_corretta) {
        UriImage_corretta = uriImage_corretta;
    }

    public String getUriImage_sbagliata() {
        return UriImage_sbagliata;
    }

    public void setUriImage_sbagliata(String uriImage_sbagliata) {
        UriImage_sbagliata = uriImage_sbagliata;
    }

    public String getId_esercizio() {
        return id_esercizio;
    }

    public void setId_esercizio(String id_esercizio) {
        this.id_esercizio = id_esercizio;
    }

    public int getConta_assegnazioni() {
        return conta_assegnazioni;
    }

    public void setConta_assegnazioni(int conta_assegnazioni) {
        this.conta_assegnazioni = conta_assegnazioni;
    }

    public int getEsperienza() {
        return esperienza;
    }

    public void setEsperienza(int esperienza) {
        this.esperienza = esperienza;
    }

    public int getMonete() {
        return monete;
    }

    public void setMonete(int monete) {
        this.monete = monete;
    }

}
