package com.example.deepfake.ui.mypage

import retrofit2.Response
import retrofit2.http.GET

interface RecordApi {
    @GET("/deepfakeDetection/detectionHistory")
    suspend fun getDetectionHistory(): Response<DetectionHistoryResponse>
}
