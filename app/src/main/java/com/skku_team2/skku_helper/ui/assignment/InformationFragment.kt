package com.skku_team2.skku_helper.ui.assignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.databinding.FragmentInformationBinding
import kotlinx.coroutines.launch
import kotlin.getValue


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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    assignmentViewModel.assignmentState.collect { assignmentState ->
                        if (assignmentState?.description == null || assignmentState.description.isEmpty()) {
                            binding.layoutDescription.visibility = View.GONE
                        } else {
                            binding.layoutDescription.visibility = View.VISIBLE
                            binding.textViewHtml.text = HtmlCompat.fromHtml(
                                assignmentState.description,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                        }
                    }
                }

                // TODO: Memo
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
