package it.uniba.dib.sms2324_4.genitore.menu.gioco.ui.shop;

public class Item {
    private int id;
    private String nome;
    private int immagineId;
    private int prezzo;
    private String descrizione;
    private boolean acquistato;
    private ItemType tipo;
    private boolean predefinito;
    private int animazioneId;

    // Enumerazione per distinguere tra personaggio, scenario e sfondo
    public enum ItemType {
        PERSONAGGIO,
        SCENARIO,
        SFONDO
    }

    // Costruttore con animazioneId come parametro opzionale
    public Item(int id, String nome, int immagineId, int prezzo, String descrizione, ItemType tipo, int animazioneId) {
        this.id = id;
        this.nome = nome;
        this.immagineId = immagineId;
        this.prezzo = prezzo;
        this.descrizione = descrizione;
        this.acquistato = false;
        this.tipo = tipo;
        this.predefinito = false;
        this.animazioneId = animazioneId;
    }

    // Costruttore senza animazioneId
    public Item(int id, String nome, int immagineId, int prezzo, String descrizione, ItemType tipo) {
        this(id, nome, immagineId, prezzo, descrizione, tipo, -1); // -1 indica che non c'è animazioneId
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

    public int getAnimazioneId() {
        return animazioneId;
    }

    public void setAnimazioneId(int animazioneId) {
        this.animazioneId = animazioneId;
    }
}
