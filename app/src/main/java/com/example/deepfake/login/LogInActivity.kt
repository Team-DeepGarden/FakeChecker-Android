package com.example.deepfake.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.deepfake.MainActivity
import com.example.deepfake.R
import com.example.deepfake.server.ApiClient
import com.example.deepfake.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.Date

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiClient 초기화 (중요)
        ApiClient.init(applicationContext)

        // 일반 SharedPreferences 사용
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // 뒤로가기 텍스트 클릭 시 StartActivity로 이동
        binding.tvBack.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }

        // 로그인 버튼 클릭 시 처리
        binding.loginBtnEnter.setOnClickListener {
            logIn() // 로그인 처리 메서드 호출
        }

        // EditText 입력 변화에 따라 버튼 색상 변경
        setupTextChangeListener()

        // 로그인 버튼 초기 상태 설정
        updateLoginButtonState()
    }

    private fun setupTextChangeListener() {
        // EditText의 텍스트 변화 리스너 추가
        val listener = { updateLoginButtonState() }
        binding.loginEtId.addTextChangedListener { listener() }
        binding.loginEtPassword.addTextChangedListener { listener() }
    }

    private fun updateLoginButtonState() {
        val id = binding.loginEtId.text.toString()
        val password = binding.loginEtPassword.text.toString()

        if (id.isNotEmpty() && password.isNotEmpty()) {
            val drawable = DrawableCompat.wrap(binding.loginBtnEnter.background)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.app_darknavy))
            binding.loginBtnEnter.background = drawable
            binding.loginBtnEnter.isEnabled = true
        } else {
            binding.loginBtnEnter.isEnabled = false
        }
    }

    private fun logIn() {
        val id = binding.loginEtId.text.toString()
        val password = binding.loginEtPassword.text.toString()

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LogInActivity", "Login request - ID: $id")

        val logInRequest = LogInRequest(id, password)
        binding.loginBtnEnter.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<LogInResponse?> = ApiClient.getAuthService().logIn(logInRequest)
                withContext(Dispatchers.Main) {
                    binding.loginBtnEnter.isEnabled = true // UI 비활성화 해제

                    // Check for successful response and handle nullability
                    val loginResponse = response.body()
                    if (response.isSuccessful && loginResponse != null) {
                        handleLoginSuccess(loginResponse)
                    } else {
                        handleError(response)
                    }
                }
            } catch (e: Exception) {
                Log.e("LogInActivity", "연결 실패: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    binding.loginBtnEnter.isEnabled = true // 오류 발생 시 버튼 활성화
                    Toast.makeText(this@LogInActivity, "서버에 연결할 수 없습니다. 나중에 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLoginSuccess(loginResponse: LogInResponse) {
        val result = loginResponse.result
        val token = loginResponse.token

        if (result == "success" && token != null) {
            sharedPreferences.edit().remove("guestToken").apply()
            saveAccessToken(token)

            if (!isTokenExpired(token)) {
                sharedPreferences.edit().putBoolean("isGuest", false).apply()
                hideKeyboard()
                startActivity(Intent(this@LogInActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                finish()
            } else {
                Toast.makeText(this@LogInActivity, "토큰이 만료되었습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("LogInActivity", "Login failed - result: $result, token: $token")
            Toast.makeText(this@LogInActivity, "로그인 실패: $result", Toast.LENGTH_SHORT).show()
        }
    }

    // 키보드 숨기기 함수
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard() // 키보드 숨기기
    }

    // 일반 로그인 토큰 저장 함수
    private fun saveAccessToken(token: String) {
        with(sharedPreferences.edit()) {
            putString("accessToken", token)
            remove("guestToken") // 이전에 저장된 게스트 토큰 제거
            apply()
        }
        Log.d("LogInActivity", "AccessToken saved: ${sharedPreferences.getString("accessToken", "null")}")
    }

    private fun isTokenExpired(token: String): Boolean {
        // JWT를 파싱하여 만료 시간을 확인하는 로직
        val decoded: DecodedJWT = JWT.decode(token) // Decode JWT
        val expiresAt = decoded.expiresAt // expiresAt 속성 사용
        return expiresAt?.before(Date()) ?: true
    }

    private fun handleError(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        Log.e("API Error", "Error: $errorBody")
        Toast.makeText(this, "오류 발생: $errorBody", Toast.LENGTH_SHORT).show()
    }
}