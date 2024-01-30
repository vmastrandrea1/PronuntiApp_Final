package it.uniba.dib.sms2324_4;

import java.util.Date;

public class Genitori {
    String nome;
    String cognome;
    String cf;
    String dataDiNascita;
    String email;
    String password;

    Bambini figlio;

    public Genitori(String nome, String cognome, String cf, String dataDiNascita, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.dataDiNascita = dataDiNascita;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
