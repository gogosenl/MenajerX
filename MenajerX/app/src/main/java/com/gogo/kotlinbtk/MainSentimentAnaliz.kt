package com.gogo.kotlinbtk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
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

class MainSentimentAnaliz : AppCompatActivity() {
    private val client = OkHttpClient()

    private val bearerTokens = listOf(
        "AAAAAAAAAAAAAAAAAAAAACjqxwEAAAAAbJS4uny%2BTuIw57luZFvamg7tDUs%3DabU9IH2HWp5Xn3SUNCMYslKmHvLPm57R1GTYFqYIIcIjIE1pfM",
        "AAAAAAAAAAAAAAAAAAAAAPbpxwEAAAAA5ZNxT1w8dWJyuL54JoftwOiuIHg%3Drn4vCVhfRAfMQ3ETqbBPFzonALFZ40LSjfX0gL1hVckvoSIPww",
        "AAAAAAAAAAAAAAAAAAAAAA%2FrxwEAAAAAwyPxIMIMXyVwzqUBeK3Hz4Q3s6g%3DsVG36vNqUTGIxECPuOeihvcxsFFJwryFHzLMyyLDfgoqa7nmSO",
        "AAAAAAAAAAAAAAAAAAAAANZNxwEAAAAAw1XZXarJBmv%2B85TbqE2ggiN0VgU%3DkJzY1vX6Z6yTLvWHE8h0rSJp33MeFDyG3qBSLA2U0eqh5M1IMG",
        "AAAAAAAAAAAAAAAAAAAAAHiLxwEAAAAA2FTEORTGnmzUG%2B8XF1LA32zValI%3DlhNtYBMd0ETu5tBWdjFowznFtj3n2d5gplcRrQg7Rb6i7lWgPc",
        "AAAAAAAAAAAAAAAAAAAAAGCLxwEAAAAA6YXUi46rfpQImbNE%2FP%2FwdET9yi8%3DgjuVTFxOzNhyQ1s6aP3Dg2hnJjhOE3EhsO1YxASaZr0wDLHhZy",
        "AAAAAAAAAAAAAAAAAAAAAIWLxwEAAAAA0%2BrtsQqWBIVK3leBm4IPYd3irho%3DBMXlrjbwWcLqYbXhMJF7ynVdzM1WntUU6kyCpLT8ZTtBYHwJF5"
    )
    private var currentTokenIndex = 0

    private fun getCurrentToken(): String {
        return bearerTokens[currentTokenIndex]
    }

    private fun switchToNextToken(): Boolean {
        return if (currentTokenIndex < bearerTokens.size - 1) {
            currentTokenIndex++
            true
        } else {
            false
        }
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sentimentanaliz)

        val username = intent.getStringExtra("USERNAME")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")
        val user = sqliteHelper.getUserDetailsByUsername(username)
        val outputTextView = findViewById<TextView>(R.id.textView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val usernameTextView = findViewById<TextView>(R.id.textxkullaniciadisentiment)

        if (user != null) {
            usernameTextView.text = user.xKullaniciAdi
        } else {
            usernameTextView.text = "Kullanıcı bilgisi bulunamadı"
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_sentiment_analiz

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
                R.id.nav_populer_konular -> {
                    val intent = Intent(this, MainPopulerKonular::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_sentiment_analiz -> true // Zaten bu ekrandayız
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
                        val email = user.getEmail() // user'dan email'i çekiyoruz
                        val intent = Intent(this, MainProfilim::class.java).apply {
                            putExtra("USERNAME", user.getKullaniciAdi())
                            putExtra("EMAIL", email) // Doğrudan user'dan gelen email'i ekliyoruz
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

        if (xKullaniciAdi != null && xKullaniciAdi.isNotBlank()) {
            progressBar.visibility = View.VISIBLE
            isModelReady { isReady ->
                if (isReady) {
                    getUserId(xKullaniciAdi) { userId ->
                        getUserMentions(userId) { mentionsText, positivePercentage ->
                            runOnUiThread {
                                progressBar.visibility = View.GONE
                                val overallSentiment = if (positivePercentage >= 51) {
                                    "Genel duygu analizi sonucu: İyi bir durumdasınız."
                                } else {
                                    "Genel duygu analizi sonucu: Durum kötü görünüyor."
                                }
                                outputTextView.text = "$mentionsText\n$overallSentiment"
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        outputTextView.text = "Model şu anda hazır değil. Lütfen daha sonra tekrar deneyin."
                    }
                }
            }
        } else {
            outputTextView.text = "Kullanıcı adı bulunamadı. Lütfen geçerli bir kullanıcı adı sağlayın."
        }
    }

    private fun getUserId(username: String, callback: (String) -> Unit) {
        val url = "https://api.twitter.com/2/users/by/username/$username"

        makeRequest(url, callback = { responseData ->
            val jsonObject = JSONObject(responseData)
            val userId = jsonObject.getJSONObject("data").getString("id")
            callback(userId)
        })
    }

    private fun getUserMentions(userId: String, callback: (String, Int) -> Unit) {
        val url = "https://api.twitter.com/2/users/$userId/mentions?tweet.fields=created_at,text,author_id"

        makeRequest(url, callback = { responseData ->
            val jsonResponse = JSONObject(responseData)
            if (jsonResponse.has("data")) {
                val mentionsArray = jsonResponse.getJSONArray("data")
                if (mentionsArray.length() == 0) {
                    callback("Bu kullanıcıya gelen yorum yok.", 0)
                } else {
                    val sentimentAnalyzer = SentimentAnalyzer()
                    val results = StringBuilder()
                    var processedCount = 0
                    var positiveCount = 0

                    for (i in 0 until mentionsArray.length()) {
                        val mention = mentionsArray.getJSONObject(i)
                        val mentionText = mention.optString("text", "No text available")

                        sentimentAnalyzer.analyzeSentiment(mentionText) { sentiment ->
                            synchronized(this) {
                                if (sentiment == "Olumlu") {
                                    positiveCount++
                                }
                                results.append("Gelen Yorum: $mentionText\nDuygu Analizi: $sentiment\n\n")
                                processedCount++
                                if (processedCount == mentionsArray.length()) {
                                    val positivePercentage = (positiveCount * 100) / mentionsArray.length()
                                    callback(results.toString(), positivePercentage)
                                }
                            }
                        }
                    }
                }
            } else {
                callback("Hiç yorum bulunamadı.", 0)
            }
        })
    }

    private fun isModelReady(callback: (Boolean) -> Unit) {
        val url = "https://api-inference.huggingface.co/models/savasy/bert-base-turkish-sentiment-cased"

        makeRequest(url, callback = { responseData ->
            callback(true)
        }, onError = {
            callback(false)
        })
    }

    private fun makeRequest(
        url: String,
        callback: (String) -> Unit,
        onError: (() -> Unit)? = null
    ) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${getCurrentToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onError?.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        callback(it)
                    }
                } else if (response.code == 429) {
                    if (switchToNextToken()) {
                        Log.d("TokenSwitch", "Çok fazla istek atıldı. Tekrar deneme yapılıyor.")
                        makeRequest(url, callback, onError)
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainSentimentAnaliz,
                                "Tüm istek haklarınız tükendi. Lütfen daha sonra tekrar deneyin.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        onError?.invoke()
                    }
                } else {
                    onError?.invoke()
                }
            }
        })
    }
}
