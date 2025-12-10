package com.skku_team2.skku_helper.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.databinding.FragmentAccountBinding
import com.skku_team2.skku_helper.key.PrefKey
import com.skku_team2.skku_helper.ui.start.StartActivity
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val accountViewModel: AccountViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountViewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.tvName.text = state.userProfile.name
                    binding.tvEmail.text = state.userProfile.primaryEmail ?: "No Email"

                    if (state.errorMessage != null) {
                        Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState.collect { state ->
                    val totalAssignments = state.assignmentDataList.size

                    val completedAssignments = state.assignmentDataList.count {
                        it.assignment.isSubmitted
                    }

                    val percentage = if (totalAssignments > 0) {
                        (completedAssignments * 100) / totalAssignments
                    } else {
                        0
                    }

                    binding.progressAchievement.setProgress(percentage, true)
                    binding.tvAchievementPercent.text = "$percentage%"
                    binding.tvAchievementCount.text = "$completedAssignments / $totalAssignments"

                    binding.tvMessage.text = when {
                        percentage == 100 -> "Perfect!"
                        percentage >= 80 -> "Almost done!"
                        percentage >= 50 -> "More than halfway!"
                        else -> "Cheer up!"
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences(PrefKey.Settings.key, Context.MODE_PRIVATE)
            sharedPreferences.edit {
                remove(PrefKey.Settings.TOKEN)
            }

            val intent = Intent(requireContext(), StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}