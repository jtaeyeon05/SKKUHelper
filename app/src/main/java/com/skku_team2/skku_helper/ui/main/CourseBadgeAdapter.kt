package com.skku_team2.skku_helper.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.canvas.Course
import com.skku_team2.skku_helper.databinding.ItemCourseBadgeBinding
import com.skku_team2.skku_helper.utils.getColorAttr


class CourseBadgeAdapter(
    private val onClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseBadgeAdapter.ItemViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    private var selectedCourseId: Int? = null

    inner class ItemViewHolder(
        private val binding: ItemCourseBadgeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.root.text = course.name
            binding.root.setOnClickListener { onClick(course) }

            binding.root.isSelected = course.id == selectedCourseId
            if (course.id == selectedCourseId) {
                binding.root.setBackgroundColor(binding.root.context.getColorAttr(android.R.attr.colorPrimary))
                binding.root.setTextColor(binding.root.context.getColorAttr(com.google.android.material.R.attr.colorOnPrimary))
            } else {
                binding.root.setBackgroundColor(binding.root.context.getColorAttr(com.google.android.material.R.attr.colorPrimaryContainer))
                binding.root.setTextColor(binding.root.context.getColorAttr(com.google.android.material.R.attr.colorOnPrimaryContainer))
            }
        }
    }

    fun submitList(list: List<Course>) {
        differ.submitList(list.toList())
    }

    fun selectCourse(courseId: Int?) {
        selectedCourseId = courseId
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(differ.currentList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCourseBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}
