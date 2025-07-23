package com.gogo.kotlinbtk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainProfilim: AppCompatActivity() {
    lateinit var sqliteHelper: VtHelper


    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_profilim)

        sqliteHelper = VtHelper(this)



        // Intent ile gelen kullanıcı bilgilerini al
        val username = intent.getStringExtra("USERNAME") /////////
        val email = intent.getStringExtra("EMAIL")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")




        val user = sqliteHelper.getUserDetailsByUsername(username)///////
        val button=findViewById<Button>(R.id.btnsecimegit)
        button.setOnClickListener {

            val intent= Intent(this,YapayZeka2::class.java)
            intent.putExtra("USERNAME", user.getKullaniciAdi())
            intent.putExtra("XKULLANICIADI", user.getXKullaniciAdi())////////
            startActivity(intent)

        }


        // Kullanıcı bilgilerini ekrana yazdır
        val userInfoTextView: TextView = findViewById(R.id.xkullaniciAdiGetir)
        userInfoTextView.text = """
             X Kullanıcı Adı: $xKullaniciAdi
        """.trimIndent()

        val userInfoTextView2: TextView = findViewById(R.id.sifregetir)
        userInfoTextView2.text = """
            Email: $email
        """.trimIndent()

        val userInfoTextView3: TextView = findViewById(R.id.kullaniciAdigetir)
        userInfoTextView3.text = """
            Kullanıcı Adı: $username           
        """.trimIndent()


        // WindowInsets uygulaması
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } else {
            Log.e("ProfilimActivity", "Main view not found in layout")
        }
    }
}
