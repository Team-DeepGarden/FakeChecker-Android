package com.example.deepfake.login

import com.google.gson.annotations.SerializedName

data class LogInResponse(
        @SerializedName("result")
        val result: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("token")
        val token: String?
)
