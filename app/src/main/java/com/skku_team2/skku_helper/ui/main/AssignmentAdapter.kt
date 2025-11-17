package com.skku_team2.skku_helper.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.databinding.HeaderRowBinding
import com.skku_team2.skku_helper.databinding.ItemRecyclerviewBinding

class AssignmentAdapter(
    private val rows: MutableList<AssignmentRow>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int =
        when(rows[position]){
            is AssignmentRow.Header -> VIEW_TYPE_HEADER
            is AssignmentRow.Item -> VIEW_TYPE_ITEM
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == VIEW_TYPE_HEADER){
            val binding = HeaderRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            HeaderViewHolder(binding)
        } else {
            val binding = ItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ItemViewHolder(binding)
        }
    }

    inner class HeaderViewHolder(
        private val binding: HeaderRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssignmentRow.Header) {
            binding.Title.text = item.title
            binding.btnToggle.rotation = if (item.expanded) 180f else 0f
        }

    }

    inner class ItemViewHolder(
        private val binding: ItemRecyclerviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssignmentRow.Item) {
            val a = item.assignment
            binding.assingnmentTitle.text = a.title
            binding.assignmentSubtitle.text = "${a.course} - ${a.type}"

            if (a.isCompleted) {
                binding.assignmentStatus.text = "Completed"
                binding.assignmentStatus.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                binding.assignmentStatus.text = "${a.remainingDays} days"
                binding.assignmentStatus.setTextColor(Color.parseColor("#D32F2F"))
            }
        }
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is AssignmentRow.Header -> (holder as HeaderViewHolder).bind(row)
            is AssignmentRow.Item   -> (holder as ItemViewHolder).bind(row)
        }
    }
}