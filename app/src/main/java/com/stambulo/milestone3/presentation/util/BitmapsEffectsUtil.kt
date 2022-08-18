package com.stambulo.milestone3.presentation.util

import android.graphics.*

// extension function to convert bitmap to black and white
fun Bitmap.toBlackWhite(threshold:Int = 128):Bitmap?{
    val bitmap = copy(config,true)
    var A: Int; var R: Int; var G: Int; var B: Int; var pixel: Int

    for (x in 0 until width) {
        for (y in 0 until height) {
            // get pixel color
            pixel = getPixel(x, y)
            A = Color.alpha(pixel)
            R = Color.red(pixel)
            G = Color.green(pixel)
            B = Color.blue(pixel)
            var gray = (0.2989 * R + 0.5870 * G + 0.1140 * B).toInt()

            // use 128 as default threshold, above -> white, below -> black
            gray = if (gray > threshold) {
                255
            } else {
                0
            }

            // set new pixel color to output bitmap
            bitmap.setPixel(x, y, Color.argb(A, gray, gray, gray))
        }
    }
    return bitmap
}

// extension function to apply sepia effect on bitmap
fun Bitmap.sepia(depth:Int = 20): Bitmap?{
    var r:Int; var g:Int; var b:Int; var c:Int; var gry:Int
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val paint = Paint()
    val matrix = ColorMatrix()
    matrix.setScale(.3f, .3f, .3f, 1.0f)
    val filter = ColorMatrixColorFilter(matrix)
    paint.colorFilter = filter

    Canvas(bitmap).drawBitmap(this, 0f, 0f, paint)
    for (x in 0 until width) {
        for (y in 0 until height) {
            c = this.getPixel(x, y)
            r = Color.red(c)
            g = Color.green(c)
            b = Color.blue(c)
            gry = (r + g + b) / 3
            b = gry
            g = b
            r = g
            r += depth * 2
            g += depth
            if (r > 255) {
                r = 255
            }
            if (g > 255) {
                g = 255
            }
            bitmap.setPixel(x, y, Color.rgb(r, g, b))
        }
    }
    return bitmap
}
