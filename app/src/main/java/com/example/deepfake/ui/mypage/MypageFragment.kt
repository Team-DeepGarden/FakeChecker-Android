package com.example.deepfake.ui.mypage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deepfake.R
import com.example.deepfake.databinding.FragmentMypageBinding
import com.example.deepfake.login.SignOutRequest
import com.example.deepfake.login.SignOutResponse
import com.example.deepfake.login.StartActivity
import com.example.deepfake.server.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", null)
        val guestToken = sharedPreferences.getString("guestToken", null)
        val nickname = sharedPreferences.getString("nickname", "GUEST")

        if (guestToken != null) {
            binding.mypageTvUserId.text = "GUEST"
            binding.mypageRvLogrecord.visibility = View.GONE
            binding.mypageTvNoData.visibility = View.VISIBLE
        } else if (accessToken != null) {
            binding.mypageTvUserId.text = nickname
            binding.mypageTvNoData.visibility = View.GONE
            getLogRecords()
        }

        binding.mypageBtnLogout.setOnClickListener {
            logOut()
        }
        /*binding.mypageBtnSignout.setOnClickListener {
            signOut()
        }*/
    }

    private fun getLogRecords() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = ApiClient.getRecordApi().getDetectionHistory()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    // detection_result가 null이 아닌 항목만 필터링하여 리스트 생성
                    val validRecords = response.body()!!.data.filter { it?.detection_result != null }

                    // 서버에서 수신한 전체 응답 로그 출력
                    Log.d("MypageFragment", "필터링된 데이터: $validRecords")

                    validRecords.forEach { record ->
                        val detectionResult = record.detection_result ?: "N/A"
                        val confidence = record.confidence ?: "N/A"
                        val detectedAt = record.detected_at ?: "N/A"
                        val image = record.image ?: "N/A"

                        Log.d("MypageFragment", """
                    Record Details:
                    - Detection Result: $detectionResult
                    - Confidence: $confidence
                    - Detected At: $detectedAt
                    - Image URL: $image
                """.trimIndent())
                    }

                    binding.mypageRvLogrecord.layoutManager = LinearLayoutManager(requireContext())
                    binding.mypageRvLogrecord.adapter = RecordAdapter(validRecords)
                    binding.mypageRvLogrecord.visibility = if (validRecords.isNotEmpty()) View.VISIBLE else View.GONE
                    binding.mypageTvNoData.visibility = if (validRecords.isNotEmpty()) View.GONE else View.VISIBLE
                } else {
                    Log.e("MypageFragment", "API 호출 실패: ${response.code()}, 오류 메시지: ${response.message()}")
                    binding.mypageTvNoData.text = "기록이 없습니다."
                    binding.mypageTvNoData.visibility = View.VISIBLE
                    binding.mypageRvLogrecord.adapter = RecordAdapter(emptyList())
                    binding.mypageRvLogrecord.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logOut() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            clearToken()
            Toast.makeText(requireContext(), "로그아웃이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), StartActivity::class.java))
            requireActivity().finish()
        }
    }

    /*private fun signOut() {
        // Show a dialog for ID and password input
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_signout, null)
        AlertDialog.Builder(requireContext())
            .setTitle("회원 탈퇴 확인")
            .setMessage("계정을 삭제하려면 아이디와 비밀번호를 입력하세요.")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val id = idInput.text.toString()
                val password = passwordInput.text.toString()

                if (id.isNotEmpty() && password.isNotEmpty()) {
                    // Send the ID and password for account deletion
                    viewLifecycleOwner.lifecycleScope.launch {
                        val response: Response<SignOutResponse> = ApiClient.getAuthService().signOut(
                            SignOutRequest(id, password)
                        )

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body()?.result == "success") {
                                Toast.makeText(requireContext(), response.body()?.status, Toast.LENGTH_SHORT).show()
                                clearToken()
                                startActivity(Intent(requireActivity(), StartActivity::class.java))
                                requireActivity().finish()
                            } else {
                                Log.e("MypageFragment", "Account deletion failed: ${response.code()}")
                                Toast.makeText(requireContext(), "회원 탈퇴에 실패했습니다.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }*/

    private fun clearToken() {
        sharedPreferences.edit().apply {
            remove("accessToken")
            remove("guestToken")
            putBoolean("isGuest", false)
            apply()
        }
        Log.d("MypageFragment", "Local token cleared")
    }
}
