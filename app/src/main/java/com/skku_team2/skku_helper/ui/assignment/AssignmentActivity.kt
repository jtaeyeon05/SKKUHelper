package com.skku_team2.skku_helper.ui.assignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ActivityAssignmentBinding
import kotlinx.coroutines.launch
import kotlin.getValue


class AssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignmentBinding

    private val viewModel: AssignmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.textViewTest.text = """
            token = ${viewModel.token?.substring(0, 10)}...
            courseId = ${viewModel.courseId}
            assignmentId = ${viewModel.assignmentId}
        """.trimIndent()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.courseState.collect { courseState ->
                        if (courseState != null) {
                            binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                                    "\n" +
                                    "course name: ${courseState?.name}\n" +
                                    "course originalName: ${courseState?.originalName}\n" +
                                    "course courseCode: ${courseState?.courseCode}\n" +
                                    "course uuid: ${courseState?.uuid}\n" +
                                    "course accountId: ${courseState?.accountId}\n" +
                                    "course createdAt: ${courseState?.createdAt}\n"
                        }
                    }
                }
                launch {
                    viewModel.assignmentState.collect { assignmentState ->
                        if (assignmentState != null) {
                            binding.textViewTest.text = "${binding.textViewTest.text}\n" +
                                    "\n" +
                                    "assignment name: ${assignmentState?.name}\n" +
                                    "assignment description: ${assignmentState?.description}\n" +
                                    "assignment position: ${assignmentState?.position}\n" +
                                    "assignment courseId: ${assignmentState?.courseId}\n" +
                                    "assignment isQuizAssignment: ${assignmentState?.isQuizAssignment}\n" +
                                    "assignment createdAt: ${assignmentState?.createdAt}\n"
                        }
                    }
                }
                launch {
                    viewModel.uiState.collect { uiState ->
                        // TODO
                    }
                }
            }
        }
    }
}
