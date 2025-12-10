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
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ActivityAssignmentBinding
import com.skku_team2.skku_helper.navigation.AssignmentScreen
import com.skku_team2.skku_helper.utils.getColorAttr
import com.skku_team2.skku_helper.utils.isBright
import kotlinx.coroutines.flow.combine
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

        binding.toolbarLayoutAssignment.title = viewModel.assignmentState.value?.name ?: "Assignment"
        binding.toolbarLayoutAssignment.subtitle = viewModel.courseState.value?.name ?: "Course"
        binding.topAppBarAssignment.setNavigationOnClickListener { finish() }

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        val navController = navHostFragment.navController
        navController.graph = navController.createGraph(AssignmentScreen.Information) {
            fragment<InformationFragment, AssignmentScreen.Information> {
                label = "Information"
            }
            fragment<DetailFragment, AssignmentScreen.Detail> {
                label = "Detail"
            }
        }

        if (savedInstanceState == null) binding.bottomNavigationViewAssignment.selectedItemId = R.id.item_information
        binding.bottomNavigationViewAssignment.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_information -> {
                    navController.navigate(AssignmentScreen.Information)
                    true
                }
                R.id.item_detail -> {
                    navController.navigate(AssignmentScreen.Detail)
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(
                        viewModel.courseState,
                        viewModel.assignmentState
                    ) { courseState, assignmentState ->
                        courseState to assignmentState
                    }.collect { (courseState, assignmentState) ->
                        binding.toolbarLayoutAssignment?.title = assignmentState?.name ?: "Assignment"
                        binding.toolbarLayoutAssignment?.subtitle = courseState?.name ?: "Course"
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
