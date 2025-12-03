package com.skku_team2.skku_helper.ui.main

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
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import kotlin.getValue
import android.transition.AutoTransition
import kotlin.math.exp


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()

    private val dummyAssignmentList: MutableList<Assignment> = mutableListOf()
    private lateinit var leftAssignmentAdapter: AssignmentAdapter
    private lateinit var completedAssignmentAdapter: AssignmentAdapter
    private lateinit var expiredAssignmentAdapter: AssignmentAdapter

    private var isLeftAssignmentExpanded = true
    private var isCompletedAssignmentExpanded = true
    private var isExpiredAssignmentExpanded = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTestAssignment.setOnClickListener {
            val assignmentActivityIntent =
                Intent(requireContext(), AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, mainViewModel.token)
                    putExtra(
                        IntentKey.EXTRA_COURSE_ID,
                        66262
                    )  // Debug: Mobile Application Programming Lab
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)  // Debug: Mobile App Programming Lab
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, 1992814)  // Debug: Lab6
                }
            startActivity(assignmentActivityIntent)
        }

        observeViewModel()
        if (viewModel.homepageData.value == null) viewModel.loadData()

        dummyAssignmentList.clear()
        dummyAssignmentList.addAll(
            listOf(
                Assignment.default,
                Assignment.default.copy(
                    id = 2,
                    name = "AS2"
                )
            )
        )

        leftAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, dummyAssignmentList)
        binding.recyclerViewLeftAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLeftAssignment.adapter = leftAssignmentAdapter

        completedAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, dummyAssignmentList)
        binding.recyclerViewCompletedAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCompletedAssignment.adapter = completedAssignmentAdapter

        expiredAssignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token, dummyAssignmentList)
        binding.recyclerViewExpiredAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExpiredAssignment.adapter = expiredAssignmentAdapter

        binding.recyclerViewLeftAssignment.visibility = View.VISIBLE
        binding.iconButtonLeftAssignment.setOnClickListener {
            toggleLeftAssignment()
        }

        binding.recyclerViewCompletedAssignment.visibility = View.VISIBLE
        binding.iconButtonCompletedAssignment.setOnClickListener {
            toggleCompletedAssignment()
        }

        binding.recyclerViewExpiredAssignment.visibility = View.VISIBLE
        binding.iconButtonExpiredAssignment.setOnClickListener {
            toggleExpiredAssignment()
        }

    }

    private fun toggleLeftAssignment(){
        isLeftAssignmentExpanded = !isLeftAssignmentExpanded

        val recycler = binding.recyclerViewLeftAssignment
        val icon = binding.iconButtonLeftAssignment

        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup,
            AutoTransition().apply { duration = 200 }
        )

        recycler.visibility = if (isLeftAssignmentExpanded) View.VISIBLE else View.GONE

        if (isLeftAssignmentExpanded) {
            icon.setImageResource(R.drawable.ic_drop_down)
        } else {
            icon.setImageResource(R.drawable.ic_drop_up)
        }
    }

    private fun toggleCompletedAssignment(){
        isCompletedAssignmentExpanded = !isCompletedAssignmentExpanded

        val recycler = binding.recyclerViewCompletedAssignment
        val icon = binding.iconButtonCompletedAssignment

        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup,
            AutoTransition().apply { duration = 200 }
        )

        recycler.visibility = if (isCompletedAssignmentExpanded) View.VISIBLE else View.GONE

        if (isCompletedAssignmentExpanded) {
            icon.setImageResource(R.drawable.ic_drop_down)
        } else {
            icon.setImageResource(R.drawable.ic_drop_up)
        }
    }

    private fun toggleExpiredAssignment(){
        isExpiredAssignmentExpanded = !isExpiredAssignmentExpanded

        val recycler = binding.recyclerViewExpiredAssignment
        val icon = binding.iconButtonExpiredAssignment

        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup,
            AutoTransition().apply { duration = 200 }
        )

        recycler.visibility = if (isExpiredAssignmentExpanded) View.VISIBLE else View.GONE

        if (isExpiredAssignmentExpanded) {
            icon.setImageResource(R.drawable.ic_drop_down)
        } else {
            icon.setImageResource(R.drawable.ic_drop_up)
        }
    }

    private fun observeViewModel() {
        viewModel.homepageData.observe(viewLifecycleOwner) { dataList ->
            dummyAssignmentList.clear()
            dummyAssignmentList.addAll(dataList.map { it.assignment })
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        // TODO: Flow 기반, CombinedAssignment 재정의
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}