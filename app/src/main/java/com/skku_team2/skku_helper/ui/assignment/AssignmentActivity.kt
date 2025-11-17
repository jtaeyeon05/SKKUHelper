package com.skku_team2.skku_helper.ui.assignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ActivityAssignmentBinding
import com.skku_team2.skku_helper.utils.getColorAttr
import com.skku_team2.skku_helper.utils.isBright
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
        setSupportActionBar(binding.topAppBarAssignment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = !this.getColorAttr(com.google.android.material.R.attr.colorOnPrimary).isBright()

            view.setPadding(systemBars.left, 0, systemBars.right, 0)

            binding.topSystemBarAssignment.layoutParams.height = systemBars.top
            insets
        }

        binding.topAppBarAssignment.setNavigationOnClickListener { finish() }

        binding.textViewTest.text = """
            token = ${viewModel.token?.substring(0, 10)}...
            courseId = ${viewModel.courseId}
            assignmentId = ${viewModel.assignmentId}
        """.trimIndent()

        binding.bottomNavigationViewAssignment.isItemActiveIndicatorEnabled = false
        binding.bottomNavigationViewAssignment.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.deleteItem -> {
                    false
                }
                R.id.editItem -> {
                    false
                }
                else -> false
            }
        }

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
