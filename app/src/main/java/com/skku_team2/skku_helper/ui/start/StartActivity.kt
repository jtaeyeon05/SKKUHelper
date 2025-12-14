package com.skku_team2.skku_helper.ui.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.skku_team2.skku_helper.databinding.ActivityStartBinding
import com.skku_team2.skku_helper.navigation.StartScreen

/**
 * 앱을 실행했을 떄 보여지는 Activity
 */

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation 설정
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerViewStart.id) as NavHostFragment
        val navController = navHostFragment.navController
        navController.graph = navController.createGraph(StartScreen.Start) {
            fragment<LobbyFragment, StartScreen.Start> {
                label = "Home"
            }
            fragment<LogInFragment, StartScreen.LogIn> {
                label = "LogIn"
            }
        }
    }
}
