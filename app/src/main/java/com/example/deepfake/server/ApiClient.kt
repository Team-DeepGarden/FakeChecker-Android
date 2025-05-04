package com.example.deepfake.server

import android.content.Context
import android.util.Log
import com.example.deepfake.login.AuthService
import com.example.deepfake.ui.chat.ChatbotApi
import com.example.deepfake.ui.mypage.RecordApi
import com.example.deepfake.ui.picture.PictureApi
import com.example.deepfake.ui.video.VideoApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://54.180.29.93:3000/"
    private var appContext: Context? = null

    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        if (appContext == null) { // 이미 초기화되었는지 확인
            appContext = context.applicationContext
            okHttpClient = provideOkHttpClient()
            retrofit = provideRetrofit()
        }
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(provideAuthInterceptor())
            .build()
    }

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder()

            val noAuthPaths = listOf("api/auth/signup", "api/auth/checkid", "api/auth/guestlogin")

            if (noAuthPaths.any { request.url.encodedPath.contains(it) }) {
                Log.d("AuthInterceptor", "No token added for path: ${request.url.encodedPath}")
            } else {
                val sharedPref = appContext?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val accessToken = sharedPref?.getString("accessToken", null)
                val guestToken = sharedPref?.getString("guestToken", null)

                if (accessToken != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                } else if (guestToken != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $guestToken")
                }
            }

            chain.proceed(requestBuilder.build())
        }
    }

    fun getAuthService(): AuthService {
        checkInitialized()
        return retrofit!!.create(AuthService::class.java)
    }

    fun getVideoApi(): VideoApi {
        checkInitialized()
        return retrofit!!.create(VideoApi::class.java)
    }

    fun getPictureApi(): PictureApi {
        checkInitialized()
        return retrofit!!.create(PictureApi::class.java)
    }

    fun getChatbotApi(): ChatbotApi {
        checkInitialized()
        return retrofit!!.create(ChatbotApi::class.java)
    }

    fun getRecordApi(): RecordApi {
        checkInitialized()
        return retrofit!!.create(RecordApi::class.java)
    }

    // 안전한 초기화 체크 추가
    private fun checkInitialized() {
        if (retrofit == null) {
            throw IllegalStateException("ApiClient is not initialized. Call ApiClient.init(context) first.")
        }
    }
}

