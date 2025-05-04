package com.example.deepfake.ui.picture

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PictureApi {
    @Multipart
    @POST("/api/imageDetect")
    fun uploadPicture(@Part file: MultipartBody.Part): Call<PictureResponse>
}

