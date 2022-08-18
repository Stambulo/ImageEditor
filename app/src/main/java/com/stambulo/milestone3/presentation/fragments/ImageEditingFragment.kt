package com.stambulo.milestone3.presentation.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import com.stambulo.milestone3.presentation.util.sepia
import com.stambulo.milestone3.presentation.util.toBlackWhite
import com.stambulo.milestone3.presentation.viewmodels.EditingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class ImageEditingFragment : BaseFragment<FragmentImageEditingBinding>() {
    private val viewModel: EditingViewModel by viewModels()
    private lateinit var imageName: Uri

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
                val bitmap = getBitmapOfSelectedImage(imageName)
                if (bitmap != null) {
                    val adjustedBitmap = implementFilterToBitmap(bitmap, colorMatrix)
                    adjustedBitmap.sepia(sepiaLevel)?.let { bmp ->
                        bmp.toBlackWhite(monochromeLevel)
                        viewModel.repository.saveImage(bmp, requireContext(), "Milestone3")
                    }
                }
            }
            contrastSlider.addOnChangeListener { _, value, _ ->
                contrastLevel = value
                editImage.colorFilter = setContrastBrightnessFilter(value, brightnessLevel)
                contrastValue.text = ((value * 100) - 100).toInt().toString()
            }
            brightnessSlider.addOnChangeListener { _, value, _ ->
                brightnessLevel = value
                editImage.colorFilter = setContrastBrightnessFilter(contrastLevel, value)
                brightnessValue.text = (value).toInt().toString()
            }
            monochromeSlider.addOnChangeListener { _, value, _ ->
                monochromeLevel = value.roundToInt()
                editImage.setImageBitmap(getBitmapOfSelectedImage(imageName)?.toBlackWhite(monochromeLevel))
            }
            sepiaSlider.addOnChangeListener { _, value, _ ->
                sepiaLevel = value.roundToInt()
                editImage.setImageBitmap(getBitmapOfSelectedImage(imageName)?.sepia(sepiaLevel))
            }
            vignetteSlider.addOnChangeListener { _, value, _ ->
                Toast.makeText(requireContext(), value.toString(), Toast.LENGTH_LONG).show()
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

    private fun goToGalleryFragment() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_editingFragment_to_galleryFragment)
    }
}
