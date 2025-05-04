package com.example.deepfake.ui.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.view.WindowManager
import com.example.deepfake.R
import com.example.deepfake.server.ApiClient

class ChatFragment : Fragment(), ChatBottomSheetFragment.OnQuestionSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var btnSend: ImageButton
    private lateinit var btnPlus: ImageButton
    private lateinit var etMsg: EditText
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatMessageList: MutableList<ChatMessage>

    private val predefinedResponses = mapOf(
        "이 앱은 어떤 기능을 제공하나요?" to "사용자가 입력한 영상이 딥페이크 영상인지 판별하는 기능을 제공합니다.",
        "앱 사용 방법을 알려주세요." to "첨부파일 업로드 혹은 영상이 포함된 링크를 입력하고 시작 버튼을 누르시면 딥페이크 탐지가 완료된 결과물을 확인하실 수 있습니다. 로그인 시 마이페이지에서 과거 탐지 내역 및 결과물을 모아서 확인하실 수 있습니다.",
        "딥페이크란 무엇인가요?" to "딥페이크(Deepfake)란 딥러닝(Deep learning)과 가짜(Fake)의 결합어입니다. 딥페이크는 인공지능 기술을 기반으로 한 가짜 이미지, 오디오, 비디오를 의미합니다. 이 기술을 악의적으로 이용하여 가짜 동영상, 가짜 뉴스 등을 유포하는 문제가 사회적 이슈로 대두되고 있습니다.",
        "딥페이크 영상의 특징은 무엇인가요?" to "주로 합성한 가짜 부분의 가장자리, 색상 변경 및 불규칙한 노이즈가 발견됩니다.",
        "딥페이크 영상을 어떻게 식별할 수 있나요?" to "딥러닝 모델을 훈련시켜 딥페이크 영상을 식별할 수 있습니다. 이러한 모델은 일반적으로 큰 데이터셋을 사용해 훈련되며, 사람의 얼굴 특징이나 비정상적인 패턴을 분석합니다.",
        "딥페이크 관련 법률에는 무엇이 있나요?" to "정보통신망 이용촉진 및 정보보호 등에 관한 법률: 이 법률은 딥페이크를 포함한 허위 정보의 유포를 금지하고 있으며, 특히 성적 딥페이크 콘텐츠에 대한 강력한 처벌 규정을 두고 있습니다. 형법: 형법에서는 딥페이크를 통한 명예훼손 및 사생활 침해를 처벌하는 조항이 있습니다.",
        "딥페이크 영상을 발견했을 때 신고 방법을 알려주세요." to "인터넷 진흥원(KISA): 한국인터넷진흥원(KISA) 홈페이지를 방문하여 사이버 침해 신고센터를 통해 딥페이크 영상을 신고할 수 있습니다.\n" +
                                                        "경찰 및 법 집행 기관: 가까운 경찰서나 사이버수사대에 신고할 수 있습니다. 경찰청 사이버안전국 홈페이지를 통해 온라인으로 신고할 수 있습니다.",
        "딥페이크 탐지 정확도는 얼마나 되나요?" to "정확도는 평균적으로 90-95%의 확률을 보이며, 입력하신 각 입력 영상별로 확률 값을 제공합니다.",
        "앱 사용에 문제가 발생하면 어떻게 하나요?" to "앱 하단 '마이페이지'란에서 '문의하기'를 클릭하신 후 문의 내용을 적어 메일 발송해주시면 빠른 시일 내로 답변 드리겠습니다."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        // Initialize views
        recyclerView = rootView.findViewById(R.id.chat_rv_messages)
        btnSend = rootView.findViewById(R.id.chat_btn_send)
        btnPlus = rootView.findViewById(R.id.chat_btn_plus)
        etMsg = rootView.findViewById(R.id.chat_et_messages)

        // Initialize ViewModel
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // Always clear messages when the fragment is created
        chatViewModel.clearMessages()

        // Initialize chat message list
        chatMessageList = mutableListOf()

        // Initialize adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = ChatMessageAdapter()
        recyclerView.adapter = adapter

        // Observe LiveData from ViewModel
        chatViewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            chatMessageList.clear()
            chatMessageList.addAll(messages)
            adapter.updateDataList(chatMessageList)
        }

        // Set up send button click listener
        btnSend.setOnClickListener {
            val msg = etMsg.text.toString()
            if (msg.isNotEmpty()) {
                val userMessage = ChatMessage(ChatMessage.ROLE_USER, msg)
                chatViewModel.addMessage(userMessage)
                etMsg.setText(null)
                hideKeyboard()
                sendMsgToChatGPT(msg)
            }
        }

        // Set up plus button click listener to show bottom sheet
        btnPlus.setOnClickListener {
            val chatBottomSheet = ChatBottomSheetFragment()
            chatBottomSheet.show(childFragmentManager, "ChatBottomSheetFragment")
        }
        return rootView
    }

    override fun onQuestionSelected(question: String) {
        // 사용자 메시지로 추가
        val userMessage = ChatMessage(ChatMessage.ROLE_USER, question)
        chatViewModel.addMessage(userMessage)

        // 사전 정의된 답변 메시지 추가
        val assistantResponse = predefinedResponses[question]
        if (assistantResponse != null) {
            val assistantMessage = ChatMessage(ChatMessage.ROLE_ASSISTANT, assistantResponse)
            chatViewModel.addMessage(assistantMessage)
        } else {
            sendMsgToChatGPT(question) // 사전 답변이 없는 경우 API를 통해 답변 요청
        }
    }

    // Hide keyboard method
    private fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val currentFocusView = activity?.currentFocus
        currentFocusView?.let {
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    // Method to send message to ChatGPT
    private fun sendMsgToChatGPT(message: String) {
        // Disable user interaction during API call
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        val request = ChatbotRequest(question = message)
        val api = ApiClient.getChatbotApi()

        api.getChatResponse(request).enqueue(object : Callback<ChatbotResponse> {
            override fun onResponse(call: Call<ChatbotResponse>, response: Response<ChatbotResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val chatResponse = response.body()!!.answer
                    val assistantMessage = ChatMessage(ChatMessage.ROLE_ASSISTANT, chatResponse)
                    chatViewModel.addMessage(assistantMessage)
                } else {
                    Log.e("getChatResponse", "Error: ${response.message()}")
                }
                // Clear the flag to re-enable user interaction
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<ChatbotResponse>, t: Throwable) {
                Log.e("getChatResponse", "onFailure: ", t)
                // Clear the flag to re-enable user interaction
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    // Save the last user message time and chat messages when the fragment is paused
    override fun onPause() {
        super.onPause()
        chatViewModel.saveLastUserMessageTime(requireContext())
        chatViewModel.saveMessages(requireContext())
    }

    // Fragment가 종료되면 메시지를 초기화
    override fun onDestroyView() {
        super.onDestroyView()

        // ViewModel의 메시지를 초기화하여 Fragment를 벗어날 때 채팅 초기화
        chatViewModel.clearMessages() // clearMessages 메서드를 ChatViewModel에 추가해야 합니다.
    }
}



