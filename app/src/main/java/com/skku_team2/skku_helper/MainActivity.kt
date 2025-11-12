package com.skku_team2.skku_helper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.skku_team2.skku_helper.databinding.ActivityMainBinding
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null){
            replaceFragment(homeFragment)
        }

        binding.navigationView.setOnItemSelectedListener { item ->
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            enableEdgeToEdge()
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
