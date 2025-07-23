package com.gogo.kotlinbtk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


lateinit var sqliteHelper: VtHelper
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        sqliteHelper = VtHelper(this)

        val usernameEditText: EditText = findViewById(R.id.KullaniciAdiText)
        val passwordEditText: EditText = findViewById(R.id.sifre)
        val loginButton: Button = findViewById(R.id.btngiris)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val isValidUser = sqliteHelper.checkUser(username, password)
                if (isValidUser) {
                    val user = sqliteHelper.getUserDetailsByUsername(username)
                    if (user != null) {
                        val intent = Intent(this, MainProfilim::class.java)
                        intent.putExtra("USERNAME", user.getKullaniciAdi())
                        intent.putExtra("EMAIL", user.getEmail())
                        intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                        startActivity(intent)



                    } else {
                        Toast.makeText(this, "Kullanıcı bilgileri bulunamadı!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Hatalı kullanıcı adı veya şifre!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen kullanıcı adı ve şifre girin!", Toast.LENGTH_SHORT).show()
            }
        }


        val button2 = findViewById<Button>(R.id.btngirisgeri)
        button2.setOnClickListener {
            val intent = Intent(this, Mainilkekran::class.java)
            startActivity(intent)
        }

    }
}

