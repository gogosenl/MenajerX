package com.gogo.kotlinbtk;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;



import androidx.annotation.Nullable;

public class VtHelper extends SQLiteOpenHelper {
    public VtHelper(@Nullable Context context ) {
        super(context, VtSabitler.VT_ADI, null, VtSabitler.VT_VERSIYON);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(VtSabitler.TABLO_OLUSTUR);
    }
    public User getUserDetailsByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + VtSabitler.TABLO_ADI + " WHERE " + VtSabitler.S_KULLANICIADI + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int kullaniciAdiIndex = cursor.getColumnIndex(VtSabitler.S_KULLANICIADI);
            int sifreIndex = cursor.getColumnIndex(VtSabitler.S_SIFRE);
            int emailIndex = cursor.getColumnIndex(VtSabitler.S_EMAIL);
            int xKullaniciAdiIndex = cursor.getColumnIndex(VtSabitler.S_XKULLANICIADI);

            if (kullaniciAdiIndex != -1 && sifreIndex != -1 && emailIndex != -1 && xKullaniciAdiIndex != -1) {
                String kullaniciAdi = cursor.getString(kullaniciAdiIndex);
                String sifre = cursor.getString(sifreIndex);
                String email = cursor.getString(emailIndex);
                String xKullaniciAdi = cursor.getString(xKullaniciAdiIndex);

                user = new User(kullaniciAdi, sifre, email, xKullaniciAdi);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return user;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + VtSabitler.TABLO_ADI);
        onCreate(sqLiteDatabase);
    }

    // Insert method renamed to insertUser
    public long insertUser(String kullaniciadi, String sifre, String email, String xkullaniciadi) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(VtSabitler.S_KULLANICIADI, kullaniciadi);
        values.put(VtSabitler.S_SIFRE, sifre);
        values.put(VtSabitler.S_EMAIL, email);
        values.put(VtSabitler.S_XKULLANICIADI, xkullaniciadi);
        long id = sqLiteDatabase.insert(VtSabitler.TABLO_ADI, null, values);
        sqLiteDatabase.close();
        return id;
    }


    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM KAYITLARIM_TABLO", null);
    }




    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM KAYITLARIM_TABLO WHERE kullaniciadi = ? AND sifre = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }



}
