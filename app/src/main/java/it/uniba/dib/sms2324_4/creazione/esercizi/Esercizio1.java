package it.uniba.dib.sms2324_4.creazione.esercizi;

public class Esercizio1 {
    String aiuto_1, aiuto_2, aiuto_3, audio_soluzione, UriImage , id_esercizio ;
    int conta_assegnazioni = 0;

    public String getAiuto_1() {
        return aiuto_1;
    }

    public void setAiuto_1(String aiuto_1) {
        this.aiuto_1 = aiuto_1;
    }

    public String getAiuto_2() {
        return aiuto_2;
    }

    public void setAiuto_2(String aiuto_2) {
        this.aiuto_2 = aiuto_2;
    }

    public String getAiuto_3() {
        return aiuto_3;
    }

    public void setAiuto_3(String aiuto_3) {
        this.aiuto_3 = aiuto_3;
    }

    public String getAudio_soluzione() {
        return audio_soluzione;
    }

    public void setAudio_soluzione(String audio_soluzione) {
        this.audio_soluzione = audio_soluzione;
    }

    public String getUriImage() {
        return UriImage;
    }

    public void setUriImage(String uriImage) {
        UriImage = uriImage;
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

    public String getId_esercizio() {
        return id_esercizio;
    }

    public void setId_esercizio(String id_esercizio) {
        this.id_esercizio = id_esercizio;
    }

    int esperienza = 25;
    int monete = 10;
    boolean corretto , eseguito , esito;

    public boolean isEsito() {
        return esito;
    }

    public void setEsito(boolean esito) {
        this.esito = esito;
    }


    public Esercizio1(String aiuto_1, String aiuto_2, String aiuto_3, String audio_soluzione, String uriImage,
                      String id_esercizio, int conta_assegnazioni, int esperienza, int monete,
                      boolean corretto, boolean eseguito, boolean esito ) {
        this.aiuto_1 = aiuto_1;
        this.aiuto_2 = aiuto_2;
        this.aiuto_3 = aiuto_3;
        this.audio_soluzione = audio_soluzione;
        UriImage = uriImage;
        this.id_esercizio = id_esercizio;
        this.conta_assegnazioni = conta_assegnazioni;
        this.esperienza = esperienza;
        this.monete = monete;
        this.corretto = corretto;
        this.eseguito = eseguito;
        this.esito = esito;
    }

    public Esercizio1() {
    }


}
