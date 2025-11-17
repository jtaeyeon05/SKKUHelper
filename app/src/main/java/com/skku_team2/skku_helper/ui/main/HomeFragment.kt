package com.skku_team2.skku_helper.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skku_team2.skku_helper.canvas.Assignment
import com.skku_team2.skku_helper.databinding.FragmentHomeBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.assignment.AssignmentActivity
import kotlin.getValue


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

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
            val assignmentActivityIntent = Intent(requireContext(), AssignmentActivity::class.java).apply {
                    putExtra(IntentKey.EXTRA_TOKEN, mainViewModel.token)
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)  // Debug: Mobile Application Programming Lab
                    putExtra(IntentKey.EXTRA_COURSE_ID, 66262)  // Debug: Mobile App Programming Lab
                    putExtra(IntentKey.EXTRA_ASSIGNMENT_ID, 1992814)  // Debug: Lab6
                }
            startActivity(assignmentActivityIntent)
        }

        val dummyAssignmentList = mutableListOf(
            Assignment.default,
            Assignment.default.copy(
                id = 2,
                name = "AS2"
            )
        )
        val adapter = AssignmentAdapter(requireContext(), mainViewModel.token, dummyAssignmentList)
        binding.recyclerViewAssignment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAssignment.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}