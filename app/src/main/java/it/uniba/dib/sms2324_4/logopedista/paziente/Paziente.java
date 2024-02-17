package it.uniba.dib.sms2324_4.logopedista.paziente;

import it.uniba.dib.sms2324_4.genitore.bambino.Bambini;

public class Paziente extends Bambini {
    String cfGenitore;
    int esperienza;
    public Paziente(String nome, String cognome, String cf, String dataDiNascita ,
                    String cfGenitore , int esperienza) {
        super(nome, cognome, cf, dataDiNascita);
        this.cfGenitore = cfGenitore;
        this.esperienza = esperienza;
    }

    public Paziente(){
        super();
    }

    public String getCfGenitore() {
        return cfGenitore;
    }

    public void setCfGenitore(String cfGenitore) {
        this.cfGenitore = cfGenitore;
    }

    public int getEsperienza() {
        return esperienza;
    }

    public void setEsperienza(int esperienza) {
        this.esperienza = esperienza;
    }
}
