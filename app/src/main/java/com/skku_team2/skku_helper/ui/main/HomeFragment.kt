package com.skku_team2.skku_helper.ui.main

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.comparisons.compareBy

/**
 * 과제 목록을 보여주는 MainActivity 내 Fragment
 */

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // MainActivity 단위 ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()
    // HomeFragment 단위 ViewModel
    private val viewModel: HomeViewModel by viewModels()

    // 필터링 코스 RecyclerView Adapter
    private lateinit var courseBadgeAdapter: CourseBadgeAdapter

    // 과제 유형 목록 RecyclerView Adapter
    private lateinit var leftAssignmentHeaderAdapter: AssignmentHeaderAdapter
    private lateinit var completedAssignmentHeaderAdapter: AssignmentHeaderAdapter
    private lateinit var expiredAssignmentHeaderAdapter: AssignmentHeaderAdapter

    // 과제 목록 RecyclerView Adapter
    private lateinit var leftAssignmentAdapter: AssignmentAdapter
    private lateinit var completedAssignmentAdapter: AssignmentAdapter
    private lateinit var expiredAssignmentAdapter: AssignmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 필터링 코스 RecyclerView Adapter 설정
        courseBadgeAdapter = CourseBadgeAdapter(
            onClick = { course ->
                if (viewModel.uiState.value.selectedCourseId != course.id) {
                    viewModel.selectCourse(course.id)
                    courseBadgeAdapter.selectCourse(course.id)
                    updateLists()
                } else {
                    viewModel.selectCourse(null)
                    courseBadgeAdapter.selectCourse(null)
                    updateLists()
                }
            }
        )
        binding.recyclerViewBadge.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewBadge.adapter = courseBadgeAdapter

        // 과제 유형 목록 RecyclerView Adapter 설정
        leftAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_left_assignments)) { viewModel.toggleLeftAssignment() }
        completedAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_completed_assignments)) { viewModel.toggleCompletedAssignment() }
        expiredAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_expired_assignments)) { viewModel.toggleExpiredAssignment() }

        // 과제 목록 RecyclerView Adapter 설정
        val onItemLongClickListener: (AssignmentData) -> Boolean = { assignmentData ->
            CoroutineScope(Dispatchers.Main).launch {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(R.string.main_dialog_delete_title)
                    setMessage(R.string.main_dialog_delete_message)
                    setNegativeButton(R.string.main_dialog_delete_cancel, null)
                    setPositiveButton(R.string.main_dialog_delete_confirm) { _, _ ->
                        lifecycleScope.launch {
                            mainViewModel.deleteAssignment(assignmentData.course.id, assignmentData.assignment.id)
                            mainViewModel.update()
                        }
                    }
                    create().show()
                }
            }
            true
        }

        leftAssignmentAdapter = AssignmentAdapter(
            token = mainViewModel.token,
            emptyItemMessage = requireContext().getString(R.string.main_home_left_empty_item_message),
            onLongClick = onItemLongClickListener
        )
        completedAssignmentAdapter = AssignmentAdapter(
            token = mainViewModel.token,
            emptyItemMessage = requireContext().getString(R.string.main_home_completed_empty_item_message),
            onLongClick = onItemLongClickListener
        )
        expiredAssignmentAdapter = AssignmentAdapter(
            token = mainViewModel.token,
            emptyItemMessage = requireContext().getString(R.string.main_home_expired_empty_item_message),
            onLongClick = onItemLongClickListener
        )

        val concatAdapter = ConcatAdapter(
            leftAssignmentHeaderAdapter, leftAssignmentAdapter,
            completedAssignmentHeaderAdapter, completedAssignmentAdapter,
            expiredAssignmentHeaderAdapter, expiredAssignmentAdapter
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = concatAdapter

        // Swipe to Refresh 설정
        binding.swipeRefreshLayoutHome.setOnRefreshListener {
            lifecycleScope.launch {
                mainViewModel.update()
                binding.swipeRefreshLayoutHome.isRefreshing = false
            }
        }

        // ViewModel 관측
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // MainViewModel 기반 Fragment 내용 변경
                launch {
                    combine(
                        mainViewModel.assignmentDataListState,
                        mainViewModel.uiState
                    ) { assignmentDataListState, uiState ->
                        Triple(assignmentDataListState, uiState.isLoading, uiState.errorMessage)
                    }
                        .distinctUntilChanged()
                        .collect {
                            val courseList = mainViewModel.assignmentDataListState.value?.map { it.course }?.distinctBy { it.id }?.sortedBy { it.createdAt }?.reversed()
                            courseBadgeAdapter.submitList(courseList ?: emptyList())
                            updateLists()
                        }
                }
                // HomeViewModel 기반 Fragment 내용 변경
                launch {
                    viewModel.uiState
                        .collect { state ->
                            if (_binding == null) return@collect

                            TransitionManager.beginDelayedTransition(
                                binding.root as ViewGroup,
                                AutoTransition().apply { duration = 200 }
                            )

                            leftAssignmentHeaderAdapter.updateExpandState(state.isLeftAssignmentExpanded)
                            completedAssignmentHeaderAdapter.updateExpandState(state.isCompletedAssignmentExpanded)
                            expiredAssignmentHeaderAdapter.updateExpandState(state.isExpiredAssignmentExpanded)
                            updateLists()
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 과제 리스트 업데이트 (필터링, 정렬)
     */
    private fun updateLists() {
        val assignmentDataList = mainViewModel.assignmentDataListState.value?.sortedWith(
            compareBy(nullsLast()) {
                it.custom?.dueAt ?: it.assignment.dueAt
            }
        )?.filter { it.custom?.isDeleted != true && (viewModel.uiState.value.selectedCourseId == null || it.course.id == viewModel.uiState.value.selectedCourseId) }

        val leftAssignmentDataList = assignmentDataList?.filter { assignmentData -> assignmentData.status == AssignmentData.Status.Left } ?: emptyList()
        val completedAssignmentDataList = assignmentDataList?.filter { assignmentData -> assignmentData.status == AssignmentData.Status.Completed } ?: emptyList()
        val expiredAssignmentDataList = assignmentDataList?.filter { assignmentData -> assignmentData.status == AssignmentData.Status.Expired } ?: emptyList()

        leftAssignmentAdapter.submitList(
            list = if (viewModel.uiState.value.isLeftAssignmentExpanded) leftAssignmentDataList else emptyList(),
            hide = !viewModel.uiState.value.isLeftAssignmentExpanded
        )
        completedAssignmentAdapter.submitList(
            list = if (viewModel.uiState.value.isCompletedAssignmentExpanded) completedAssignmentDataList else emptyList(),
            hide = !viewModel.uiState.value.isCompletedAssignmentExpanded
        )
        expiredAssignmentAdapter.submitList(
            list = if (viewModel.uiState.value.isExpiredAssignmentExpanded) expiredAssignmentDataList else emptyList(),
            hide = !viewModel.uiState.value.isExpiredAssignmentExpanded
        )
    }
}
