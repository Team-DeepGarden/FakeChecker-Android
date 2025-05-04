package com.example.deepfake.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.content.SharedPreferences
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.deepfake.R
import com.example.deepfake.server.ApiClient
import com.example.deepfake.databinding.ActivitySignupBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // 뒤로가기 텍스트 클릭 시 StartActivity로 이동
        binding.tvBack.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 비밀번호 확인 로직 추가
        binding.signupEtPassword.addTextChangedListener {
            validatePasswords()
        }
        binding.signupEtCheckpassword.addTextChangedListener {
            validatePasswords()
        }

        // 중복 확인 버튼 클릭 시 처리
        binding.signupBtnCheckid.setOnClickListener {
            checkIdDuplicate()
        }

        // 회원가입 버튼 클릭 시 처리
        binding.signupBtnEnter.setOnClickListener {
            signUp()
        }
    }

    private fun validatePasswords() {
        val password = binding.signupEtPassword.text.toString()
        val confirmPassword = binding.signupEtCheckpassword.text.toString()

        if (password == confirmPassword && password.isNotEmpty()) {
            binding.signupBtnEnter.isEnabled = true
            binding.signupBtnEnter.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.app_darknavy)
        } else {
            binding.signupBtnEnter.isEnabled = false
            binding.signupBtnEnter.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.app_gray)
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun checkIdDuplicate() {
        hideKeyboard()
        val id = binding.signupEtId.text.toString()

        if (id.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val checkIdRequest = CheckIdRequest(id)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<CheckIdResponse> = ApiClient.getAuthService().checkId(checkIdRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!.result
                        val status = response.body()!!.status

                        if (result == "success") {
                            Log.d("SignUpActivity", "ID is available")
                            binding.signupBtnCheckid.isEnabled = false
                            binding.signupBtnCheckid.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.app_white))
                            binding.signupBtnCheckid.backgroundTintList =
                                ContextCompat.getColorStateList(this@SignUpActivity, R.color.app_gray)
                            validatePasswords()
                        } else {
                            Log.e("SignUpActivity", "ID already exists: $status")
                            Toast.makeText(this@SignUpActivity, status, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SignUpActivity", "Check ID error: $errorBody")
                        Toast.makeText(this@SignUpActivity, "오류 발생: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp() {
        val nickname = binding.signupEtNickname.text.toString()
        val id = binding.signupEtId.text.toString()
        val password = binding.signupEtPassword.text.toString()

        // 입력값 로그 출력
        Log.d("SignUpActivity", "Nickname: $nickname, ID: $id, Password: $password")

        val signUpRequest = SignUpRequest(nickname, id, password)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<SignUpResponse> = ApiClient.getAuthService().signUp(signUpRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!.result
                        val status = response.body()!!.status

                        if (result == "success") {
                            clearExistingTokens()
                            sharedPreferences.edit().putString("nickname", nickname).apply()

                            val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("SignUpActivity", "Sign up failed: $result")
                            Toast.makeText(this@SignUpActivity, "가입 실패: $status", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 서버에서 반환된 오류 메시지를 로그로 출력
                        val errorBody = response.errorBody()?.string()
                        Log.e("SignUpActivity", "Sign up error body: $errorBody")
                        Toast.makeText(this@SignUpActivity, "가입 오류 발생: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearExistingTokens() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            remove("guest_token")
            apply()
        }
    }
}
