package it.uniba.dib.sms2324_4;

public class Paziente extends Bambini {
    String cfGenitore;
    public Paziente(String nome, String cognome, String cf, String dataDiNascita , String cfGenitore) {
        super(nome, cognome, cf, dataDiNascita);
        this.cfGenitore = cfGenitore;
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

}
