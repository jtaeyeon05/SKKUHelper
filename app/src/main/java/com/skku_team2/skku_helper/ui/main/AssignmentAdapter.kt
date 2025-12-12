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
import com.skku_team2.skku_helper.databinding.ItemAssignmentEmptyBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import com.skku_team2.skku_helper.utils.DateUtil
import com.skku_team2.skku_helper.utils.getColorAttr


class AssignmentAdapter(
    private val token: String,
    private val emptyItemMessage: String,
    private val onLongClick: ((AssignmentData) -> Boolean)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    sealed interface AssignmentItem {
        data class Real(val data: AssignmentData) : AssignmentItem
        data object Empty : AssignmentItem
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_EMPTY = 1
    }

    private val diffCallback = object : DiffUtil.ItemCallback<AssignmentItem>() {
        override fun areItemsTheSame(oldItem: AssignmentItem, newItem: AssignmentItem): Boolean {
            return when (oldItem) {
                is AssignmentItem.Real if newItem is AssignmentItem.Real -> oldItem.data.assignment.id == newItem.data.assignment.id
                is AssignmentItem.Empty if newItem is AssignmentItem.Empty -> true
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: AssignmentItem, newItem: AssignmentItem): Boolean {
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

    inner class EmptyItemHolder(
        private val binding: ItemAssignmentEmptyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.textViewMessage.text = emptyItemMessage
        }
    }

    fun submitList(list: List<AssignmentData>) {
        if (list.isNotEmpty()) {
            differ.submitList(list.map { AssignmentItem.Real(it) })
        } else {
            differ.submitList(listOf(AssignmentItem.Empty))
        }
    }

    override fun getItemCount(): Int =  differ.currentList.size

    override fun getItemViewType(position: Int) = if (differ.currentList[position] is AssignmentItem.Real) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind((differ.currentList[position] as AssignmentItem.Real).data)
        else if (holder is EmptyItemHolder) holder.bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val binding = ItemAssignmentEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmptyItemHolder(binding)
        }
    }
}
