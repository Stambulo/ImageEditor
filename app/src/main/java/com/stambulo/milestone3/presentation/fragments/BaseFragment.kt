package com.stambulo.milestone3.presentation.fragments

import android.R
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.FileObserver
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.stambulo.milestone3.presentation.image.IImageLoader
import java.io.*
import javax.inject.Inject

abstract class BaseFragment<Binding : ViewBinding> : Fragment() {
    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    private var _binding: Binding? = null
    val binding: Binding get() = _binding ?: throw NullPointerException()
    protected var contrastLevel = 1F
    protected var brightnessLevel = 0F
    protected var colorMatrix = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 1f,
            0f, 1f, 0f, 0f, 1f,
            0f, 0f, 1f, 0f, 1f,
            0f, 0f, 0f, 1f, 0f
        )
    )

    abstract fun inflateMethod(inflater: LayoutInflater, viewGroup: ViewGroup?): Binding
    abstract fun setupViewModel()
    abstract fun observeViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setContrastBrightnessFilter(
        contrast: Float,
        brightness: Float
    ): ColorMatrixColorFilter {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix = cm
        return ColorMatrixColorFilter(cm)
    }

    // Extension function to share save bitmap in cache directory and share
    fun shareCacheDirBitmap(uri: Uri){
        val fis = FileInputStream(uri.path)  // 2nd line
        val bitmap = BitmapFactory.decodeStream(fis)
        fis.close()

        try {
            val file = File("${requireActivity().cacheDir}/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
            val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName + ".provider", file)

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.type = "image/*"
            this.startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getUriFromBitmap(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(requireActivity().externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = Uri.parse(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    fun getBitmapOfSelectedImage(imageName: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        val file = getRealPathFromURI(imageName)
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
        } catch (e: Exception) {
            Log.i(">>>", "getAdjustedBitmap. Catch - $e")
        }
        return bitmap
    }

    fun implementFilterToBitmap(bitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap {
        val bmp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val cm = ColorMatrix()
        cm.set(colorMatrix)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bmp
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            contentUri?.let { requireContext().contentResolver.query(it, proj, null, null, null) }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = cursor.getString(columnIndex)
            }
        }
        cursor?.close()
        return res
    }
}
