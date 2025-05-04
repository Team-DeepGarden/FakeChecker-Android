package com.example.deepfake.ui.video

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VideoApi {
    @Multipart
    @POST("/deepfakeDetection/videoDetect")
    fun uploadVideo(@Part file: MultipartBody.Part): Call<VideoResponse>
}


