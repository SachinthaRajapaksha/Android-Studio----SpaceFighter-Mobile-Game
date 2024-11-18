package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LaunchScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launch_screen)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@LaunchScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}