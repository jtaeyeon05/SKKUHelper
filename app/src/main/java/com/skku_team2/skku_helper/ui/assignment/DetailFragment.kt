package com.skku_team2.skku_helper.ui.assignment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentDetailBinding
import com.skku_team2.skku_helper.utils.DateUtil
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val assignmentViewModel: AssignmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(
                        assignmentViewModel.userState,
                        assignmentViewModel.courseState,
                        assignmentViewModel.assignmentState
                    ) { userState, courseState, assignmentState ->
                        Triple(userState, courseState, assignmentState)
                    }.collect { (userState, courseState, assignmentState) ->
                        binding.buttonAssignment.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, assignmentState?.htmlUrl?.toUri())
                            requireContext().startActivity(intent)
                        }
                        if (!(assignmentState?.isSubmitted ?: true)) {
                            binding.buttonSubmission.visibility = View.GONE
                        } else {
                            binding.buttonSubmission.visibility = View.VISIBLE
                            binding.buttonSubmission.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, assignmentState?.submission?.previewUrl?.toUri())
                                requireContext().startActivity(intent)
                            }
                        }

                        if (assignmentState?.description == null || assignmentState.description.isEmpty()) {
                            binding.layoutDescription.visibility = View.GONE
                        } else {
                            binding.layoutDescription.visibility = View.VISIBLE
                            binding.textViewHtml.text = HtmlCompat.fromHtml(
                                assignmentState.description,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                        }

                        if (!(assignmentState?.isSubmitted ?: true)) {
                            binding.layoutSubmission.visibility = View.GONE
                        } else if (assignmentState?.submission != null) {
                            binding.layoutSubmission.visibility = View.VISIBLE
                            assignmentState.submission.run {
                                if (grade == null) {
                                    binding.tableRowGrade.visibility = View.GONE
                                } else {
                                    binding.tableRowGrade.visibility = View.VISIBLE
                                    binding.tableTextGrade.text = "$grade / $${assignmentState.pointsPossible}"
                                }
                                if (submissionType == null) {
                                    binding.tableRowType.visibility = View.GONE
                                } else {
                                    binding.tableRowType.visibility = View.VISIBLE
                                    binding.tableTextType.text = when (submissionType) {
                                        "online_quiz" -> "Quiz"
                                        "online_upload" -> "Assignment"
                                        else -> submissionType
                                    }
                                }
                                if (submittedAt == null) {
                                    binding.tableRowSubmissionDate.visibility = View.GONE
                                } else {
                                    binding.tableRowSubmissionDate.visibility = View.VISIBLE
                                    binding.tableTextSubmissionDate.text = DateUtil.parseTime(submittedAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))} ${if (late) (requireContext().getString(
                                        R.string.assignment_detail_submission_submission_date_late)) else ""}" }
                                }
                                if (gradedAt == null) {
                                    binding.tableRowGradingDate.visibility = View.GONE
                                } else {
                                    binding.tableRowGradingDate.visibility = View.VISIBLE
                                    binding.tableTextGradingDate.text = DateUtil.parseTime(gradedAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                                }
                                if (attachments == null || attachments.isEmpty()) {
                                    binding.tableRowAttachments.visibility = View.GONE
                                } else {
                                    binding.tableRowAttachments.visibility = View.VISIBLE
                                    binding.tableTextAttachments.text = attachments.joinToString(", ") { it.displayName }
                                }
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
