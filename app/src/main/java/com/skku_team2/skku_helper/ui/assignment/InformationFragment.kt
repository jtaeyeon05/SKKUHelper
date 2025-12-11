package com.skku_team2.skku_helper.ui.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.databinding.FragmentInformationBinding
import kotlinx.coroutines.launch


class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val assignmentViewModel: AssignmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextMemo.doOnTextChanged { editable, _, _, _ ->
            assignmentViewModel.changeAssignmentMemo(editable.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    assignmentViewModel.assignmentDataState.collect { assignmentData ->
                        val assignment = assignmentData?.assignment
                        val custom = assignmentData?.custom
                        if (assignment?.description == null || assignment.description.isEmpty()) {
                            binding.layoutDescription.visibility = View.GONE
                        } else {
                            binding.layoutDescription.visibility = View.VISIBLE
                            binding.textViewHtml.text = HtmlCompat.fromHtml(
                                assignment.description,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                        }
                        val memoFromState = custom?.memo ?: ""
                        if (binding.editTextMemo.text.toString() != memoFromState) {
                            binding.editTextMemo.setText(memoFromState)
                            binding.editTextMemo.setSelection(memoFromState.length)
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
