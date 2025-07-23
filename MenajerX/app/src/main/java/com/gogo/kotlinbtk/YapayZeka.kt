package com.gogo.kotlinbtk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@Suppress("NAME_SHADOWING")
class YapayZeka : ComponentActivity() {
    private val client = OkHttpClient();

    // Birden fazla Bearer Token tanımlayın
    private val bearerTokens = listOf(
        "Bearer AAAAAAAAAAAAAAAAAAAAAATlxwEAAAAALYdheZQe%2FrRXje91EV%2BzExcZrDA%3D3Q4lG74Nl222qarQ5Gzxq0krLOQOAC1NPDDjwJFVT5rar9FBvW", //yeni
        "Bearer AAAAAAAAAAAAAAAAAAAAANrpxwEAAAAA5e9zvoYZ7rOhDkXLja6HbWOTR1k%3D2Poq1qpzuIOD3krdpzzdDqCBZw6v2BYLzRnXMdo0l4YQzYJETo",  //yeni
        "Bearer AAAAAAAAAAAAAAAAAAAAABnrxwEAAAAA1%2BUgDJ%2BrnrsXOYp3llQNKe6aDSo%3DSDR8BFlVUDzOShMHb94Z1G1YZPsQdJxPh25E6V0b56Uwp8W9xg", //yeni
        "Bearer AAAAAAAAAAAAAAAAAAAAAGuLxwEAAAAA6lSwhJo9B2zUHr8gmGB9C78QOBM%3DVJbrSFNCwAQ1sUpszSl71u9E2J51cm2tuttAUDLo0VD5Nk5eKm",
        "Bearer AAAAAAAAAAAAAAAAAAAAAD%2BLxwEAAAAAh8EqBL%2FjXnORzFAt9a3%2FW7Ct%2F0g%3DTlWWTPhO9lB1Zu5U7hNxm9TUbjiOg8QoF1wIdF5lBVMEkdo1Fc"
    );
    private var currentTokenIndex = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yapay_zeka);
        val username = intent.getStringExtra("USERNAME");
        val xKullaniciAdi = intent.getStringExtra("XKULLANICIADI");
        val user = sqliteHelper.getUserDetailsByUsername(username);

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation);
        bottomNavigationView.selectedItemId = R.id.nav_yapay_zeka;

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_etkilesim_goruntule -> {
                    val intent = Intent(this, MainEtkilesimGoruntule::class.java).apply {
                        putExtra("USERNAME", user.kullaniciAdi);
                        putExtra("XKULLANICIADI", user.xKullaniciAdi);
                    };
                    startActivity(intent);
                    true;
                }
                R.id.nav_populer_konular -> {
                    val intent = Intent(this, MainPopulerKonular::class.java).apply {
                        putExtra("USERNAME", user.kullaniciAdi)
                        putExtra("XKULLANICIADI", user.xKullaniciAdi)
                    };
                    startActivity(intent);
                    true;
                }
                R.id.nav_sentiment_analiz -> {
                    val intent = Intent(this, MainSentimentAnaliz::class.java).apply {
                        putExtra("USERNAME", user.kullaniciAdi);
                        putExtra("XKULLANICIADI", user.xKullaniciAdi);
                    };
                    startActivity(intent);
                    true;
                }
                R.id.nav_yapay_zeka -> true;
                R.id.nav_yapay_zeka_2 -> {
                    val intent = Intent(this, YapayZeka2::class.java).apply {
                        putExtra("USERNAME", user.kullaniciAdi);
                        putExtra("XKULLANICIADI", user.xKullaniciAdi);
                    };
                    startActivity(intent);
                    true;
                }

                else -> false;
            }
        };

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




        val btnFetchTweets = findViewById<Button>(R.id.btnFetchTweets);
        val btnGenerateContent = findViewById<Button>(R.id.btnGenerateContent);
        val tvResponse = findViewById<TextView>(R.id.tvResponse);

        // Arkaplanı drawable olarak ayarla
        btnFetchTweets.background = ContextCompat.getDrawable(this, R.drawable.button_background)
        btnGenerateContent.background = ContextCompat.getDrawable(this, R.drawable.button_background)



        btnFetchTweets.setOnClickListener {
            if (!xKullaniciAdi.isNullOrEmpty()) {
                getTweets(xKullaniciAdi) { response ->
                    runOnUiThread { tvResponse.text = response };
                };
            } else {
                Toast.makeText(this, "Veritabanında kullanıcı adı bulunamadı!", Toast.LENGTH_SHORT).show();
            }
        };

        val rgMood = findViewById<RadioGroup>(R.id.rgMood);

        btnGenerateContent.setOnClickListener {
            val responseText = tvResponse.text.toString();
            if (responseText.isNotEmpty() && responseText != "Cevap burada görünecek...") {
                val selectedMoodId = rgMood.checkedRadioButtonId;
                val moodPrompt = when (selectedMoodId) {
                    R.id.rbGood -> "Çok mutlu bir şekilde";
                    R.id.rbNeutral -> "Normal bir şekilde";
                    R.id.rbBad -> "Çok üzgün bir şekilde";
                    else -> "Normal bir şekilde";
                };

                sendChatRequest("Bu tweetlerin içeriğine göre $moodPrompt yeni tweet içeriği oluştur: \n$responseText") { response ->
                    runOnUiThread { tvResponse.text = response };
                };
            } else {
                Toast.makeText(this, "Önce bir tweet alın!", Toast.LENGTH_SHORT).show();
            }
        };

        val userInfoTextView3: TextView = findViewById(R.id.textxkullaniciadiyeniicerik);
        userInfoTextView3.text = """
           $xKullaniciAdi
        """.trimIndent();
    }

    private fun getTweets(username: String, callback: (String) -> Unit) {
        val url = "https://api.twitter.com/2/users/by/username/$username";

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", bearerTokens[currentTokenIndex])
            .build();

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback("Hata: ${e.localizedMessage}");
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string();
                    val jsonObject = JSONObject(responseData ?: "");
                    val userId = jsonObject.getJSONObject("data").getString("id");

                    fetchTweets(userId, callback);
                } else if (response.code == 429) {
                    switchToken()
                    callback("Token değiştiriliyor, lütfen tekrar deneyin...")
                } else {
                    callback("Kullanıcı bulunamadı veya hata oluştu: ${response.code}");
                }
            }
        });
    }

    private fun fetchTweets(userId: String, callback: (String) -> Unit) {
        val url = "https://api.twitter.com/2/users/$userId/tweets?tweet.fields=created_at,text";

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", bearerTokens[currentTokenIndex])
            .build();

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback("Tweetleri çekerken hata oluştu: ${e.localizedMessage}");
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string();
                    val jsonResponse = JSONObject(responseData ?: "");
                    val tweetsArray = jsonResponse.optJSONArray("data");
                    if (tweetsArray != null && tweetsArray.length() > 0) {
                        val tweets = StringBuilder();
                        for (i in 0 until tweetsArray.length()) {
                            val tweet = tweetsArray.getJSONObject(i);
                            val text = tweet.getString("text");
                            val createdAt = tweet.getString("created_at");
                            tweets.append("Tweet: $text\nTarih: $createdAt\n\n");
                        }
                        callback(tweets.toString());
                    } else {
                        callback("Bu kullanıcının tweet'i bulunamadı.");
                    }
                } else if (response.code == 429) {
                    switchToken()
                    callback("Token değiştiriliyor, lütfen tekrar deneyin...")
                } else {
                    callback("Tweetler alınırken hata oluştu: ${response.code}");
                }
            }
        });
    }

    private fun sendChatRequest(prompt: String, callback: (String) -> Unit) {
        val request = ChatRequest(
            model = "gpt-4",
            messages = listOf(
                Message(role = "user", content = prompt)
            )
        )

        RetrofitClient.instance.getChatResponse(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.get(0)?.message?.content ?: "Cevap alınamadı."
                    callback(reply.trim())
                } else {
                    callback("Hata: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                callback("Hata: ${t.message}")
            }
        })
    }

    private fun switchToken() {
        currentTokenIndex = (currentTokenIndex + 1) % bearerTokens.size
    }
}
