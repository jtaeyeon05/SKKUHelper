package com.skku_team2.skku_helper.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.canvas.CombinedAssignmentInfo
import com.skku_team2.skku_helper.databinding.ItemAssignmentBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import com.skku_team2.skku_helper.utils.DateUtil
import com.skku_team2.skku_helper.utils.getColorAttr


class AssignmentAdapter(
    private val context: Context,
    private val token: String,
    private val assignmentList: MutableList<Assignment>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(assignment: Assignment) {
            binding.textViewTitle.text = assignment.name
            binding.textViewSubTitle.text = "${assignment.courseId} - ${if (assignment.isQuizAssignment) "Quiz" else "Assignment"}" // TODO
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
            binding.root.setOnClickListener {
                val assignmentActivityIntent = Intent(context, AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, token)
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)  // Debug: Mobile Application Programming Lab
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)  // Debug: Mobile App Programming Lab
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, 1992814)  // Debug: Lab6
                }
                context.startActivity(assignmentActivityIntent)
            }
        }
    }

    override fun getItemCount(): Int = assignmentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as ItemViewHolder).bind(assignmentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}
