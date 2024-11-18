package com.example.myapplication2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class GameView(private val activity: GameActivity, screenX: Int, screenY: Int) : SurfaceView(activity), Runnable {

    private var thread: Thread = Thread()

    private var isPlaying = false
    private var isGameOver = false
    private var screenX: Int = 0
    private var screenY: Int = 0
    private var score = 0
    private var paint: Paint
    private var asters = arrayOf<Asteroid>()
    private var prefs: SharedPreferences
    private var random: Random
    private lateinit var soundPool: SoundPool
    private var missiles: MutableList<Missile>
    private var sound: Int
    private lateinit var ship: Ship
    private lateinit var background1: Background
    private lateinit var background2: Background
    private lateinit var mediaPlayer: MediaPlayer
    private var isMute: Boolean = false

    companion object {
        var screenRatioX: Float = 0F
        var screenRatioY: Float = 0F
    }

    init {
        this.screenX = screenX
        this.screenY = screenY
        screenRatioX = 1920f / screenX
        screenRatioY = 1080f / screenY

        background1 = Background(screenX, screenY, resources)
        background2 = Background(screenX, screenY, resources)

        ship = Ship(this, screenY, resources)

        missiles = ArrayList()

        background2.x = screenX

        paint = Paint()
        paint.textSize = 128F
        paint.color = Color.WHITE


        asters = Array(4) {
            Asteroid(resources)
        }

        random = Random()

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()

            soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }

        sound = soundPool.load(activity, R.raw.shoot, 1)


        initializeMediaPlayer()
    }

    override fun run() {
        startBackgroundMusic()
        while (isPlaying) {
            update()
            draw()
            sleep()
        }
        stopBackgroundMusic()
    }

    private fun update() {
        background1.x -= (10 * screenRatioX).toInt()
        background2.x -= (10 * screenRatioX).toInt()

        if (background1.x + background1.background.width < 0) {
            background1.x = screenX
        }

        if (background2.x + background2.background.width < 0) {
            background2.x = screenX
        }

        if (ship.isGoingUp)
            ship.y -= (30 * screenRatioY).toInt()
        else
            ship.y += (30 * screenRatioY).toInt()

        if (ship.y < 0)
            ship.y = 0

        if (ship.y >= screenY - ship.height)
            ship.y = screenY - ship.height

        /* missile shoot logic */
        val trash = ArrayList<Missile>()

        for (missile in missiles) {
            if (missile.x > screenX)
                trash.add(missile)

            missile.x += (50 * screenRatioX).toInt()


            for (aster in asters) {
                if (Rect.intersects(aster.getCollisionShape(), missile.getCollisionShape())) {
                    score++
                    aster.x = -500
                    missile.x = screenX + 500
                    aster.wasShot = true
                }
            }
        }

        for (missile in trash) {
            missiles.remove(missile)
        }

            /* asters */
            for (aster in asters) {
                aster.x -= aster.speed

                if (aster.x + aster.width < 0) {
                    if (!aster.wasShot) {
                        isGameOver = true
                        return
                    }
                    /* max speed */
                    val bound = (25 * screenRatioX).toInt()
                    aster.speed = random.nextInt(bound)

                    /* min speed */
                    if (aster.speed < (10 * screenRatioX).toInt())
                        aster.speed = (10 * screenRatioX).toInt()

                    aster.x = screenX
                    aster.y = random.nextInt(screenY - aster.height)

                    aster.wasShot = false
                }



            if (Rect.intersects(aster.getCollisionShape(), ship.getCollisionShape())) {
                isGameOver = true
                return
            }
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)


            for (aster in asters)
                canvas.drawBitmap(aster.getBird(), aster.x.toFloat(), aster.y.toFloat(), paint)

            canvas.drawText(score.toString(), (screenX / 2).toFloat(), 164F, paint)

            if (isGameOver) {
                isPlaying = false
                canvas.drawBitmap(ship.getDeadBitmap(), ship.x.toFloat(), ship.y.toFloat(), paint)
                holder.unlockCanvasAndPost(canvas)
                saveIfHighScore()
                waitBeforeExiting()
                return
            }

            canvas.drawBitmap(ship.getFlight(), ship.x.toFloat(), ship.y.toFloat(), paint)

            for (missile in missiles)
                canvas.drawBitmap(missile.missile, missile.x.toFloat(), missile.y.toFloat(), paint)

            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun waitBeforeExiting() {
        try {
            Thread.sleep(2000)
            activity.runOnUiThread {
                val gameOverDialog = Dialog(activity)
                gameOverDialog.setCancelable(false)
                gameOverDialog.setContentView(R.layout.activity_game_over)
                val scoreText = gameOverDialog.findViewById<TextView>(R.id.scoreText)
                scoreText.text = "Your Score: $score"
                val retryButton = gameOverDialog.findViewById<Button>(R.id.retryButton)
                retryButton.setOnClickListener {
                    gameOverDialog.dismiss()
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                }
                gameOverDialog.show()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    private fun saveIfHighScore() {
        if (prefs.getInt("highscore", 0) < score) {
            val editor = prefs.edit()
            editor.putInt("highscore", score)
            editor.apply()
        }
    }

    private fun sleep() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread.start()
        startBackgroundMusic()
    }

    fun pause() {
        try {
            isPlaying = false
            thread.join()
            stopBackgroundMusic()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < screenX / 2) {
                    ship.isGoingUp = true
                }
            }
            MotionEvent.ACTION_UP -> {
                ship.isGoingUp = false
                if (event.x > screenX / 2)
                    ship.toShoot++
            }
        }
        return true
    }

    fun newMissile() {
        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1F, 1F, 0, 0, 1F)

        val missile = Missile(resources)
        missile.x = ship.x + ship.width
        missile.y = ship.y + (ship.height / 3)
        missiles.add(missile)
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer.create(activity, R.raw.ingamebg)
        mediaPlayer.isLooping = true
    }

    private fun startBackgroundMusic() {
        if (!isMute) {
            mediaPlayer.start()
        }
    }

    private fun stopBackgroundMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }


}
