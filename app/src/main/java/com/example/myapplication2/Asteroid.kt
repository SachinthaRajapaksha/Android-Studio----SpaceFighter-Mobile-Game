package com.example.myapplication2

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Asteroid(res: Resources) {

    var speed = 20
    var wasShot = true
    var x = 0
    var y: Int
    var width: Int
    var height: Int
    var aster1: Bitmap


    init {
        aster1 = BitmapFactory.decodeResource(res, R.drawable.aster)

        var screenRatioX = GameView.screenRatioX
        var screenRatioY = GameView.screenRatioY

        width = aster1.width
        height = aster1.height

        width /= 6
        height /= 6

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        aster1 = Bitmap.createScaledBitmap(aster1, width, height, false)

        y = -height
    }

    fun getBird(): Bitmap {
        return aster1
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}
