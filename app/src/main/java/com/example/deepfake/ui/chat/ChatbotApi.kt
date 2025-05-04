package com.example.deepfake.ui.chat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {

    @POST("api/chatbot")
    fun getChatResponse(@Body request: ChatbotRequest): Call<ChatbotResponse>
}
