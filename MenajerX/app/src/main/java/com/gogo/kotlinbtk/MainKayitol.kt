package com.gogo.kotlinbtk

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainKayitol : AppCompatActivity() {

    private lateinit var sqliteHelper: VtHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kayitol)

        val nameEditText: EditText = findViewById(R.id.kullaniciAdi)
        val passwordEditText: EditText = findViewById(R.id.sifre)
        var passwordEditText2: EditText = findViewById(R.id.sifreTekrar)
        val emailEditText: EditText = findViewById(R.id.email)
        val usernameEditText: EditText = findViewById(R.id.XKullaniciAdi)
        val saveButton: Button = findViewById(R.id.kaydetButonu)
        sqliteHelper = VtHelper(this)

        saveButton.setOnClickListener {
            val kullaniciadi = nameEditText.text.toString().trim()
            val sifre = passwordEditText.text.toString().trim()
            var sifreTekrar = passwordEditText2.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val xkullaniciadi = usernameEditText.text.toString().trim()

            // Email ve şifre doğrulama kontrolleri
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Geçerli bir email adresi girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sifre.length < 8) {
                Toast.makeText(this, "Şifre en az 8 karakterden oluşmalı!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (!sifre.equals(sifreTekrar)) {
                Toast.makeText(this, "Şifre uyuşmuyor!", Toast.LENGTH_SHORT).show();
                return@setOnClickListener
            }


            if (kullaniciadi.isNotEmpty() && sifre.isNotEmpty() && email.isNotEmpty() && xkullaniciadi.isNotEmpty()) {
                val result = sqliteHelper.insertUser(kullaniciadi, sifre, email, xkullaniciadi)



                if (result > 0) {
                    Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show()
                    // Alanları temizle
                    nameEditText.text.clear()
                    passwordEditText.text.clear()
                    emailEditText.text.clear()
                    usernameEditText.text.clear()
                    passwordEditText2.text.clear()

                    saveButton.postDelayed({
                        val intent = Intent(this, Mainilkekran::class.java)
                        startActivity(intent)
                    }, 1000)

                } else {
                    Toast.makeText(this, "Kayıt Başarısız!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

        val button2 = findViewById<Button>(R.id.btnkayitgeri)
        button2.setOnClickListener {
            val intent = Intent(this, Mainilkekran::class.java)
            startActivity(intent)
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

