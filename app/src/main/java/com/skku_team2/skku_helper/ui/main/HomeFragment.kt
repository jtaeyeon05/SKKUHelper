package com.skku_team2.skku_helper.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var assignmentAdapter: AssignmentAdapter

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
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, 1992814)
                }
            startActivity(assignmentActivityIntent)
        }

        assignmentAdapter = AssignmentAdapter(requireContext(), mainViewModel.token)

        binding.recyclerViewAssignment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = assignmentAdapter
        }
        observeViewModel()
        if (viewModel.homepageData.value == null) viewModel.loadData()
    }

    private fun observeViewModel() {
        viewModel.homepageData.observe(viewLifecycleOwner) { dataList ->
            assignmentAdapter.submitList(dataList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}