package com.gogo.kotlinbtk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainEtkilesimGoruntule : AppCompatActivity() {
    private val client = OkHttpClient()

    // Token listenizi burada tanımlayın
    private val bearerTokens = listOf(
        "AAAAAAAAAAAAAAAAAAAAANXqxwEAAAAAGBQTyooXpvh5%2FvczrHFBI4LjCCE%3DN7Gka3FbVWQnb6AuJEycZNyPZoHmm7zXAHcbhk1bQB2UASdMtZ", //yeni
        "AAAAAAAAAAAAAAAAAAAAAMLqxwEAAAAAUz33qcIt0dQBOFvV0mF%2Fp9UCaZI%3DtaKvh7B2uf5Id5EVt6syiVYGiF3hbuIAaQCnIz9wA5j6Nh541o",  //yeni
        "AAAAAAAAAAAAAAAAAAAAAGeLxwEAAAAAlFcMkO0lnHOVJF%2B%2F3w21HpyXS3E%3Daf8yN7uVx27PRfkPN3C93aUCEyWUc6Im9v0NQhihiQPRt2lYpJ",
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

    lateinit var sqliteHelper: VtHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_etkilesimgoruntule)

        sqliteHelper = VtHelper(this)

        val username = intent.getStringExtra("USERNAME")
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI")
        val user = sqliteHelper.getUserDetailsByUsername(username)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_populer_konular

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_populer_konular -> {
                    val intent = Intent(this, MainPopulerKonular::class.java).apply {
                        putExtra("USERNAME", user.getKullaniciAdi())
                        putExtra("XKULLANICIADI", user.getXKullaniciAdi())
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_etkilesim_goruntule -> true
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

        // Toolbar ayarları
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

        val userInfoTextView3: TextView = findViewById(R.id.textxkullaniciadi)
        userInfoTextView3.text = """$xKullaniciAdi""".trimIndent()

        // Sayfaya gelir gelmez veriyi çekmek için burada çağırıyoruz
        if (xKullaniciAdi != null && xKullaniciAdi.isNotBlank()) {
            getUserId(xKullaniciAdi) { userId ->
                // Kullanıcı kimliğini aldıktan sonra tweet verilerini çekiyoruz
                getTweetData(userId)
            }
        } else {
            // Eğer xKullaniciAdi null veya boş ise hata mesajı göster
            userInfoTextView3.error = "Kullanıcı adı bulunamadı. Lütfen geçerli bir kullanıcı adı girin."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Menü öğelerini inflate etmek
        menuInflater.inflate(R.menu.nav_menu2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profilimbtn -> {
                // Profilim Activity'sine geçiş yap
                val intent = Intent(this, MainProfilim::class.java).apply {
                    // İsterseniz kullanıcı bilgilerini buradan ProfilimActivity'e geçirebilirsiniz
                    putExtra("USERNAME", intent.getStringExtra("USERNAME"))
                    putExtra("XKULLANICIADI", intent.getStringExtra("XKULLANICIADI"))
                }
                startActivity(intent)
                true
            }
            R.id.cikisyapbtn -> {
                // Çıkış yapma işlemleri (şimdilik sadece Toast gösteriliyor)
                Toast.makeText(this, "Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Mainilkekran::class.java).apply {
                    // İsterseniz kullanıcı bilgilerini buradan ProfilimActivity'e geçirebilirsiniz
                    putExtra("USERNAME", intent.getStringExtra("USERNAME"))
                    putExtra("XKULLANICIADI", intent.getStringExtra("XKULLANICIADI"))
                }
                startActivity(intent)
                // Çıkış işlemlerini burada gerçekleştirin (örneğin, oturumu kapatma)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getUserId(username: String, callback: (String) -> Unit) {
        val url = "https://api.twitter.com/2/users/by/username/$username"

        makeRequest(url, callback = { responseData ->
            val jsonObject = JSONObject(responseData)
            val userId = jsonObject.getJSONObject("data").getString("id")
            callback(userId) // callback fonksiyon ile userId'yi döndürüyoruz
        })
    }

    private fun getTweetData(userId: String) {

        val tweetUrl =
            "https://api.twitter.com/2/users/$userId/tweets?tweet.fields=created_at,public_metrics"
        val outputTextView = findViewById<TextView>(R.id.textView)

        makeRequest(tweetUrl, callback = { responseData ->
            println("API Yanıtı: $responseData")  // API yanıtının tamamını yazdırıyoruz

            if (responseData.isNotBlank()) {
                try {
                    val jsonResponse = JSONObject(responseData)
                    if (jsonResponse.has("data")) {
                        val tweetArray = jsonResponse.getJSONArray("data")
                        val hourLikesMap = mutableMapOf<Int, Int>()
                        var totalLikes = 0

                        for (i in 0 until tweetArray.length()) {
                            val tweet = tweetArray.getJSONObject(i)
                            val createdAt = tweet.optString("created_at", "")
                            val likeCount = tweet.optJSONObject("public_metrics")?.optInt("like_count", 0) ?: 0

                            totalLikes += likeCount

                            if (createdAt.isNotBlank()) {
                                val hour = parseHourFromTimestamp(createdAt)
                                hourLikesMap[hour] = hourLikesMap.getOrDefault(hour, 0) + likeCount
                            }
                        }

                        val sortedHours = hourLikesMap.toList().sortedByDescending { it.second }
                        val outputText = StringBuilder()
                        outputText.append("Saat Bazında Beğeni Yoğunluğu:\n")
                        for ((hour, likes) in sortedHours) {
                            outputText.append("$hour:00 - ${hour + 1}:00: $likes beğeni\n")
                        }
                        outputText.append("\nToplam Beğeni Sayısı: $totalLikes")

                        runOnUiThread {
                            outputTextView?.text = outputText.toString()
                            val pieChart = findViewById<PieChart>(R.id.pieChart)
                            val filteredSortedHours = sortedHours.filter { it.second > 0 }
                            val entries = ArrayList<PieEntry>()
                            for ((hour, likes) in filteredSortedHours) {
                                entries.add(PieEntry(likes.toFloat(), "$hour:00"))
                            }

                            val dataSet = PieDataSet(entries, "Saat Bazında Beğeni")
                            dataSet.valueTextColor = Color.WHITE
                            dataSet.valueTextSize = 14f

                            val colorList = ArrayList<Int>()
                            val n = filteredSortedHours.size

                            if (n == 1) {
                                colorList.add(Color.rgb(173, 216, 230)) // Açık mavi
                            } else {
                                for (i in filteredSortedHours.indices) {
                                    val ratio = i.toFloat() / (n - 1)
                                    val blue = (230 * (1 - ratio) + 139 * ratio).toInt()
                                    val red = (173 * (1 - ratio) + 75 * ratio).toInt()
                                    val green = (216 * (1 - ratio) + 130 * ratio).toInt()
                                    colorList.add(Color.rgb(red, green, blue))
                                }
                            }

                            dataSet.colors = colorList


                            dataSet.setValueFormatter(object :
                                com.github.mikephil.charting.formatter.ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return "${value.toInt()} beğeni"
                                }
                            })

                            val pieData = PieData(dataSet)
                            pieChart.data = pieData
                            pieChart.description.isEnabled = false
                            pieChart.isDrawHoleEnabled = false
                            pieChart.legend.isEnabled = true
                            pieChart.setUsePercentValues(false)

                            pieChart.invalidate()
                        }
                    } else {
                        runOnUiThread {
                            outputTextView.text = "Veri bulunamadı: $jsonResponse"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        outputTextView.text = "JSON parse hatası: ${e.localizedMessage}"
                    }
                }
            } else {
                runOnUiThread {
                    outputTextView.text = "API'den boş yanıt alındı."
                }
            }
        })
    }

    private fun parseHourFromTimestamp(timestamp: String): Int {
        return try {
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(timestamp)
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            calendar.get(java.util.Calendar.HOUR_OF_DAY)
        } catch (e: Exception) {
            e.printStackTrace()
            0
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
                        makeRequest(url, callback, onError)
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainEtkilesimGoruntule,
                                "İstek limitine ulaştınız. Lütfen daha sonra tekrar deneyin.",
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
