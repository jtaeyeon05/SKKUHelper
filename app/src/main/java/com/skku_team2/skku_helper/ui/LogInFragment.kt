package com.skku_team2.skku_helper.ui

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.skku_team2.skku_helper.MainActivity
import com.skku_team2.skku_helper.databinding.FragmentLoginBinding


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

        binding.textInputLayoutToken.setOnClickListener {
            // TODO: Dialog
        }

        binding.buttonConfirm.setOnClickListener {
            // TODO: Log In
            val mainActivityIntent = Intent(requireContext(), MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            val theme = context?.theme
            binding.buttonConfirm.isEnabled = state.buttonLogInEnabled
            if (state.buttonLogInEnabled) {
                val colorButtonContainer = TypedValue()
                val colorButtonOnContainer = TypedValue()
                theme?.resolveAttribute(com.google.android.material.R.attr.colorPrimaryContainer, colorButtonContainer, true)
                theme?.resolveAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer, colorButtonOnContainer, true)
                binding.buttonConfirm.setBackgroundColor(colorButtonContainer.data)
                binding.buttonConfirm.setTextColor(colorButtonOnContainer.data)
            } else {
                val colorButtonContainer = TypedValue()
                val colorButtonOnContainer = TypedValue()
                theme?.resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainerHighest, colorButtonContainer, true)
                theme?.resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, colorButtonOnContainer, true)
                binding.buttonConfirm.setBackgroundColor(colorButtonContainer.data)
                binding.buttonConfirm.setTextColor(colorButtonOnContainer.data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
