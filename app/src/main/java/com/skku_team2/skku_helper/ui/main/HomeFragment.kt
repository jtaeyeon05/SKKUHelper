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
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var leftAssignmentAdapter: AssignmentAdapter
    private lateinit var completedAssignmentAdapter: AssignmentAdapter
    private lateinit var expiredAssignmentAdapter: AssignmentAdapter

    private lateinit var leftAssignmentHeaderAdapter: AssignmentHeaderAdapter
    private lateinit var completedAssignmentHeaderAdapter: AssignmentHeaderAdapter
    private lateinit var expiredAssignmentHeaderAdapter: AssignmentHeaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leftAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token)
        completedAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token)
        expiredAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token)

        leftAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_left_assignments)) { viewModel.toggleLeftAssignment() }
        completedAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_completed_assignments)) { viewModel.toggleCompletedAssignment() }
        expiredAssignmentHeaderAdapter = AssignmentHeaderAdapter(requireContext().getString(R.string.main_home_expired_assignments)) { viewModel.toggleExpiredAssignment() }

        val concatAdapter = ConcatAdapter(
            leftAssignmentHeaderAdapter, leftAssignmentAdapter,
            completedAssignmentHeaderAdapter, completedAssignmentAdapter,
            expiredAssignmentHeaderAdapter, expiredAssignmentAdapter
        )

        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = concatAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(
                        mainViewModel.assignmentDataListState,
                        mainViewModel.uiState
                    ) { assignmentDataListState, uiState ->
                        Triple(assignmentDataListState, uiState.isLoading, uiState.errorMessage)
                    }
                        .distinctUntilChanged()
                        .collect { updateLists() }
                }
                launch {
                    viewModel.uiState
                        .collect { state ->
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

    private fun updateLists() {
        val assignmentDataList = mainViewModel.assignmentDataListState.value?.sortedWith(
            compareBy(nullsLast()) {
                it.custom?.dueAt ?: it.assignment.dueAt
            }
        )?.filter { it.custom?.isDeleted != true }

        val leftAssignmentDataList = assignmentDataList?.filter { (_, assignment) -> assignment.status == Assignment.Status.Left } ?: emptyList()
        val completedAssignmentDataList = assignmentDataList?.filter { (_, assignment) -> assignment.status == Assignment.Status.Completed } ?: emptyList()
        val expiredAssignmentDataList = assignmentDataList?.filter { (_, assignment) -> assignment.status == Assignment.Status.Expired } ?: emptyList()

        leftAssignmentAdapter.submitList(if (viewModel.uiState.value.isLeftAssignmentExpanded) leftAssignmentDataList else emptyList())
        completedAssignmentAdapter.submitList(if (viewModel.uiState.value.isCompletedAssignmentExpanded) completedAssignmentDataList else emptyList())
        expiredAssignmentAdapter.submitList(if (viewModel.uiState.value.isExpiredAssignmentExpanded) expiredAssignmentDataList else emptyList())
    }
}
