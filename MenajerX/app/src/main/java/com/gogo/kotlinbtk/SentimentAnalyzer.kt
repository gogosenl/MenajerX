package com.gogo.kotlinbtk

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class SentimentAnalyzer {
    private val client = OkHttpClient()
    private val apiKey = "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" // Hugging Face API Key
    private val url = "https://api-inference.huggingface.co/models/savasy/bert-base-turkish-sentiment-cased"

    private val labelDescriptions = mapOf(
        "negative" to "Olumsuz",
        "positive" to "Olumlu",
    )

    fun analyzeSentiment(text: String?, callback: (String) -> Unit) {
        if (text.isNullOrBlank()) {
            callback("Error: Input text cannot be empty.")
            return
        }

        val requestBody = """
        {
            "inputs": [
                {"text": "$text"}
            ]
        }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val call = client.newCall(request)
        retryWithExponentialBackoff(call, callback = callback)
    }

    private fun retryWithExponentialBackoff(
        call: Call,
        attempt: Int = 1,
        maxAttempts: Int = 3,
        delay: Long = 2000L, // Başlangıçta 2 saniye
        callback: (String) -> Unit
    ) {
        if (attempt > maxAttempts) {
            callback("Max retry attempts reached. Please try again later.")
            return
        }

        Thread.sleep(delay)
        client.newCall(call.request()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                retryWithExponentialBackoff(call, attempt + 1, maxAttempts, delay * 2, callback) // Gecikmeyi artır
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    handleResponse(responseData, callback)
                } else {
                    if (response.code == 503 || response.code == 429) { // Çok fazla istek veya geçici hizmet yok
                        retryWithExponentialBackoff(call, attempt + 1, maxAttempts, delay * 2, callback)
                    } else {
                        callback("API Error: ${response.message}")
                    }
                }
            }
        })
    }

    private fun handleResponse(responseData: String?, callback: (String) -> Unit) {
        if (responseData == null) {
            callback("Empty response from API")
            return
        }

        try {
            val jsonArray = JSONArray(responseData)
            val predictions = jsonArray.getJSONArray(0)
            val firstPrediction = predictions.getJSONObject(0)
            val label = firstPrediction.getString("label")
            val score = firstPrediction.getDouble("score")
            val readableLabel = labelDescriptions[label] ?: "Bilinmiyor"
            callback("$readableLabel (${(score * 100).toInt()}%)")
        } catch (e: Exception) {
            e.printStackTrace()
            callback("JSON Parse Error: ${e.localizedMessage}")
        }
    }
}
