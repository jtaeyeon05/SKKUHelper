package com.skku_team2.skku_helper.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ItemAssignmentHeaderBinding

/**
 * 과제 유형 목록을 보여주는 RecyclerView Adapter
 */

class AssignmentHeaderAdapter(
    private val title: String,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<AssignmentHeaderAdapter.ItemViewHolder>() {
    private var isExpanded = true

    /**
     * 뷰홀더
     */
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

    /**
     * 과제 목록 펼치기/접기 함수
     */
    fun updateExpandState(expanded: Boolean) {
        if (isExpanded != expanded) {
            isExpanded = expanded
            notifyItemChanged(0)
        }
    }

    /**
     * 과제 리스트 크기 반환 함수
     */
    override fun getItemCount(): Int = 1

    /**
     * 뷰홀더 바인딩 함수
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(title, isExpanded, onClick)

    /**
     * 뷰홀더 생성 함수
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemAssignmentHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}
