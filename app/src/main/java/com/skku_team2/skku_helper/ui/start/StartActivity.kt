package com.skku_team2.skku_helper.ui.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.skku_team2.skku_helper.databinding.ActivityStartBinding
import com.skku_team2.skku_helper.navigation.StartScreen

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

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