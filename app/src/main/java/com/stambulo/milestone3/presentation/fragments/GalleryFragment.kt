package com.stambulo.milestone3.presentation.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.stambulo.milestone3.databinding.FragmentGalleryBinding
import com.stambulo.milestone3.presentation.adapter.GalleryAdapter
import com.stambulo.milestone3.presentation.viewmodels.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val VIEW_TYPE_HEADER = 0

@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding>() {
    private val viewModel: GalleryViewModel by viewModels()
    private val galleryAdapter by lazy { GalleryAdapter() }

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(">>>", "Read Permission Granted")
            showImages()
        } else {
            Log.i(">>>", "Read Permission Denied")
        }
    }

    private val requestPhotoPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(">>>", "Photo Permission Granted")
            takePhoto()
        } else {
            Log.i(">>>", "Photo Permission Denied")
        }
    }

    private var photoResultLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(">>>", "Activity Result - $result")
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                viewModel.repository.saveImage(bitmap, requireContext(), "Milestone3")
            } else {
                /** some error to be shown here */
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestStoragePermission()
        setViews()
    }

    override fun inflateMethod(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentGalleryBinding {
        return FragmentGalleryBinding.inflate(inflater, viewGroup, false)
    }

    private fun setViewModel() {
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imagesWithPaging3.collect {
                galleryAdapter.submitData(it)
            }
        }
    }

    private fun setViews() {
        //TODO: you can create not anonymous, but normal class to be used with it.
        // Let it have a constructor with (Int) -> Int lambda, where arg is position and return is span size.
        val gridLayoutManager = GridLayoutManager(activity, 4)
        binding.apply {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (galleryAdapter.getItemViewType(position)) {
                        VIEW_TYPE_HEADER -> 4
                        else -> 1
                    }
                }
            }
            rvGallery.layoutManager = gridLayoutManager
            rvGallery.adapter = galleryAdapter
            openAlbum.setOnClickListener { requestStoragePermission() }
            fab.setOnClickListener { checkCameraHardware(requireContext()) }
        }
    }

    private fun showImages() {
        setViewModel()
        binding.apply {
            progressBar.isVisible = false
            welcomeView.isVisible = false
            recyclerView.isVisible = true
            fab.isVisible = true
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            requestCameraPermission()
            true
        } else {
            // no camera on this device
            false
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(">>>", "Check Self Permission")
                takePhoto()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                Log.i(">>>", "Should Show Request Permission Rationale")
                requestPhotoPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            else -> {
                requestPhotoPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun requestStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(">>>", "Check Self Permission")
                showImages()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                Log.i(">>>", "Should Show Request Permission Rationale")
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun takePhoto() {
        Log.i(">>>", "takePhoto()")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoResultLaunch.launch(intent)
    }
}
