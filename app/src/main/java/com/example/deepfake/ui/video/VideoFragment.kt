package com.example.deepfake.ui.video

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide // Glide 라이브러리 추가
import com.example.deepfake.R
import com.example.deepfake.databinding.FragmentVideoBinding
import com.example.deepfake.server.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private val videoApi: VideoApi by lazy {
        ApiClient.getVideoApi()
    }

    private val pickVideoLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val videoFile = createFileFromUri(requireContext(), uri)
                uploadVideoMultipart(videoFile)  // multipart 형식으로 업로드
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        binding.videoBtnFile.setOnClickListener {
            pickVideoLauncher.launch("video/*")
        }

        return binding.root
    }

    private fun createFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "uploaded_video.mp4")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun uploadVideoMultipart(file: File) {
        binding.videoPbUpload.visibility = View.VISIBLE

        val requestFile = file.asRequestBody("video/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("video", file.name, requestFile)

        videoApi.uploadVideo(body).enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                binding.videoPbUpload.visibility = View.GONE

                if (response.isSuccessful) {
                    val videoResponse = response.body()
                    val deepfakeProbability = videoResponse?.fakeProbability
                    val frameImageUrl = videoResponse?.frameImage

                    binding.videoTvResultreturn.text = "딥페이크 확률: $deepfakeProbability"

                    if (frameImageUrl != null) {
                        loadImageFromUrl(frameImageUrl)
                    } else {
                        Log.e("VideoFragment", "이미지 URL이 없습니다.")
                    }
                } else {
                    try {
                        val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                        Log.e("VideoFragment", "서버 응답 실패: $errorMessage")
                    } catch (e: Exception) {
                        Log.e("VideoFragment", "에러 본문을 읽는 중 오류 발생: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                binding.videoPbUpload.visibility = View.GONE
                Log.e("VideoFragment", "업로드 오류: ${t.message}")
            }
        })
    }

    private fun loadImageFromUrl(url: String) {
        Glide.with(this)
            .load(url)
            .into(binding.videoIvPlay)
        Log.d("VideoFragment", "이미지 URL 로드 성공: $url")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
