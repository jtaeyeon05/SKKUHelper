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

/**
 * 과제 목록을 보여주는 RecyclerView Adapter
 */

class AssignmentAdapter(
    private val token: String,
    private val emptyItemMessage: String,
    private val onLongClick: ((AssignmentData) -> Boolean)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * 과제 래퍼 클래스
     * (과제 리스트가 비었으나, 고의가 아닌 경우 Empty를 표시)
     */
    sealed interface AssignmentItem {
        data class Real(val data: AssignmentData) : AssignmentItem
        data object Empty : AssignmentItem
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_EMPTY = 1
    }

    // 변경된 부분만 변경 (성능 최적화)
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

    // Diff 도우미 객체
    private val differ = AsyncListDiffer(this, diffCallback)

    /**
     * 과제 아이템 뷰홀더
     */
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

    /**
     * Empty 아이템 뷰홀더
     */
    inner class EmptyItemHolder(
        private val binding: ItemAssignmentEmptyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.textViewMessage.text = emptyItemMessage
        }
    }

    /**
     * 과제 리스트 변경 함수
     */
    fun submitList(list: List<AssignmentData>, hide: Boolean = false) {
        if (list.isNotEmpty() || hide) {
            differ.submitList(list.map { AssignmentItem.Real(it) })
        } else {
            differ.submitList(listOf(AssignmentItem.Empty))
        }
    }

    /**
     * 과제 리스트 크기 반환 함수
     */
    override fun getItemCount(): Int =  differ.currentList.size

    /**
     * 과제 리스트 아이템 유형 반환 함수
     */
    override fun getItemViewType(position: Int) = if (differ.currentList[position] is AssignmentItem.Real) VIEW_TYPE_ITEM else VIEW_TYPE_EMPTY

    /**
     * 뷰홀더 바인딩 함수
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind((differ.currentList[position] as AssignmentItem.Real).data)
        else if (holder is EmptyItemHolder) holder.bind()
    }

    /**
     * 뷰홀더 생성 함수
     */
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
