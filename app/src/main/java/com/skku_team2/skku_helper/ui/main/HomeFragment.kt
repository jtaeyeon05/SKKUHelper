package com.skku_team2.skku_helper.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val recycler = binding.recyclerAssignments

        //todo
        val dummy = listOf(
            Assignment(1, "Midterm Project Proposal", "Mobile App Programming", "Assignment", 3, false),
            Assignment(2, "Programming Assignment 2", "Computer Security", "Assignment", 0, false)
        )

        val rows = mutableListOf<AssignmentRow>()
        rows.add(AssignmentRow.Header("Left Assignments", true))
        rows.addAll(dummy.map { AssignmentRow.Item(it) })

        val adapter = AssignmentAdapter(rows)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}