package com.skku_team2.skku_helper.ui.assignment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.ActivityAssignmentBinding
import com.skku_team2.skku_helper.databinding.DialogEditAssignmentBinding
import com.skku_team2.skku_helper.navigation.AssignmentScreen
import com.skku_team2.skku_helper.utils.getColorAttr
import com.skku_team2.skku_helper.utils.isBright
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = !this.getColorAttr(com.google.android.material.R.attr.colorOnPrimary).isBright()

            view.setPadding(systemBars.left, 0, systemBars.right, 0)

            binding.topSystemBarAssignment.layoutParams.height = systemBars.top
            insets
        }

        binding.toolbarLayoutAssignment.title = viewModel.assignmentDataState.value?.assignment?.name ?: "Assignment"
        binding.toolbarLayoutAssignment.subtitle = viewModel.assignmentDataState.value?.course?.name ?: "Course"
        binding.topAppBarAssignment.setNavigationOnClickListener { finish() }
        binding.topAppBarAssignment.inflateMenu(R.menu.menu_toolbar_assignment)
        binding.topAppBarAssignment.menu.forEach { menuItem ->
            MenuItemCompat.setIconTintList(
                menuItem,
                ColorStateList.valueOf(
                    this.getColorAttr(com.google.android.material.R.attr.colorOnPrimary)
                )
            )
        }
        binding.topAppBarAssignment.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_menu_edit -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        MaterialAlertDialogBuilder(this@AssignmentActivity).apply {
                            val dialogBinding = DialogEditAssignmentBinding.inflate(LayoutInflater.from(this@AssignmentActivity))
                            dialogBinding.editTextAssignmentName.hint = viewModel.assignmentDataState.value?.assignment?.name ?: "Assignment Name"
                            dialogBinding.editTextDueDate.hint = viewModel.assignmentDataState.value?.assignment?.dueAt ?: "Due Date"

                            setTitle(R.string.assignment_dialog_edit_title)
                            setView(dialogBinding.root)
                            setNegativeButton(R.string.assignment_dialog_edit_cancel, null)
                            setPositiveButton(R.string.assignment_dialog_edit_confirm) { _, _ ->
                                viewModel.changeAssignmentData(
                                    name = dialogBinding.editTextAssignmentName.text.toString(),
                                    dueAt = dialogBinding.editTextDueDate.text.toString(),
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.update()
                                }
                            }
                            create().show()
                        }
                    }
                    true
                }
                R.id.item_menu_delete -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        MaterialAlertDialogBuilder(this@AssignmentActivity).apply {
                            setTitle(R.string.assignment_dialog_delete_title)
                            setMessage(R.string.assignment_dialog_delete_message)
                            setNegativeButton(R.string.assignment_dialog_delete_cancel, null)
                            setPositiveButton(R.string.assignment_dialog_delete_confirm) { _, _ ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    viewModel.deleteAssignment()
                                    finish()
                                }
                            }
                            create().show()
                        }
                    }
                    true
                }
                else -> false
            }
        }

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
                    viewModel.assignmentDataState.collect { assignmentData ->
                        val course = assignmentData?.course
                        val assignment = assignmentData?.assignment
                        val custom = assignmentData?.custom
                        binding.toolbarLayoutAssignment.title = custom?.name ?: assignment?.name ?: "Assignment"
                        binding.toolbarLayoutAssignment.subtitle = course?.name ?: "Course"
                    }
                }
            }
        }
    }
}
