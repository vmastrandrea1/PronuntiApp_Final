package it.uniba.dib.sms2324_4.classParent;

import it.uniba.dib.sms2324_4.genitore.bambino.Bambini;

public class Figli extends Bambini {

    public String cfLogopedista;

    int esperienza = 0;
    int monete = 0;

    public Figli(String nome, String cognome, String cf, String dataDiNascita, String cfLogopedista, int esperienza, int monete) {
        super(nome, cognome, cf, dataDiNascita);
        this.cfLogopedista = cfLogopedista;
        this.esperienza = esperienza;
        this.monete = monete;
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

    public String getCfLogopedista() {
        return cfLogopedista;
    }

    public void setCfLogopedista(String cfLogopedista) {
        this.cfLogopedista = cfLogopedista;
    }
}
