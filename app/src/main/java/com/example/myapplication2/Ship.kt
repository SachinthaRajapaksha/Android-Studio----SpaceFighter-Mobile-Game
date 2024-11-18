package com.example.myapplication2

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Ship(private val gameView: GameView, screenY: Int, res: Resources) {

    var toShoot = 0
    var isGoingUp = false
    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var shootCounter = 0
    var jet: Bitmap
    var dead: Bitmap

    init {
        jet = BitmapFactory.decodeResource(res, R.drawable.jet)


        var screenRatioX = GameView.screenRatioX
        var screenRatioY = GameView.screenRatioY

        width = jet.width
        height = jet.height

        width /= 4
        height /= 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        jet = Bitmap.createScaledBitmap(jet, width, height, false)

        dead = BitmapFactory.decodeResource(res, R.drawable.expl)
        dead = Bitmap.createScaledBitmap(dead, width, height, false)

        y = screenY / 2
        x = (64 * screenRatioX).toInt()
    }

    fun getFlight(): Bitmap {

        if (toShoot != 0) {
            when (shootCounter) {
                0 -> shootCounter = 1
                1 -> shootCounter = 2
                2 -> shootCounter = 3
                3 -> shootCounter = 4
                else -> {
                    shootCounter = 0
                    toShoot--
                    gameView.newMissile()
                }
            }
            return jet // Return the same jet image for shooting animation
        }
        return jet
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    fun getDeadBitmap(): Bitmap {
        return dead
    }
}
