package com.gogo.kotlinbtk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainPopulerKonular: AppCompatActivity() {
    private val client = OkHttpClient()

    // Bearer token listesini tanımlıyoruz
    private val bearerTokens = listOf(
        "AAAAAAAAAAAAAAAAAAAAALnqxwEAAAAACB%2BV1JXf2PKxr1VB0M8mVhBf%2FxY%3DromgDaCXbj78L7y5NdLRbo6c43gjwYd3sYd0pUiTMSGagBceCC", //yeni
        "AAAAAAAAAAAAAAAAAAAAAD3qxwEAAAAAnLJIKubEVyBDWzmiOwot3T5BI1Y%3DkOe2KsdtfD9tMcwNkvKWP7l4Y4PN5Y8LajoO6DG0gDQxECjZP4",  //yeni
        "AAAAAAAAAAAAAAAAAAAAAPjqxwEAAAAAICwgrv36n2oTLcFojioUnEqyzCw%3DFGvnAEYpRogHKrVN8LN2Rf4iJCouvlFQ8xNsl1WmAoOLREPY7T",  //yeni
        "AAAAAAAAAAAAAAAAAAAAAHiLxwEAAAAA2FTEORTGnmzUG%2B8XF1LA32zValI%3DlhNtYBMd0ETu5tBWdjFowznFtj3n2d5gplcRrQg7Rb6i7lWgPc",
        "AAAAAAAAAAAAAAAAAAAAAD%2BLxwEAAAAAh8EqBL%2FjXnORzFAt9a3%2FW7Ct%2F0g%3DTlWWTPhO9lB1Zu5U7hNxm9TUbjiOg8QoF1wIdF5lBVMEkdo1Fc"

    )
    private var currentTokenIndex = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_trending)

        val username = intent.getStringExtra("USERNAME")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")
        val user = sqliteHelper.getUserDetailsByUsername(username)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_populer_konular

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_etkilesim_goruntule -> {
                    val intent = Intent(this, MainEtkilesimGoruntule::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_populer_konular -> true
                R.id.nav_sentiment_analiz -> {
                    val intent = Intent(this, MainSentimentAnaliz::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_yapay_zeka -> {
                    val intent = Intent(this, YapayZeka::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_yapay_zeka_2 -> {
                    val intent = Intent(this, YapayZeka2::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profilimbtn -> {
                    try {
                        val email = user.getEmail()
                        val intent = Intent(this, MainProfilim::class.java).apply {
                            putExtra("USERNAME", user.getKullaniciAdi())
                            putExtra("EMAIL", email)
                            putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Profilim ekranına yönlendirme sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.cikisyapbtn -> {
                    val intent = Intent(this, Mainilkekran::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val searchQueryEditText = findViewById<EditText>(R.id.edtSearchQuery)
        val fetchTrendsButton = findViewById<Button>(R.id.btnFetchTrends)

        fetchTrendsButton.setOnClickListener {
            val query = searchQueryEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchTrendingHashtags(query)
            } else {
                val outputTextView = findViewById<TextView>(R.id.txtTrends)
                outputTextView.text = "Lütfen bir kelime girin."
            }
        }
    }

    private fun fetchTrendingHashtags(query: String) {
        val url = "https://api.twitter.com/2/tweets/search/recent?query=$query&tweet.fields=entities"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${bearerTokens[currentTokenIndex]}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    val outputTextView = findViewById<TextView>(R.id.txtTrends)
                    outputTextView.text = "API bağlantısı başarısız: ${e.localizedMessage}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                runOnUiThread {
                    val outputTextView = findViewById<TextView>(R.id.txtTrends)

                    if (response.code == 429) {
                        currentTokenIndex = (currentTokenIndex + 1) % bearerTokens.size
                        outputTextView.text = "Çok fazla istek attınız, farklı bir token ile tekrar deneniyor..."
                        fetchTrendingHashtags(query) // Yeni token ile tekrar dene
                    } else if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonResponse = JSONObject(responseData)
                            val dataArray = jsonResponse.optJSONArray("data")
                            val hashtagCounts = mutableMapOf<String, Int>()

                            if (dataArray != null) {
                                for (i in 0 until dataArray.length()) {
                                    val tweet = dataArray.getJSONObject(i)
                                    val entities = tweet.optJSONObject("entities")
                                    val hashtags = entities?.optJSONArray("hashtags")

                                    if (hashtags != null && hashtags.length() > 0) {
                                        for (j in 0 until hashtags.length()) {
                                            val hashtag = hashtags.getJSONObject(j).optString("tag")
                                            if (hashtag.isNotEmpty()) {
                                                hashtagCounts[hashtag] = hashtagCounts.getOrDefault(hashtag, 0) + 1
                                            }
                                        }
                                    }
                                }

                                val topHashtags = hashtagCounts.entries
                                    .sortedByDescending { it.value }
                                    .take(5)
                                    .joinToString("\n") { "#${it.key}" }

                                outputTextView.text = "En Popüler Hashtagler:\n$topHashtags"
                            } else {
                                outputTextView.text = "Veri bulunamadı."
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            outputTextView.text = "JSON parse hatası: ${e.localizedMessage}"
                        }
                    } else {
                        outputTextView.text = "API hatası: ${response.code} - ${response.message}\nYanıt: $responseData"
                    }
                }
            }
        })
    }
}
