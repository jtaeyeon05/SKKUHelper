package com.skku_team2.skku_helper.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.skku_team2.skku_helper.canvas.CombinedAssignmentInfo
import com.skku_team2.skku_helper.databinding.ItemAssignmentBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import com.skku_team2.skku_helper.utils.DateUtil
import com.skku_team2.skku_helper.utils.getColorAttr

class AssignmentAdapter(
    private val context: Context,
    private val token: String
) : ListAdapter<CombinedAssignmentInfo, AssignmentAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CombinedAssignmentInfo>() {
            override fun areItemsTheSame(oldItem: CombinedAssignmentInfo, newItem: CombinedAssignmentInfo): Boolean {
                return oldItem.assignment.id == newItem.assignment.id
            }

            override fun areContentsTheSame(oldItem: CombinedAssignmentInfo, newItem: CombinedAssignmentInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ItemViewHolder(
        private val binding: ItemAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CombinedAssignmentInfo) {
            val assignment = item.assignment

            binding.textViewTitle.text = assignment.name
            binding.textViewSubTitle.text = "${item.courseName} - ${if (assignment.isQuizAssignment) "Quiz" else "Assignment"}"

            val isSubmitted = assignment.submission?.workflowState == "submitted" || assignment.submission?.workflowState == "graded"

            if (isSubmitted) {
                binding.textViewState.text = "Submitted"
                binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
            } else {
                // 미제출 시 남은 시간 표시
                when (val remainingTime = DateUtil.calculateRemainingTime(assignment.dueAt)) {
                    is DateUtil.RemainingTime.Days -> {
                        binding.textViewState.text = "${remainingTime.value} Days"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
                    }
                    is DateUtil.RemainingTime.Hours -> {
                        binding.textViewState.text = "${remainingTime.value} Hours"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
                    }
                    is DateUtil.RemainingTime.Minutes -> {
                        binding.textViewState.text = "${remainingTime.value} Minutes"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
                    }
                    is DateUtil.RemainingTime.Closed -> {
                        binding.textViewState.text = "Closed"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorErrorContainer))
                    }
                    else -> {
                        binding.textViewState.text = "Error"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorErrorContainer))
                    }
                }
            }
            binding.root.setOnClickListener {
                val assignmentActivityIntent = Intent(context, AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, token)
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, assignment.id)
                }
                context.startActivity(assignmentActivityIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}