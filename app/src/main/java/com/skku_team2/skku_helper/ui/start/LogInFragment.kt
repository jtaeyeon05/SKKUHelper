package com.skku_team2.skku_helper.ui.start

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.DialogLoadingBinding
import com.skku_team2.skku_helper.databinding.FragmentLoginBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.ui.main.MainActivity
import com.skku_team2.skku_helper.utils.getColorAttr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val startViewModel: StartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInputLayoutToken.editText?.addTextChangedListener {
            startViewModel.changeToken(it?.toString().orEmpty())
        }

        binding.buttonTokenHelp.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.start_login_token_help_title)
                setMessage(R.string.start_login_token_help_message)
                setNegativeButton(R.string.start_login_token_help_dismiss, null)
                setPositiveButton(R.string.start_login_token_help_issue) { dialog, which ->
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://canvas.skku.edu/profile/settings".toUri()
                    )
                    startActivity(browserIntent)
                }
                create().show()
            }
        }

        binding.buttonLogin.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(R.string.start_login_login_loading_title)
                    setView(DialogLoadingBinding.inflate(LayoutInflater.from(requireContext())).root)
                    setCancelable(false)
                }.create()
                dialog.show()

                startViewModel.verifyToken()
                if (startViewModel.uiState.value.isTokenVerified == true) {
                    val mainActivityIntent = Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra(IntentKey.EXTRA_TOKEN, startViewModel.uiState.value.token)
                    }
                    startActivity(mainActivityIntent)
                } else {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle(R.string.start_login_login_failed_title)
                        setMessage(R.string.start_login_login_failed_message)
                        setNegativeButton(R.string.start_login_login_failed_dismiss, null)
                        create().show()
                    }
                }

                dialog.dismiss()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                startViewModel.uiState.collect { state ->
                    binding.buttonLogin.isEnabled = !state.token.isNullOrBlank()
                    if (binding.buttonLogin.isEnabled) {
                        binding.buttonLogin.setBackgroundColor(context?.getColorAttr(com.google.android.material.R.attr.colorPrimaryContainer) ?: Color.BLACK)
                        binding.buttonLogin.setTextColor(context?.getColorAttr(com.google.android.material.R.attr.colorOnPrimaryContainer) ?: Color.WHITE)
                    } else {
                        binding.buttonLogin.setBackgroundColor(context?.getColorAttr(com.google.android.material.R.attr.colorSurfaceContainerHighest) ?: Color.GRAY)
                        binding.buttonLogin.setTextColor(context?.getColorAttr(com.google.android.material.R.attr.colorOnSurfaceVariant) ?: Color.WHITE)
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
