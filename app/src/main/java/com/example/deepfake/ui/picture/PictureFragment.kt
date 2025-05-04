package com.example.deepfake.ui.picture

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.deepfake.R
import com.example.deepfake.databinding.FragmentPictureBinding
import com.example.deepfake.server.ApiClient
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream

class PictureFragment : Fragment() {

    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!

    // PictureApi 초기화
    private val pictureApi: PictureApi by lazy {
        ApiClient.getPictureApi()
    }

    // 업로드 시간 추적 변수
    private var lastUploadTime: Long = 0
    private val uploadTimeoutMillis: Long = 30 * 60 * 1000 // 30분

    // Register the ActivityResultLauncher for picking picture
    private val pickPictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // 새로운 이미지 설정
                updatePictureViewWithSelectedImage(uri)
                val pictureFile = createFileFromUri(requireContext(), uri)
                uploadPicture(pictureFile)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 버튼 클릭 시 동작 설정
        binding.pictureBtnFile.setOnClickListener {
            resetToInitialState()
            pickPictureLauncher.launch("image/*") // 이미지 선택
        }

        return root
    }

    // 선택한 이미지로 ImageView를 업데이트하는 함수
    private fun updatePictureViewWithSelectedImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .override(500, 500) // 원하는 크기로 조정
            .into(binding.pictureIvPlay)
    }

    // 30분 경과 시 결과를 초기화하는 함수
    private fun resetToInitialState() {
        binding.pictureIvPlay.setImageResource(R.drawable.ic_nonestate) // 초기 이미지 설정
        binding.pictureTvResultreturn.text = "" // 결과 텍스트 초기화
    }

    private fun createFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "uploaded_image.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun uploadPicture(file: File) {
        Log.d("PictureFragment", "파일 경로: ${file.absolutePath}, 파일 크기: ${file.length()}")

        // ProgressBar를 표시
        binding.picturePbUpload.visibility = View.VISIBLE

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestBody)

        pictureApi.uploadPicture(body).enqueue(object : Callback<PictureResponse> {
            override fun onResponse(call: Call<PictureResponse>, response: Response<PictureResponse>) {
                // ProgressBar 숨기기
                binding.picturePbUpload.visibility = View.GONE

                if (response.isSuccessful) {
                    try {
                        val pictureResponse = response.body()
                        val result = pictureResponse?.result

                        if (result != null) {
                            // result가 null이 아닌 경우
                            val percentage = result.percentage

                            // 서버에서 처리된 결과를 TextView에 표시
                            binding.pictureTvResultreturn.text = "위 사진이 딥페이크일 확률은 $percentage 입니다"
                            Log.d("PictureFragment", "업로드 성공, 결과 업데이트 완료")

                            // 업로드 시간 기록
                            lastUploadTime = System.currentTimeMillis()
                        } else {
                            // result가 null인 경우
                            binding.pictureTvResultreturn.text = "결과가 없습니다."
                            Log.e("PictureFragment", "결과가 비어있습니다.")
                        }
                    } catch (e: Exception) {
                        Log.e("PictureFragment", "응답 처리 오류: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<PictureResponse>, t: Throwable) {
                // ProgressBar 숨기기
                binding.picturePbUpload.visibility = View.GONE
                Log.e("PictureFragment", "업로드 오류: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // 현재 시간이 마지막 업로드 시간에서 30분이 지났는지 확인
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUploadTime > uploadTimeoutMillis) {
            // 30분이 경과한 경우 이미지와 결과 텍스트 초기화
            resetToInitialState() // 초기 설정
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
