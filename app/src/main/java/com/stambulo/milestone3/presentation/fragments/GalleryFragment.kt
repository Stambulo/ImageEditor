package com.stambulo.milestone3.presentation.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
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
import com.stambulo.milestone3.BuildConfig
import com.stambulo.milestone3.databinding.FragmentGalleryBinding
import com.stambulo.milestone3.presentation.adapter.GalleryAdapter
import com.stambulo.milestone3.presentation.viewmodels.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
private const val VIEW_TYPE_HEADER = 0

@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding>() {
    private val viewModel: GalleryViewModel by viewModels()
    private val galleryAdapter by lazy { GalleryAdapter() }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(">>>", "Granted")
            takePhoto()
        } else {
            Log.i(">>>", "Denied")
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
        checkPermissions()
        setViews()
    }

    override fun inflateMethod(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentGalleryBinding {
        return FragmentGalleryBinding.inflate(inflater, viewGroup, false)
    }

    private fun setViewModel() {
        //TODO: you should add loading to not show empty page for some seconds.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imagesWithPaging3.collect {
                galleryAdapter.submitData(it)
            }
        }
    }

    private fun setViews() {
        //TODO: you can create not anonymous, but normal class to be used with it. Let it have a constructor with (Int) -> Int lambda, where arg is position and return is span size.
        val gridLayoutManager = GridLayoutManager(activity, 4)
        binding.rvGallery.apply {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (galleryAdapter.getItemViewType(position)) {
                        VIEW_TYPE_HEADER -> 4
                        else -> 1
                    }
                }
            }
            layoutManager = gridLayoutManager
            adapter = galleryAdapter
        }
        binding.openAlbum.setOnClickListener { openMediaStore() }
        binding.grantPermissionButton.setOnClickListener { openMediaStore() }
        binding.fab.setOnClickListener { checkCameraHardware(requireContext()) }
    }

    private fun checkPermissions() {
        if (!haveStoragePermission()) {
            binding.welcomeView.isVisible = true
        } else {
            openMediaStore()
        }
    }

    private fun haveStoragePermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    private fun showImages() {
        setViewModel()
        binding.apply {
            welcomeView.isVisible = false
            permissionRationaleView.isVisible = false
            recyclerView.isVisible = true
            fab.isVisible = true
        }
    }

    private fun showNoAccess() {
        binding.apply {
            welcomeView.isVisible = false
            permissionRationaleView.isVisible = true
        }
    }

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            //TODO: deprecated requests, you already use new API
            requestPermissions(
                permissions,
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    //TODO: deprecated requests, use new API (https://developer.android.com/training/permissions/requesting), you already use it.
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                } else {
                    // If we weren't granted the permission, check to see if we should show
                    // rationale for the permission.
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    if (showRationale) {
                        showNoAccess()
                    } else {
                        goToSettings()
                    }
                }
                return
            }
        }
    }

    private fun goToSettings() {
        val packageName = BuildConfig.APPLICATION_ID
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            try {
                startActivity(intent)
            } catch (activityNotFound: ActivityNotFoundException){
                Log.i(">>>", "Error: Activity not found - $activityNotFound")
            }
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
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePhoto() {
        Log.i(">>>", "takePhoto()")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoResultLaunch.launch(intent)
    }
}