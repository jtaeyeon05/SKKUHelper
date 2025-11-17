package com.skku_team2.skku_helper.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar
import com.skku_team2.skku_helper.databinding.ActivityMainBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.navigation.MainScreen
import com.skku_team2.skku_helper.utils.getColorAttr
import com.skku_team2.skku_helper.utils.isBright


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBarMain)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = !this.getColorAttr(R.attr.colorOnPrimary).isBright()

            view.setPadding(systemBars.left, 0, systemBars.right, 0)

            binding.topSystemBarMain.layoutParams.height = systemBars.top
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerViewMain.id) as NavHostFragment
        val navController = navHostFragment.navController
        navController.graph = navController.createGraph(MainScreen.Home) {
            fragment<CalendarFragment, MainScreen.Calendar> {
                label = "Calendar"
            }
            fragment<HomeFragment, MainScreen.Home> {
                label = "Home"
            }
            fragment<AccountFragment, MainScreen.Account> {
                label = "Account"
            }
        }

        if (savedInstanceState == null) binding.bottomNavigationViewMain.selectedItemId = com.skku_team2.skku_helper.R.id.homeItem
        binding.bottomNavigationViewMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.skku_team2.skku_helper.R.id.calendarItem -> {
                    navController.navigate(MainScreen.Calendar)
                    true
                }
                com.skku_team2.skku_helper.R.id.homeItem -> {
                    navController.navigate(MainScreen.Home)
                    true
                }
                com.skku_team2.skku_helper.R.id.accountItem -> {
                    navController.navigate(MainScreen.Account)
                    true
                }
                else -> false
            }
        }

        if (intent.getBooleanExtra(IntentKey.EXTRA_IS_AUTO_LOGIN, false)) {
            Snackbar.make(binding.main, this.getString(com.skku_team2.skku_helper.R.string.main_message_auto_login), Snackbar.LENGTH_SHORT).apply {
                anchorView = binding.bottomNavigationViewMain
            }.show()
        }
    }
}
