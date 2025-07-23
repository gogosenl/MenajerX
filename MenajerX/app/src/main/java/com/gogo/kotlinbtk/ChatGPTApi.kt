package com.gogo.kotlinbtk


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("v1/chat/completions")
    fun getChatResponse(@Body request: ChatRequest): Call<ChatResponse>
}
data class ChatRequest(
    val model: String = "gpt-4",
    val messages: List<Message>
)
data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

