package it.uniba.dib.sms2324_4.genitore.bambino;

public class Skin {

    int idSkin;
    int costo;

    public Skin(int idSkin, int costo) {
        this.idSkin = idSkin;
        this.costo = costo;
    }

    public Skin() {
        //Empty
    }

    public int getIdSkin() {
        return idSkin;
    }

    public void setIdSkin(int idSkin) {
        this.idSkin = idSkin;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }
}
