package com.gogo.kotlinbtk;

public class VtSabitler {
    public static  final String VT_ADI="KAYITLARIM";

    public static  final int VT_VERSIYON = 1;

    public  static final String TABLO_ADI = "KAYITLARIM_TABLO";

    public static final String S_ID = "ID";
    public static final String S_KULLANICIADI = "KULLANICIADI";
    public static final String S_SIFRE="SIFRE";
    public static final String S_EMAIL="EMAIL";
    public static final String S_XKULLANICIADI="XKULLANICIADI";

    public static final String TABLO_OLUSTUR = "CREATE TABLE " + TABLO_ADI + "("
            + S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + S_KULLANICIADI + " TEXT, "
            + S_SIFRE + " TEXT, "
            + S_EMAIL + " TEXT, "
            + S_XKULLANICIADI + " TEXT"
            + ")" ;






















}
