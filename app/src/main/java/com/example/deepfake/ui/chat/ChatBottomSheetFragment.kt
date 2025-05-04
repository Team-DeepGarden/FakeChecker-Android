package com.example.deepfake.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.deepfake.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChatBottomSheetFragment : BottomSheetDialogFragment() {

    private var listener: OnQuestionSelectedListener? = null

    interface OnQuestionSelectedListener {
        fun onQuestionSelected(question: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.bottomsheet_ib_back)
        backButton.setOnClickListener {
            dismiss()
        }

        val questionButtons = listOf(
            view.findViewById<Button>(R.id.bottomsheet_btn_question1) to "이 앱은 어떤 기능을 제공하나요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question2) to "앱 사용 방법을 알려주세요.",
            view.findViewById<Button>(R.id.bottomsheet_btn_question3) to "딥페이크란 무엇인가요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question4) to "딥페이크 영상의 특징은 무엇인가요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question5) to "딥페이크 영상을 어떻게 식별할 수 있나요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question6) to "딥페이크 관련 법률에는 무엇이 있나요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question7) to "딥페이크 영상을 발견했을 때 신고 방법을 알려주세요.",
            view.findViewById<Button>(R.id.bottomsheet_btn_question8) to "딥페이크 탐지 정확도는 얼마나 되나요?",
            view.findViewById<Button>(R.id.bottomsheet_btn_question9) to "앱 사용에 문제가 발생하면 어떻게 하나요?"
        )

        questionButtons.forEach { (button, question) ->
            button.setOnClickListener {
                listener?.onQuestionSelected(question)
                dismiss()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = 900
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnQuestionSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnQuestionSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
