package com.skku_team2.skku_helper.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skku_team2.skku_helper.canvas.Course
import com.skku_team2.skku_helper.databinding.ItemCourseBadgeBinding
import com.skku_team2.skku_helper.utils.getColorAttr

/**
 * 필터링 코스 목록을 보여주는 RecyclerView Adapter
 */

class CourseBadgeAdapter(
    private val onClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseBadgeAdapter.ItemViewHolder>() {
    private var selectedCourseId: Int? = null

    // 변경된 부분만 변경 (성능 최적화)
    private val diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }

    // Diff 도우미 객체
    private val differ = AsyncListDiffer(this, diffCallback)

    /**
     * 뷰홀더
     */
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

    /**
     * 코스 리스트 변경 함수
     */
    fun submitList(list: List<Course>) {
        differ.submitList(list.toList())
    }

    /**
     * 필터링 코스 지정 함수
     */
    fun selectCourse(courseId: Int?) {
        selectedCourseId = courseId
        notifyDataSetChanged()
    }

    /**
     * 과제 리스트 크기 반환 함수
     */
    override fun getItemCount(): Int = differ.currentList.size

    /**
     * 뷰홀더 바인딩 함수
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(differ.currentList[position])

    /**
     * 뷰홀더 생성 함수
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCourseBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
}
