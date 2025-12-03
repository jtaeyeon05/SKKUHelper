package com.skku_team2.skku_helper.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import kotlin.getValue
import android.transition.AutoTransition
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.canvas.AssignmentData
import com.skku_team2.skku_helper.canvas.AssignmentStatus
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()

    private val leftAssignmentDataList = mutableListOf<AssignmentData>()
    private lateinit var leftAssignmentAdapter: AssignmentAdapter
    private val completedAssignmentDataList = mutableListOf<AssignmentData>()
    private lateinit var completedAssignmentAdapter: AssignmentAdapter
    private val expiredAssignmentDataList = mutableListOf<AssignmentData>()
    private lateinit var expiredAssignmentAdapter: AssignmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTestAssignment.setOnClickListener {
            val assignmentActivityIntent =
                Intent(requireContext(), AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, mainViewModel.token)
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, 1992814)
                }
            startActivity(assignmentActivityIntent)
        }

        leftAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, leftAssignmentDataList)
        binding.recyclerViewLeftAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLeftAssignment.adapter = leftAssignmentAdapter

        completedAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, completedAssignmentDataList)
        binding.recyclerViewCompletedAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCompletedAssignment.adapter = completedAssignmentAdapter

        expiredAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, expiredAssignmentDataList)
        binding.recyclerViewExpiredAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExpiredAssignment.adapter = expiredAssignmentAdapter

        binding.recyclerViewLeftAssignment.visibility = View.VISIBLE
        binding.layoutLeftAssignment.setOnClickListener { viewModel.toggleLeftAssignment() }
        binding.iconButtonLeftAssignment.setOnClickListener { viewModel.toggleLeftAssignment() }

        binding.recyclerViewCompletedAssignment.visibility = View.VISIBLE
        binding.layoutCompletedAssignment.setOnClickListener { viewModel.toggleCompletedAssignment() }
        binding.iconButtonCompletedAssignment.setOnClickListener { viewModel.toggleCompletedAssignment() }

        binding.recyclerViewExpiredAssignment.visibility = View.VISIBLE
        binding.layoutExpiredAssignment.setOnClickListener { viewModel.toggleExpiredAssignment() }
        binding.iconButtonExpiredAssignment.setOnClickListener { viewModel.toggleExpiredAssignment() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState
                        .map { Triple(it.assignmentDataList, it.isLoading, it.errorMessage) }
                        .distinctUntilChanged()
                        .collect { (assignmentDataList, isLoading, errorMessage) ->
                            leftAssignmentDataList.clear()
                            leftAssignmentDataList.addAll(assignmentDataList.filter { (_, assignment) -> assignment.status == AssignmentStatus.Left })
                            completedAssignmentDataList.clear()
                            completedAssignmentDataList.addAll(assignmentDataList.filter { (_, assignment) -> assignment.status == AssignmentStatus.Completed })
                            expiredAssignmentDataList.clear()
                            expiredAssignmentDataList.addAll(assignmentDataList.filter { (_, assignment) -> assignment.status == AssignmentStatus.Expired })

                            leftAssignmentAdapter.notifyDataSetChanged()
                            completedAssignmentAdapter.notifyDataSetChanged()
                            expiredAssignmentAdapter.notifyDataSetChanged()

                            binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
                            if (errorMessage != null) Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                }
                launch {
                    viewModel.uiState
                        .map { it.isLeftAssignmentExpanded }
                        .distinctUntilChanged()
                        .collect { isLeftAssignmentExpanded ->
                            TransitionManager.beginDelayedTransition(
                                binding.root as ViewGroup,
                                AutoTransition().apply { duration = 200 }
                            )
                            binding.iconButtonLeftAssignment.setImageResource(if (isLeftAssignmentExpanded) R.drawable.ic_drop_down else R.drawable.ic_drop_up)
                            binding.recyclerViewLeftAssignment.visibility = if (isLeftAssignmentExpanded) View.VISIBLE else View.GONE
                        }
                }
                launch {
                    viewModel.uiState
                        .map { it.isCompletedAssignmentExpanded }
                        .distinctUntilChanged()
                        .collect { isCompletedAssignmentExpanded ->
                            TransitionManager.beginDelayedTransition(
                                binding.root as ViewGroup,
                                AutoTransition().apply { duration = 200 }
                            )
                            binding.iconButtonCompletedAssignment.setImageResource(if (isCompletedAssignmentExpanded) R.drawable.ic_drop_down else R.drawable.ic_drop_up)
                            binding.recyclerViewCompletedAssignment.visibility = if (isCompletedAssignmentExpanded) View.VISIBLE else View.GONE
                        }
                }
                launch {
                    viewModel.uiState
                        .map { it.isExpiredAssignmentExpanded }
                        .distinctUntilChanged()
                        .collect { isExpiredAssignmentExpanded ->
                            TransitionManager.beginDelayedTransition(
                                binding.root as ViewGroup,
                                AutoTransition().apply { duration = 200 }
                            )
                            binding.iconButtonExpiredAssignment.setImageResource(if (isExpiredAssignmentExpanded) R.drawable.ic_drop_down else R.drawable.ic_drop_up)
                            binding.recyclerViewExpiredAssignment.visibility = if (isExpiredAssignmentExpanded) View.VISIBLE else View.GONE
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}