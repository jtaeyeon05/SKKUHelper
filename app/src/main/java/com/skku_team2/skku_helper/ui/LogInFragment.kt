package com.skku_team2.skku_helper.ui

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skku_team2.skku_helper.MainActivity
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentLoginBinding
import androidx.core.net.toUri
import androidx.core.view.setPadding
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogInViewModel by viewModels()

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
            viewModel.onTokenChanged(it?.toString().orEmpty())
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
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(R.string.start_login_token_help_title)
                    val dialogLayout = LinearLayout(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        gravity = Gravity.CENTER_VERTICAL
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(64)
                    }
                    CircularProgressIndicator(requireContext()).let {
                        it.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        it.isIndeterminate = true
                        dialogLayout.addView(it)
                    }
                    Space(requireContext()).let {
                        it.layoutParams = LinearLayout.LayoutParams(32, 0)
                        dialogLayout.addView(it)
                    }
                    TextView(requireContext()).let {
                        it.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        it.textSize = 16f
                        it.setText(R.string.start_login_login_loading)
                        dialogLayout.addView(it)
                    }
                    setView(dialogLayout)
                    setCancelable(false)

                    val dialog = create()
                    dialog.show()
                    val logInResult = viewModel.logIn()
                    if (logInResult) {
                        val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
                        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainActivityIntent)
                    } else {
                        // Log In Failed
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
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            val theme = context?.theme
            binding.buttonLogin.isEnabled = state.buttonLogInEnabled
            if (state.buttonLogInEnabled) {
                val colorButtonContainer = TypedValue()
                val colorButtonOnContainer = TypedValue()
                theme?.resolveAttribute(com.google.android.material.R.attr.colorPrimaryContainer, colorButtonContainer, true)
                theme?.resolveAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer, colorButtonOnContainer, true)
                binding.buttonLogin.setBackgroundColor(colorButtonContainer.data)
                binding.buttonLogin.setTextColor(colorButtonOnContainer.data)
            } else {
                val colorButtonContainer = TypedValue()
                val colorButtonOnContainer = TypedValue()
                theme?.resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainerHighest, colorButtonContainer, true)
                theme?.resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, colorButtonOnContainer, true)
                binding.buttonLogin.setBackgroundColor(colorButtonContainer.data)
                binding.buttonLogin.setTextColor(colorButtonOnContainer.data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
