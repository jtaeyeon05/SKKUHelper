package com.skku_team2.skku_helper.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ItemAssignmentHeaderBinding


class AssignmentHeaderAdapter(
    private val title: String,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<AssignmentHeaderAdapter.ItemViewHolder>() {
    private var isExpanded = true

    class ItemViewHolder(
        private val binding: ItemAssignmentHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String, isExpanded: Boolean, onClick: () -> Unit) {
            binding.root.setOnClickListener { onClick() }
            binding.textViewTitle.text = title
            binding.iconButtonExpand.setImageResource(if (isExpanded) R.drawable.ic_drop_down else R.drawable.ic_drop_up)
            binding.iconButtonExpand.setOnClickListener { onClick() }
        }
    }

    fun updateExpandState(expanded: Boolean) {
        if (isExpanded != expanded) {
            isExpanded = expanded
            notifyItemChanged(0)
        }
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(title, isExpanded, onClick)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemAssignmentHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}
