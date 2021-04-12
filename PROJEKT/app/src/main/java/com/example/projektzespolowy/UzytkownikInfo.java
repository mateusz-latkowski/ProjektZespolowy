package com.example.projektzespolowy;

class UzytkownikInfo {

    private String Imie;
    private String Nazwisko;
    private String Telefon;
    private String Email;
    private String Przewodnik;
    private String Blokada;
    private String ID;

    public UzytkownikInfo() {}

    public UzytkownikInfo(String imie, String nazwisko, String telefon, String email, String przewodnik, String blokada, String id) {
        Imie = imie;
        Nazwisko = nazwisko;
        Telefon = telefon;
        Email = email;
        Przewodnik = przewodnik;
        Blokada = blokada;
        ID = id;
    }

    public String getImie() {
        return Imie;
    }

    public void setImie(String imie) {
        Imie = imie;
    }

    public String getNazwisko() {
        return Nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        Nazwisko = nazwisko;
    }

    public String getTelefon() {
        return Telefon;
    }

    public void setTelefon(String telefon) {
        Telefon = telefon;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPrzewodnik() {
        return Przewodnik;
    }

    public void setPrzewodnik(String przewodnik) {
        Przewodnik = przewodnik;
    }

    public String getBlokada() {
        return Blokada;
    }

    public void setBlokada(String blokada) {
        Blokada = blokada;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = id;
    }
}
