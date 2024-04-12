package com.example.zertte.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.zertte.R
import com.example.zertte.ui.activities.user.ActivityLogin

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        @Suppress("DEPRECATION")
        android.os.Handler().postDelayed(
            {
                startActivity(Intent(this@SplashActivity, ActivityLogin::class.java))
                finish()
            },
            1500
        )
    }
}