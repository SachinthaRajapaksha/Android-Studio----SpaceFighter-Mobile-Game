package com.example.myapplication2

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Missile(res: Resources) {

    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var missile: Bitmap

    init {
        missile = BitmapFactory.decodeResource(res, R.drawable.missile)

        var screenRatioX = GameView.screenRatioX
        var screenRatioY = GameView.screenRatioY

        width = missile.width
        height = missile.height

        width /= 4
        height /= 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        missile = Bitmap.createScaledBitmap(missile, width, height, false)
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}