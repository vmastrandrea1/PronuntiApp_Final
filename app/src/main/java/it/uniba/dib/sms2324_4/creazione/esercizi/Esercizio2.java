package it.uniba.dib.sms2324_4.creazione.esercizi;

public class Esercizio2 {
    String parola_1, parola_2, parola_3, audio_soluzione , id_esercizio ;
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

    public Esercizio2(String parola_1, String parola_2, String parola_3, String audio_soluzione,
                      String id_esercizio, int conta_assegnazioni, int esperienza, int monete,
                      boolean corretto, boolean eseguito, boolean esito) {
        this.parola_1 = parola_1;
        this.parola_2 = parola_2;
        this.parola_3 = parola_3;
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

    public String getParola_1() {
        return parola_1;
    }

    public void setParola_1(String parola_1) {
        this.parola_1 = parola_1;
    }

    public String getParola_2() {
        return parola_2;
    }

    public void setParola_2(String parola_2) {
        this.parola_2 = parola_2;
    }

    public String getParola_3() {
        return parola_3;
    }

    public void setParola_3(String parola_3) {
        this.parola_3 = parola_3;
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
