package com.gogo.kotlinbtk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainSecim : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val username = intent.getStringExtra("USERNAME")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")/////////
        val user = sqliteHelper.getUserDetailsByUsername(username)///////

        setContentView(R.layout.activity_main_secim)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button10=findViewById<Button>(R.id.btnetkilesimgoruntule)
        button10.setOnClickListener {

            val intent= Intent(this,MainEtkilesimGoruntule::class.java)
            intent.putExtra("USERNAME", user.getKullaniciAdi())
            intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())////////

            startActivity(intent)

        }


        val button15=findViewById<Button>(R.id.btnyorum)
        button15.setOnClickListener {

            val intent= Intent(this,YapayZeka2::class.java)
            intent.putExtra("USERNAME", user.getKullaniciAdi())
            intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())////////
            startActivity(intent)

        }
        val button11=findViewById<Button>(R.id.btnpopuler)
        button11.setOnClickListener {

            val intent= Intent(this,MainPopulerKonular::class.java)
            startActivity(intent)

        }

        val button4=findViewById<Button>(R.id.chatgbtbtn)
        button4.setOnClickListener {

            val intent= Intent(this,YapayZeka::class.java)
            intent.putExtra("USERNAME", user.getKullaniciAdi())
            intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())////////
            startActivity(intent)

        }

        sqliteHelper = VtHelper(this)

        // Bir buton tanımlayın
        val showDataButton = findViewById<Button>(R.id.btnsentimentgit)

        // Buton tıklama dinleyicisi
        showDataButton.setOnClickListener {
            val intent = Intent(this, MainSentimentAnaliz::class.java)
            intent.putExtra("USERNAME", user.getKullaniciAdi())
            intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())////////
            startActivity(intent)

            val cursor = sqliteHelper.getAllUsers()

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                    val kullaniciadi = cursor.getString(cursor.getColumnIndexOrThrow("KULLANICIADI"))
                    val sifre = cursor.getString(cursor.getColumnIndexOrThrow("SIFRE"))
                    val email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"))
                    val xkullaniciadi = cursor.getString(cursor.getColumnIndexOrThrow("XKULLANICIADI"))

                    Log.d("KullaniciVerisi", "ID: $id, Kullanıcı Adı: $kullaniciadi, Şifre: $sifre, Email: $email, xKullanıcı Adı: $xkullaniciadi")
                } while (cursor.moveToNext())
            } else {
                Log.d("KullaniciVerisi", "Veritabanında kayıtlı kullanıcı bulunamadı.")
            }
            cursor.close()
        }


    }
}