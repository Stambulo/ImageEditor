package com.stambulo.milestone3.presentation.fragments

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
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

@AndroidEntryPoint
class ImageEditingFragment : BaseFragment<FragmentImageEditingBinding>() {
    private val viewModel: EditingViewModel by viewModels()
    private lateinit var imageName: String
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
        imageName = arguments?.getString("imageName") ?: "no file name"
        setViews()
        setupViewModel()
        observeViewModel()
        setOnClickListener()
    }

    private fun setViews(){
        binding.apply {
            confirmButton.setOnClickListener {
                Toast.makeText(requireContext(), "Confirm", Toast.LENGTH_LONG).show()
//                viewModel.repository.saveImage(getAdjustedBitmap(), requireContext(), "Milestone3")
            }
            contrastSlider.addOnChangeListener { _, value, _ ->
                bitmapContrast = value
                editImage.setColorFilter(getContrastBrightnessFilter(value, bitmapBrightness))
                contrastValue.text = ((value*100)-100).toInt().toString()
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

    private fun getContrastBrightnessFilter(contrast: Float, brightness: Float): ColorMatrixColorFilter {
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

//    private fun getAdjustedBitmap(): Bitmap{
//        val fileName = imageName
//        val file = File(fileName)
//        val bmp = BitmapFactory.decodeFile(file.toString())
//        Log.i(">>>", imageName)
//        Log.i(">>>", "$bmp")
//        return bmp
//    }

    override fun setupViewModel() {
        binding.progressBar.isVisible = true
        lifecycleScope.launch {
            viewModel.intent.send(EditingIntent.ShowImage(imageName))
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.editingState.collect{
                when (it.type) {
                    EditingState.Type.IDLE -> {}
                    EditingState.Type.Loading -> { renderLoading() }
                    EditingState.Type.ShowImage -> { showImage() }
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

    private fun setOnClickListener(){
        binding.backChevron.setOnClickListener {
            goToGalleryFragment()
        }
    }

    private fun goToGalleryFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_editingFragment_to_galleryFragment)
    }
}
