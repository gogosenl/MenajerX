package com.gogo.kotlinbtk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class YapayZeka2 : ComponentActivity() {
    private val client = OkHttpClient()

    // Token listenizi burada tanımlayın
    private val bearerTokens = listOf(
        "AAAAAAAAAAAAAAAAAAAAAKrkxwEAAAAA23q2kV65Ud%2FR7kbIOA7QwCVrcA4%3D7akDZaXDyDpGbvfBJAHTB6fcwpJPkXHps11VqhrcdlPCo8yOTk", //yeni
        "AAAAAAAAAAAAAAAAAAAAAOvkxwEAAAAAgrhwm9w1fcKa9BbaZf%2FgjCpDlBw%3DBL8YzzWQzIsrKYB3GWjG04392jCBuy6rKQdo8QVRhYV302wlmj",  //yeni
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yapay_zeka2)
        val username = intent.getStringExtra("USERNAME")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")
        val user = sqliteHelper.getUserDetailsByUsername(username)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_yapay_zeka_2


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
                R.id.nav_yapay_zeka_2 -> true

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

        val userInfoTextView3: TextView = findViewById(R.id.textxkullaniciadiyorumyanit)
        userInfoTextView3.text = """
           $xKullaniciAdi
        """.trimIndent()

        val getMentionsButton = findViewById<Button>(R.id.getMentionsButton)
        val mentionsText = findViewById<TextView>(R.id.mentionsText)
        val generateRepliesButton = findViewById<Button>(R.id.generateRepliesButton)

        getMentionsButton.background = ContextCompat.getDrawable(this, R.drawable.button_background)
        generateRepliesButton.background = ContextCompat.getDrawable(this, R.drawable.button_background)

        getMentionsButton.setOnClickListener {
            if (!xKullaniciAdi.isNullOrEmpty()) {
                getUserId(xKullaniciAdi) { userId ->
                    getUserMentions(userId) { mentions ->
                        runOnUiThread { mentionsText.text = mentions }
                    }
                }
            } else {
                Toast.makeText(this, "Geçerli bir kullanıcı adı bulunamadı!", Toast.LENGTH_SHORT).show()
            }
        }

        generateRepliesButton.setOnClickListener {
            val mentions = mentionsText.text.toString()
            if (mentions.isNotEmpty()) {
                generateRepliesForMentions(mentions) { replies ->
                    runOnUiThread { mentionsText.text = replies }
                }
            } else {
                Toast.makeText(this, "Önce yorumları alın.", Toast.LENGTH_SHORT).show()
            }
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

    private fun getUserMentions(userId: String, callback: (String) -> Unit) {
        val url = "https://api.twitter.com/2/users/$userId/mentions?tweet.fields=created_at,text"

        makeRequest(url, callback = { responseData ->
            val jsonObject = JSONObject(responseData)
            if (jsonObject.has("data")) {
                val mentionsArray = jsonObject.getJSONArray("data")
                val results = StringBuilder()
                for (i in 0 until mentionsArray.length()) {
                    val mention = mentionsArray.getJSONObject(i)
                    val mentionText = mention.optString("text", "No text available")
                    results.append("Yorum: $mentionText\n")
                }
                callback(results.toString())
            } else {
                callback("Hiç yorum bulunamadı.")
            }
        })
    }

    private fun generateRepliesForMentions(
        mentionsText: String,
        updateMentionsText: (String) -> Unit
    ) {
        val mentions = mentionsText.split("\n").filter { it.startsWith("Yorum: ") }
        val updatedMentions = StringBuilder()

        if (mentions.isEmpty()) {
            updateMentionsText("Hiç geçerli yorum bulunamadı.")
            return
        }

        var processedCount = 0

        mentions.forEach { mention ->
            val mentionContent = mention.removePrefix("Yorum: ").trim()
            if (mentionContent.isNotBlank()) {
                sendChatRequest("Bu yorum için yanıt oluştur: $mentionContent") { reply ->
                    updatedMentions.append("Yorum: $mentionContent\nYoruma Yanıt: $reply\n\n")
                    processedCount++

                    if (processedCount == mentions.size) {
                        updateMentionsText(updatedMentions.toString())
                    }
                }
            } else {
                processedCount++
            }
        }
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
                                this@YapayZeka2,
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

    private fun sendChatRequest(prompt: String, callback: (String) -> Unit) {
        val request = ChatRequest(
            model = "gpt-4",
            messages = listOf(
                Message(role = "user", content = prompt)
            )
        )

        RetrofitClient.instance.getChatResponse(request).enqueue(object :
            retrofit2.Callback<ChatResponse> {
            override fun onResponse(call: retrofit2.Call<ChatResponse>, response: retrofit2.Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.get(0)?.message?.content ?: "Cevap alınamadı."
                    callback(reply.trim())
                } else {
                    callback("Hata: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<ChatResponse>, t: Throwable) {
                callback("Hata: ${t.message}")
            }
        })
    }
}
