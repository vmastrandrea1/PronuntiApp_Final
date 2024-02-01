package it.uniba.dib.sms2324_4.creazione.esercizi;

public class Esercizio2 {
    String frase_1, frase_2, frase_3, audio_soluzione , id_esercizio ;
    int conta_assegnazioni = 0;
    int esperienza = 25;
    int monete = 10;
    boolean corretto;
    boolean eseguito;

    public boolean isEsito() {
        return esito;
    }

    public void setEsito(boolean esito) {
        this.esito = esito;
    }

    boolean esito;

    public Esercizio2(String frase_1, String frase_2, String frase_3, String audio_soluzione,
                      String id_esercizio, int conta_assegnazioni, int esperienza, int monete,
                      boolean corretto, boolean eseguito, boolean esito) {
        this.frase_1 = frase_1;
        this.frase_2 = frase_2;
        this.frase_3 = frase_3;
        this.audio_soluzione = audio_soluzione;
        this.id_esercizio = id_esercizio;
        this.conta_assegnazioni = conta_assegnazioni;
        this.esperienza = esperienza;
        this.monete = monete;
        this.corretto = corretto;
        this.eseguito = eseguito;
        this.esito = esito;
    }

    public Esercizio2(){
        //Costruttore Vuoto
    }

    public String getFrase_1() {
        return frase_1;
    }

    public void setFrase_1(String frase_1) {
        this.frase_1 = frase_1;
    }

    public String getFrase_2() {
        return frase_2;
    }

    public void setFrase_2(String frase_2) {
        this.frase_2 = frase_2;
    }

    public String getFrase_3() {
        return frase_3;
    }

    public void setFrase_3(String frase_3) {
        this.frase_3 = frase_3;
    }

    public String getAudio_soluzione() {
        return audio_soluzione;
    }

    public void setAudio_soluzione(String audio_soluzione) {
        this.audio_soluzione = audio_soluzione;
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

    public boolean isCorretto() {
        return corretto;
    }

    public void setCorretto(boolean corretto) {
        this.corretto = corretto;
    }

    public boolean isEseguito() {
        return eseguito;
    }

    public void setEseguito(boolean eseguito) {
        this.eseguito = eseguito;
    }
}
