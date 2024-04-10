package com.example.zertte

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
            setupWithNavController(binding.bottomNavigationView, navController)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(){
        doubleBackToExit()
    }
}