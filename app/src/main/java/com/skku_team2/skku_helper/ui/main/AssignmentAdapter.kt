package com.skku_team2.skku_helper.ui.main

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
    private val token: String,
    private val onLongClick: ((AssignmentData) -> Boolean)? = null
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
            val custom = assignmentData.custom

            binding.textViewTitle.text = custom?.name ?: assignment.name
            binding.textViewSubTitle.text = "${course.name} - ${if (assignment.isQuizAssignment) "Quiz" else "Assignment"}"

            if (assignmentData.isSubmitted) {
                binding.textViewState.text = "Submitted"
                binding.textViewState.setTextColor(binding.root.context.getColorAttr(R.attr.colorTertiary))
            } else {
                val remainingTime = DateUtil.calculateRemainingTime(custom?.dueAt ?: assignment.dueAt)
                when (remainingTime.type) {
                    DateUtil.DateResult.Type.UPCOMING -> {
                        binding.textViewState.text = DateUtil.formatRemainingTime(remainingTime.remainingSeconds)
                        binding.textViewState.setTextColor(binding.root.context.getColorAttr(R.attr.colorTertiary))
                    }
                    DateUtil.DateResult.Type.OVERDUE -> {
                        binding.textViewState.text = "Closed"
                        binding.textViewState.setTextColor(binding.root.context.getColorAttr(R.attr.colorErrorContainer))
                    }
                    DateUtil.DateResult.Type.NO_DATA -> {
                        binding.textViewState.text = "No Data"
                        binding.textViewState.setTextColor(binding.root.context.getColorAttr(R.attr.colorErrorContainer))
                    }
                    else -> {
                        binding.textViewState.text = "Error"
                        binding.textViewState.setTextColor(binding.root.context.getColorAttr(R.attr.colorErrorContainer))
                    }
                }
            }
            binding.root.setOnClickListener {
                val assignmentActivityIntent = Intent(binding.root.context, AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, token)
                    putExtra(IntentKey.EXTRA_COURSE_ID, course.id)
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, assignment.id)
                }
                binding.root.context.startActivity(assignmentActivityIntent)
            }
            if (onLongClick != null) {
                binding.root.setOnLongClickListener {
                    onLongClick(assignmentData)
                }
            }
        }
    }

    fun submitList(list: List<AssignmentData>) {
        differ.submitList(list.toList())
    }


    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(differ.currentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}