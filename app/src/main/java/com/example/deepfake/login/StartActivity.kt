package com.example.deepfake.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.deepfake.MainActivity
import com.example.deepfake.databinding.ActivityStartBinding
import com.example.deepfake.server.ApiClient
import com.example.deepfake.utils.SnackbarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import java.util.Date

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        try {
            ApiClient.getAuthService() // 초기화 여부 체크
        } catch (e: IllegalStateException) {
            ApiClient.init(this) // 초기화가 필요하면 여기서 실행
        }

        lifecycleScope.launch(Dispatchers.IO) {
            checkExistingToken()
        }

        binding.startBtnLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        binding.startBtnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.startBtnGuestlogin.setOnClickListener {
            performGuestLogin()
        }
    }

    private suspend fun checkExistingToken() {
        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null && !isTokenExpired(accessToken)) {
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
                finish()
            }
        } else {
            sharedPreferences.edit { remove("guestToken") }
        }
    }

    private fun performGuestLogin() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val guestLoginRequest = GuestLogInRequest(userid = "guest")
                val response = ApiClient.getAuthService().guestLogin(guestLoginRequest)
                Log.d("GuestLogin", "Response: ${response.body()}")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val guestToken = response.body()?.token
                        Log.d("GuestLogin", "Received guestToken: $guestToken")
                        if (guestToken != null) {
                            sharedPreferences.edit {
                                putString("guestToken", guestToken)
                                remove("accessToken")
                            }
                            SnackbarUtils.showSnackbar(this@StartActivity, binding.root, "게스트 로그인 성공")
                            startActivity(Intent(this@StartActivity, MainActivity::class.java))
                            finish()
                        } else {
                            SnackbarUtils.showSnackbar(this@StartActivity, binding.root, "게스트 로그인 실패: 토큰 없음")
                            Log.e("GuestLogin", "Token is null in response body")
                        }
                    } else {
                        Log.e("GuestLogin", "Error: ${response.code()} - ${response.message()}")
                        SnackbarUtils.showSnackbar(this@StartActivity, binding.root, "게스트 로그인 실패: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    SnackbarUtils.showSnackbar(
                        this@StartActivity,
                        binding.root,
                        "네트워크 오류 발생: ${e.message}"
                    )
                    Log.e("GuestLogin", "Exception: ${e.message}")
                }
            }
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val decoded: DecodedJWT = JWT.decode(token)
            val expiresAt = decoded.expiresAt
            expiresAt?.before(Date()) ?: true
        } catch (e: Exception) {
            Log.e("TokenCheck", "JWT 파싱 오류: ${e.message}")
            true
        }
    }

    override fun onPause() {
        super.onPause()
    }
}
