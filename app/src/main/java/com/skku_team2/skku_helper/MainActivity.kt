package com.skku_team2.skku_helper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.skku_team2.skku_helper.databinding.ActivityMainBinding
import com.skku_team2.skku_helper.utils.getColorAttr
import com.skku_team2.skku_helper.utils.isBright
import com.skku_team2.skku_helper.ui.AccountFragment
import com.skku_team2.skku_helper.ui.CalendarFragment
import com.skku_team2.skku_helper.ui.HomeFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private val accountFragment = AccountFragment()
    private val calendarFragment = CalendarFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBarMain)

        if(savedInstanceState == null){
            replaceFragment(homeFragment)
        }

        binding.navigationViewMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeItem -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.calendarItem -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.accountItem -> {
                    replaceFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            insetsController.isAppearanceLightStatusBars = !this.getColorAttr(com.google.android.material.R.attr.colorOnPrimary).isBright()

            view.setPadding(systemBars.left, 0, systemBars.right, 0)

            binding.topSystemBarMain.layoutParams.height = systemBars.top
            binding.bottomSystemBarMain.layoutParams.height = systemBars.bottom
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout_main, fragment)
            .commit()
    }
}
