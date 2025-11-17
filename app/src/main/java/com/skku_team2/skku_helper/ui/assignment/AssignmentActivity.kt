package com.skku_team2.skku_helper.ui.assignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ActivityAssignmentBinding
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
            token = ${viewModel.token}
            courseId = ${viewModel.courseId}
            assignmentId = ${viewModel.assignmentId}
        """.trimIndent()
    }
}
