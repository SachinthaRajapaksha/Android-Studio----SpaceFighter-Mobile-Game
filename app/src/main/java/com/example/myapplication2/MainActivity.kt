package com.example.myapplication2

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.app.AlertDialog
import android.content.DialogInterface

class MainActivity : AppCompatActivity() {

    private lateinit var aboutApp: Button
    private lateinit var helpApp: Button
    private var isMute: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        aboutApp = findViewById(R.id.aboutApp)
        helpApp = findViewById(R.id.helpButton)

        // Initialize the MediaPlayer instance if it's not already initialized
        if (!AudioPlayer.isInitialized()) {
            AudioPlayer.init(this, R.raw.menubg)
            AudioPlayer.start()
        }

        findViewById<View>(R.id.plays).setOnClickListener {
            // Stop and release the MediaPlayer when starting the game activity
            AudioPlayer.stop()
            startActivity(Intent(this@MainActivity, GameActivity::class.java))
        }

        val highScoreTxt: TextView = findViewById(R.id.highScoreTxt)

        val prefs: SharedPreferences = getSharedPreferences("game", MODE_PRIVATE)
        highScoreTxt.text = "HighScore: ${prefs.getInt("highscore", 0)}"

        isMute = prefs.getBoolean("isMute", false)

        val volumeCtrl: ImageView = findViewById(R.id.volumeCtrl)

        volumeCtrl.setImageResource(if (isMute) R.drawable.baseline_volume_off_24 else R.drawable.baseline_volume_up_24)

        volumeCtrl.setOnClickListener {
            isMute = !isMute
            volumeCtrl.setImageResource(if (isMute) R.drawable.baseline_volume_off_24 else R.drawable.baseline_volume_up_24)

            // Apply the mute state to the background music
            if (isMute) {
                AudioPlayer.mute()
            } else {
                AudioPlayer.unmute()
            }

            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("isMute", isMute)
            editor.apply()
        }

        aboutApp.setOnClickListener {
            startActivity(Intent(this, AboutApp::class.java))
        }

        helpApp.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer instance when the activity is destroyed
        if (isFinishing) {
            AudioPlayer.release()
        }
    }

    fun showQuitConfirmationDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit Game")
        builder.setMessage("Are you sure you want to exit the game?")
        builder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing
        }
        val dialog = builder.create()
        dialog.show()
    }

}
