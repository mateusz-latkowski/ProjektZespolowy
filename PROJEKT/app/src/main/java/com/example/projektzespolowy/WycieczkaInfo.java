package com.example.projektzespolowy;

public class WycieczkaInfo {

    private String Miejsce;
    private String Cena;
    private String Data;
    private String URL;
    private String ID;

    public WycieczkaInfo() {
    }

    public WycieczkaInfo(String miejsce, String cena, String data, String url, String id) {
        Miejsce = miejsce;
        Cena = cena;
        Data = data;
        URL = url;
        ID = id;
    }

    public String getMiejsce() {
        return Miejsce;
    }

    public void setMiejsce(String miejsce) {
        Miejsce = miejsce;
    }

    public String getCena() {
        return Cena;
    }

    public void setCena(String cena) {
        Cena = cena;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
