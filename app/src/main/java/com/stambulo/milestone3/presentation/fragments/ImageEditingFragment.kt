package com.stambulo.milestone3.presentation.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.milestone3.R
import com.stambulo.milestone3.databinding.FragmentImageEditingBinding
import com.stambulo.milestone3.presentation.intents.EditingIntent
import com.stambulo.milestone3.presentation.states.EditingState
import com.stambulo.milestone3.presentation.util.saveToInternalStorage
import com.stambulo.milestone3.presentation.util.sepia
import com.stambulo.milestone3.presentation.util.toBlackWhite
import com.stambulo.milestone3.presentation.util.vignette
import com.stambulo.milestone3.presentation.viewmodels.EditingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class ImageEditingFragment : BaseFragment<FragmentImageEditingBinding>() {
    private val viewModel: EditingViewModel by viewModels()
    private lateinit var imageName: Uri
    private var bitmapIn: Bitmap? = null
    private var bitmapOut: Bitmap? = null

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
        bitmapIn = getBitmapOfSelectedImage(imageName)
        setViews()
        setupViewModel()
        observeViewModel()
        setOnClickListener()
    }

    private fun setViews() {
        binding.apply {
            shareButton.setOnClickListener {
                val uri = bitmapIn?.saveToInternalStorage(requireContext())
                if (uri != null) {
                    shareCacheDirBitmap(uri)
                }
            }
            confirmButton.setOnClickListener {
                bitmapIn = bitmapOut
                savingDialog()
            }
            contrastSlider.addOnChangeListener { _, value, _ ->
                contrastLevel = value
                setContrastBrightnessFilter(value, brightnessLevel)
                val bmp = bitmapIn?.let { implementFilterToBitmap(it, colorMatrix) }
                editImage.setImageBitmap(bmp)
                contrastValue.text = ((value * 100) - 100).toInt().toString()
                bitmapOut = bmp
            }
            brightnessSlider.addOnChangeListener { _, value, _ ->
                brightnessLevel = value
                setContrastBrightnessFilter(contrastLevel, value)
                val bmp = bitmapIn?.let { implementFilterToBitmap(it, colorMatrix) }
                editImage.setImageBitmap(bmp)
                brightnessValue.text = (value).toInt().toString()
                bitmapOut = bmp
            }
            monochromeSlider.addOnChangeListener { _, value, _ ->
                val bmp = bitmapIn?.toBlackWhite(value.roundToInt())
                editImage.setImageBitmap(bmp)
                monochromeValue.text = (value).toInt().toString()
                if (bmp != null) {
                    bitmapOut = bmp
                }
            }
            sepiaSlider.addOnChangeListener { _, value, _ ->
                val bmp = bitmapIn?.sepia(value.roundToInt())
                editImage.setImageBitmap(bmp)
                sepiaValue.text = (value).toInt().toString()
                if (bmp != null) {
                    bitmapOut = bmp
                }
            }
            vignetteSlider.addOnChangeListener { _, value, _ ->
                val bmp = bitmapIn?.vignette(value.roundToInt())
                editImage.setImageBitmap(bmp)
                vignetteValue.text = (value).toInt().toString()
                if (bmp != null) {
                    bitmapOut = bmp
                }
            }
        }
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

    private fun savingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Save Dialog")
        builder.setMessage("Save Image in storage ?")
        builder.setPositiveButton("Save"){ _, _ ->
            bitmapOut?.let { bmp ->
                viewModel.repository.saveImage(bmp, requireContext(), "Milestone3")
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.setNeutralButton("Proceed Editing") { _, _ -> }
        builder.show()
    }

    private fun goToGalleryFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_editingFragment_to_galleryFragment)
    }
}
