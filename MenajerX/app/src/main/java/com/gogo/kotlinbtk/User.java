package com.gogo.kotlinbtk;

public class User {
    private final String kullaniciAdi;
    private final String sifre;
    private final String email;
    private final String xKullaniciAdi;

    public User(String kullaniciAdi, String sifre, String email, String xKullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.email = email;
        this.xKullaniciAdi = xKullaniciAdi;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public String getEmail() {
        return email;
    }

    public String getXKullaniciAdi() {
        return xKullaniciAdi;
    }

    @Override
    public String toString() {
        return "KullaniciAdi: " + kullaniciAdi + "\\n" +
                "Email: " + email + "\\n" +
                "XKullaniciAdi: " + xKullaniciAdi;
    }
}



