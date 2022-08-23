package com.stambulo.milestone3.presentation.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

// Extension method to save bitmap to internal storage
fun Bitmap.saveToInternalStorage(context: Context): Uri {
    // Get the context wrapper instance
    val wrapper = ContextWrapper(context)

    // Initializing a new file
    // The bellow line return a directory in internal storage
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)


    // Create a file to save the image, random file name
    //file = File(file, "${UUID.randomUUID()}.png")

    file = File(file, "image.png")

    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // Compress bitmap
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)

        // Flush the stream
        stream.flush()

        // Close stream
        stream.close()
    } catch (e: IOException){ // Catch the exception
        e.printStackTrace()
    }

    // Return the saved image uri
    return Uri.parse(file.absolutePath)
}

// extension function Vignette
fun Bitmap.vignette(threshold:Int = 128):Bitmap?{
    val bitmap = copy(config,true)
    val width: Int = bitmap.width
    val height: Int = bitmap.height
    val tenthLeftRight: Int = width / 5
    val tenthTopBottom: Int = height / 5
    val canvas = Canvas(bitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, null)

    // Gradient left - right
    val linGradLR: Shader = LinearGradient(
        0f, (height / 2).toFloat(), (tenthLeftRight / 2).toFloat(), (height / 2).toFloat(),
        Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP
    )

    // Gradient top - bottom
    val linGradTB: Shader = LinearGradient(
        (width / 20).toFloat(), 0f, (width / 2).toFloat(), tenthTopBottom.toFloat(), Color.BLACK,
        Color.TRANSPARENT, Shader.TileMode.CLAMP
    )

    // Gradient right - left
    val linGradRL: Shader = LinearGradient(
        width.toFloat(), (height / 2).toFloat(),
        (width - tenthLeftRight).toFloat(),
        (height / 2).toFloat(), Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP
    )

    // Gradient bottom - top
    val linGradBT: Shader = LinearGradient(
        (width / 2).toFloat(), height.toFloat(), (width / 2).toFloat(),
        height - tenthTopBottom.toFloat(), Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP
    )

    val paint = Paint()
    paint.shader = linGradLR
    paint.isAntiAlias = true
    paint.isDither = true
    paint.alpha = threshold

    // Rect for Grad left - right
    var rect = Rect(0, 0, tenthLeftRight, height)
    var rectf = RectF(rect)
    canvas.drawRect(rectf, paint)

    // Rect for Grad top - bottom
    paint.shader = linGradTB
    rect = Rect(0, 0, width, tenthTopBottom)
    rectf = RectF(rect)
    canvas.drawRect(rectf, paint)

    // Rect for Grad right - left
    paint.shader = linGradRL
    rect = Rect(width, 0, width - tenthLeftRight, height)
    rectf = RectF(rect)
    canvas.drawRect(rectf, paint)

    // Rect for Grad bottom - top
    paint.shader = linGradBT
    rect = Rect(0, height - tenthTopBottom, width, height)
    rectf = RectF(rect)
    canvas.drawRect(rectf, paint)
    return bitmap
}

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
