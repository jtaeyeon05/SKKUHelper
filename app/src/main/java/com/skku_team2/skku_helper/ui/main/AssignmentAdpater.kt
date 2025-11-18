package com.skku_team2.skku_helper.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.canvas.CombinedAssignmentInfo
import com.skku_team2.skku_helper.databinding.ItemAssignmentBinding
import com.skku_team2.skku_helper.utils.DateUtil

class AssignmentAdapter :
    ListAdapter<CombinedAssignmentInfo, AssignmentAdapter.AssignmentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class AssignmentViewHolder(private val binding: ItemAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CombinedAssignmentInfo) {
            binding.textViewAssignmentName.text = item.assignment.name
            val courseInfo = "${item.courseName} - ${item.assignment.gradingType ?: "?"}"
            binding.textViewCourseInfo.text = courseInfo
            binding.textViewRemainingTime.text = DateUtil.calculateRemainingDays(item.assignment.dueAt)
        }
    }
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
}