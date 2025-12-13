package com.skku_team2.skku_helper.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentAccountBinding
import com.skku_team2.skku_helper.key.PrefKey
import com.skku_team2.skku_helper.ui.start.StartActivity
import kotlinx.coroutines.launch


class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonInvalidate.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.main_account_dialog_invalidate_title)
                setMessage(R.string.main_account_dialog_invalidate_message)
                setNegativeButton(R.string.main_account_dialog_invalidate_cancel, null)
                setPositiveButton(R.string.main_account_dialog_invalidate_confirm) { _, _ ->
                    lifecycleScope.launch {
                        mainViewModel.invalidateFirebaseData()
                        mainViewModel.update()
                    }
                }
                create().show()
            }
        }
        binding.buttonLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.main_account_dialog_logout_title)
                setMessage(R.string.main_account_dialog_logout_message)
                setNegativeButton(R.string.main_account_dialog_logout_cancel, null)
                setPositiveButton(R.string.main_account_dialog_logout_confirm) { _, _ ->
                    lifecycleScope.launch {
                        val sharedPreferences = requireContext().getSharedPreferences(PrefKey.Settings.key, Context.MODE_PRIVATE)
                        sharedPreferences.edit {
                            remove(PrefKey.Settings.TOKEN)
                        }

                        val intent = Intent(requireContext(), StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                create().show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.userState.collect { user ->
                        binding.textViewName.text = user?.name
                        binding.textViewEmail.text = user?.primaryEmail ?: getString(R.string.main_account_profile_email_none)
                    }
                }
                launch {
                    mainViewModel.assignmentDataListState.collect { assignmentDataList ->
                        if (assignmentDataList == null) {

                        } else {
                            val totalAssignments = assignmentDataList.size
                            val completedAssignments = assignmentDataList.count { it.isSubmitted }

                            val percentage = if (totalAssignments > 0) (completedAssignments * 100) / totalAssignments else 0

                            binding.progressAchievement.setProgress(percentage, true)
                            binding.textViewAchievementPercent.text = "$percentage%"
                            binding.textViewAchievementCount.text = "$completedAssignments / $totalAssignments"

                            binding.textViewAchievementMessage.text = when {
                                percentage == 100 -> getString(R.string.main_account_achievement_message_100)
                                percentage >= 80 -> getString(R.string.main_account_achievement_message_80)
                                percentage >= 50 -> getString(R.string.main_account_achievement_message_50)
                                else -> getString(R.string.main_account_achievement_message_0)
                            }
                        }
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