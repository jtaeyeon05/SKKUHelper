package com.skku_team2.skku_helper.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.databinding.ItemRecyclerviewBinding


class AssignmentAdapter(
    private val assignmentList: MutableList<Assignment>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ItemViewHolder(
        private val binding: ItemRecyclerviewBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(assignment: Assignment) {
            binding.textViewTitle.text = assignment.name
            binding.textViewSubTitle.text = "${assignment.courseId} - ${assignment.isQuizAssignment}"

            if (assignment.isQuizAssignment) {
                binding.textViewState.text = "Completed"
                binding.textViewState.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                binding.textViewState.text = "${assignment.dueAt} days"
                binding.textViewState.setTextColor(Color.parseColor("#D32F2F"))
            }
        }
    }

    override fun getItemCount(): Int = assignmentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as ItemViewHolder).bind(assignmentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}