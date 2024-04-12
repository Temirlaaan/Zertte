package com.example.zertte

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.zertte.databinding.ActivityMainGuideBinding
import com.example.zertte.ui.activities.BaseActivity

class MainActivityGuide : BaseActivity() {

    private lateinit var binding: ActivityMainGuideBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.activity_main_nav_guide_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(){
        doubleBackToExit()
    }
}