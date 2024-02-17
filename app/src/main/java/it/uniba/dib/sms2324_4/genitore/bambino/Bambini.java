package it.uniba.dib.sms2324_4.genitore.bambino;

public class Bambini {

    String nome;
    String cognome;
    String cf;
    String dataDiNascita;

    public Bambini(String nome, String cognome, String cf, String dataDiNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.dataDiNascita = dataDiNascita;
    }

    public Bambini(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getDataDiNascita() {
        return dataDiNascita;
    }

    public void setDataDiNascita(String dataDiNascita) {
        this.dataDiNascita = dataDiNascita;
    }
}
