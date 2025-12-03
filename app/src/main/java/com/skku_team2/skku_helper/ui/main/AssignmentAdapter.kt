package com.skku_team2.skku_helper.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.databinding.ItemAssignmentBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import com.skku_team2.skku_helper.utils.DateUtil
import com.skku_team2.skku_helper.utils.getColorAttr


class AssignmentAdapter(
    private val context: Context,
    private val token: String,
) : RecyclerView.Adapter<AssignmentAdapter.ItemViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<AssignmentData>() {
        override fun areItemsTheSame(oldItem: AssignmentData, newItem: AssignmentData): Boolean {
            return oldItem.assignment.id == newItem.assignment.id
        }

        override fun areContentsTheSame(oldItem: AssignmentData, newItem: AssignmentData): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    inner class ItemViewHolder(
        private val binding: ItemAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assignmentData: AssignmentData) {
            val course = assignmentData.course
            val assignment = assignmentData.assignment

            binding.textViewTitle.text = assignment.name
            binding.textViewSubTitle.text = "${course.name} - ${if (assignment.isQuizAssignment) "Quiz" else "Assignment"}"

            if (assignment.isSubmitted) {
                binding.textViewState.text = "Submitted"
                binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
            } else {
                val remainingTime = DateUtil.calculateRemainingTime(assignment.dueAt)
                when (remainingTime.type) {
                    DateUtil.TimeType.UPCOMING -> {
                        binding.textViewState.text = DateUtil.formatRemainingTime(remainingTime.remainingSeconds)
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorTertiary))
                    }
                    DateUtil.TimeType.OVERDUE -> {
                        binding.textViewState.text = "Closed"
                        binding.textViewState.setTextColor(context.getColorAttr(R.attr.colorErrorContainer))
                    }
                    DateUtil.TimeType.NO_DATA -> {
                        binding.textViewState.text = "No Data"
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
                    putExtra(IntentKey.EXTRA_COURSE_ID, course.id)
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, assignment.id)
                }
                context.startActivity(assignmentActivityIntent)
            }
        }
    }

    fun submitList(list: List<AssignmentData>) {
        differ.submitList(list.toList())
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =holder.bind(differ.currentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}