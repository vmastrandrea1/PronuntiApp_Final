package it.uniba.dib.sms2324_4.gioco.ui.shop;

public class Item {
    private int id;
    private String nome;
    private int immagineId;
    private int prezzo;
    private String descrizione;
    private boolean acquistato;
    private ItemType tipo;
    private boolean predefinito;


    // Enumerazione per distinguere tra personaggio, scenario e sfondo
    public enum ItemType {
        PERSONAGGIO,
        SCENARIO,
        SFONDO
    }

    public Item(int id, String nome, int immagineId, int prezzo, String descrizione, ItemType tipo) {
        this.id = id;
        this.nome = nome;
        this.immagineId = immagineId;
        this.prezzo = prezzo;
        this.descrizione = descrizione;
        this.acquistato = false;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getImmagineId() {
        return immagineId;
    }

    public int getPrezzo() {
        return prezzo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public boolean isAcquistato() {
        return acquistato;
    }

    public void setAcquistato(boolean acquistato) {
        this.acquistato = acquistato;
    }

    public ItemType getTipo() {
        return tipo;
    }

    public boolean isPredefinito() {
        return predefinito;
    }

    public void setPredefinito(boolean predefinito) {
        this.predefinito = predefinito;
    }
}
