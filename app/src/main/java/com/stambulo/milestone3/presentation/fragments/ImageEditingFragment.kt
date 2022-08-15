package com.stambulo.milestone3.presentation.fragments

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.milestone3.R
import com.stambulo.milestone3.databinding.FragmentImageEditingBinding
import com.stambulo.milestone3.presentation.intents.EditingIntent
import com.stambulo.milestone3.presentation.states.EditingState
import com.stambulo.milestone3.presentation.viewmodels.EditingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.FileInputStream

@AndroidEntryPoint
class ImageEditingFragment : BaseFragment<FragmentImageEditingBinding>() {
    private val viewModel: EditingViewModel by viewModels()
    private lateinit var imageName: Uri
    private var bitmapContrast = 1F
    private var bitmapBrightness = 0F

    override fun inflateMethod(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentImageEditingBinding {
        return FragmentImageEditingBinding.inflate(inflater, viewGroup, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToGalleryFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageName = Uri.parse(Uri.decode(arguments?.getString("imageName") ?: "no file name"))
        setViews()
        setupViewModel()
        observeViewModel()
        setOnClickListener()
    }

    private fun setViews() {
        binding.apply {
            confirmButton.setOnClickListener {
                Toast.makeText(requireContext(), "Confirm", Toast.LENGTH_LONG).show()
//                viewModel.repository.saveImage(getAdjustedBitmap(), requireContext(), "Milestone3")
                getAdjustedBitmap()
            }
            contrastSlider.addOnChangeListener { _, value, _ ->
                bitmapContrast = value
                editImage.setColorFilter(getContrastBrightnessFilter(value, bitmapBrightness))
                contrastValue.text = ((value * 100) - 100).toInt().toString()
            }
            brightnessSlider.addOnChangeListener { _, value, _ ->
                bitmapBrightness = value
                editImage.setColorFilter(getContrastBrightnessFilter(bitmapContrast, value))
                brightnessValue.text = (value).toInt().toString()
            }
            monochromeSlider.addOnChangeListener { _, value, _ ->
                Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_LONG).show()
            }
            sepiaSlider.addOnChangeListener { _, value, _ ->
                Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_LONG).show()
            }
            vignetteSlider.addOnChangeListener { _, value, _ ->
                Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getContrastBrightnessFilter(
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
        return ColorMatrixColorFilter(cm)
    }

    private fun getAdjustedBitmap(): Bitmap? {
        var bitmap: Bitmap? = null
        val file = getRealPathFromURI(imageName)
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
        } catch (e: Exception) {
            Log.i(">>>", "catch !!!  -  $e")
        }
        Log.i(">>>", "bitmap - $bitmap")
        return bitmap
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            contentUri?.let { requireContext().getContentResolver().query(it, proj, null, null, null) }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = cursor.getString(columnIndex)
            }
        }
        cursor?.close()
        return res
    }

    override fun setupViewModel() {
        binding.progressBar.isVisible = true
        lifecycleScope.launch {
            viewModel.intent.send(EditingIntent.ShowImage(imageName))
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.editingState.collect {
                when (it.type) {
                    EditingState.Type.IDLE -> {}
                    EditingState.Type.Loading -> {
                        renderLoading()
                    }
                    EditingState.Type.ShowImage -> {
                        showImage()
                    }
                }
            }
        }
    }

    private fun renderLoading() {
        binding.progressBar.isVisible = true
    }

    private fun showImage() {
        binding.progressBar.isVisible = false
        imageLoader.loadInto(imageName, binding.editImage)
    }

    private fun setOnClickListener() {
        binding.backChevron.setOnClickListener {
            goToGalleryFragment()
        }
    }

    private fun goToGalleryFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_editingFragment_to_galleryFragment)
    }
}
