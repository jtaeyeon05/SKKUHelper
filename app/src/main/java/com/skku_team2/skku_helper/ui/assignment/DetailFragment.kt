package com.skku_team2.skku_helper.ui.assignment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentDetailBinding
import com.skku_team2.skku_helper.utils.DateUtil
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
                    assignmentViewModel.assignmentDataState.collect { assignmentData ->
                        val course = assignmentData?.course
                        val assignment = assignmentData?.assignment
                        val custom = assignmentData?.custom

                        binding.buttonAssignment.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, assignment?.htmlUrl?.toUri())
                            requireContext().startActivity(intent)
                        }
                        if (!(assignmentData?.isSubmitted ?: true)) {
                            binding.buttonSubmission.visibility = View.GONE
                        } else {
                            binding.buttonSubmission.visibility = View.VISIBLE
                            binding.buttonSubmission.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, assignment?.submission?.previewUrl?.toUri())
                                requireContext().startActivity(intent)
                            }
                        }

                        if (assignment == null || course == null) {
                            binding.layoutInformation.visibility = View.GONE
                        } else {
                            binding.layoutInformation.visibility = View.VISIBLE

                            binding.tableTextName.text = if (custom?.name == null) assignment.name else "${custom.name} (${assignment.name})"
                            binding.tableTextCourse.text = course.name
                            binding.tableTextAssignmentType.text = if (assignment.isQuizAssignment) "Quiz" else "Assignment"
                            if (assignment.pointsPossible == null) {
                                binding.tableRowMaxPoints.visibility = View.GONE
                            } else {
                                binding.tableRowMaxPoints.visibility = View.VISIBLE
                                binding.tableTextMaxPoints.text = assignment.pointsPossible.toString()
                            }
                            binding.tableTextSubmitted.text = if (assignmentData.isSubmitted) "Yes" else "No"
                            binding.tableTextLocked.text = if (assignment.lockedForUser ?: false) "Yes" else "No"
                            if (assignment.createdAt == null) {
                                binding.tableRowCreatedDate.visibility = View.GONE
                            } else {
                                binding.tableRowCreatedDate.visibility = View.VISIBLE
                                binding.tableTextCreatedDate.text = DateUtil.parseTime(assignment.createdAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                            }
                            if (assignment.updatedAt == null) {
                                binding.tableRowUpdatedDate.visibility = View.GONE
                            } else {
                                binding.tableRowUpdatedDate.visibility = View.VISIBLE
                                binding.tableTextUpdatedDate.text = DateUtil.parseTime(assignment.updatedAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                            }
                            if (assignment.dueAt == null) {
                                binding.tableRowDueDate.visibility = View.GONE
                            } else {
                                binding.tableRowDueDate.visibility = View.VISIBLE
                                binding.tableTextDueDate.text =
                                    if (custom?.dueAt == null) DateUtil.parseTime(assignment.dueAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                                    else "${DateUtil.parseTime(custom!!.dueAt!!).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }} (${DateUtil.parseTime(assignment.dueAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }})"
                            }
                            if (assignment.lockAt == null || assignment.lockedForUser == false) {
                                binding.tableRowLockedDate.visibility = View.GONE
                            } else {
                                binding.tableRowLockedDate.visibility = View.VISIBLE
                                binding.tableTextLockedDate.text = DateUtil.parseTime(assignment.lockAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                            }
                            if (assignment.unlockAt == null || assignment.lockedForUser == true) {
                                binding.tableRowUnlockedDate.visibility = View.GONE
                            } else {
                                binding.tableRowUnlockedDate.visibility = View.VISIBLE
                                binding.tableTextUnlockedDate.text = DateUtil.parseTime(assignment.unlockAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))}" }
                            }
                        }

                        if (!(assignmentData?.isSubmitted ?: true)) {
                            binding.layoutSubmission.visibility = View.GONE
                        } else if (assignment?.submission != null) {
                            binding.layoutSubmission.visibility = View.VISIBLE
                            assignment.submission.run {
                                if (grade == null) {
                                    binding.tableRowGrade.visibility = View.GONE
                                } else {
                                    binding.tableRowGrade.visibility = View.VISIBLE
                                    binding.tableTextGrade.text = "$score / ${assignment.pointsPossible}"
                                }
                                binding.tableRowSubmissionType.visibility = View.VISIBLE
                                binding.tableTextSubmissionType.text = when (submissionType) {
                                    "online_quiz" -> "Quiz"
                                    "online_upload" -> "Assignment"
                                    else -> submissionType
                                }
                                if (submittedAt == null) {
                                    binding.tableRowSubmissionDate.visibility = View.GONE
                                } else {
                                    binding.tableRowSubmissionDate.visibility = View.VISIBLE
                                    binding.tableTextSubmissionDate.text = DateUtil.parseTime(submittedAt).let { "${it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))} ${if (late) (requireContext().getString(R.string.assignment_detail_submission_submission_date_late)) else ""}" }
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
