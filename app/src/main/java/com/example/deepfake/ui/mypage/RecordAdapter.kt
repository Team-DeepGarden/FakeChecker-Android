package com.example.deepfake.ui.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deepfake.R
import com.example.deepfake.databinding.ItemLogBinding

class RecordAdapter(private val detectionRecords: List<DetectionHistoryResponse.DetectionHistoryRecord>) :
    RecyclerView.Adapter<RecordAdapter.LogRecordViewHolder>() {

    inner class LogRecordViewHolder(private val binding: ItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(detectionRecord: DetectionHistoryResponse.DetectionHistoryRecord?) {
            if (detectionRecord != null) {
                binding.logTvTimestamp.text = detectionRecord.detected_at ?: "Unknown"
                binding.logTvResult.text = detectionRecord.detection_result ?: "Unknown"

                Glide.with(binding.root.context)
                    .load(detectionRecord.image)
                    .placeholder(R.drawable.ic_nonestate)
                    .error(R.drawable.ic_nonestate)
                    .into(binding.logIvThumbnail)

                // 아이템 클릭 리스너 설정
                binding.root.setOnClickListener {
                    val fragmentManager = (binding.root.context as AppCompatActivity).supportFragmentManager
                    val dialog = ClickDetailDialog.newInstance(
                        detectionRecord.image ?: "",
                        detectionRecord.detection_result ?: "Unknown",
                        detectionRecord.confidence ?: "Unknown",
                        detectionRecord.detected_at ?: "Unknown"
                    )
                    dialog.show(fragmentManager, "RecordDetailDialog")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogRecordViewHolder {
        val binding = ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogRecordViewHolder, position: Int) {
        holder.bind(detectionRecords[position])
    }

    override fun getItemCount(): Int = detectionRecords.size
}
