package com.stambulo.milestone3.view.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.stambulo.milestone3.databinding.FragmentGalleryBinding
import com.stambulo.milestone3.view.viewmodels.GalleryViewModel
import com.stambulo.milestone3.view.viewmodels.util.ViewModelFactory

private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

class GalleryFragment : BaseFragment<FragmentGalleryBinding>(FragmentGalleryBinding::inflate) {

    private lateinit var viewModel: GalleryViewModel

    override fun FragmentGalleryBinding.initialize() {
        checkPermissions()
        setupViewModel()
        binding.openAlbum.setOnClickListener { openMediaStore() }
        binding.grantPermissionButton.setOnClickListener { openMediaStore() }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(GalleryViewModel::class.java)
    }

    private fun checkPermissions() {
        if (!haveStoragePermission()) {
            binding.welcomeView.isVisible = true
        } else {
//            showImages()
        }
    }

    private fun showImages() {
        viewModel.loadImages()
        binding.welcomeView.isVisible = false
        binding.permissionRationaleView.isVisible = false
    }

    private fun showNoAccess() {
        binding.welcomeView.isVisible = false
        binding.permissionRationaleView.isVisible = true
    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
//            showImages()
        } else {
            requestPermission()
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(
                permissions,
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

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
//                        goToSettings()
                    }
                }
                return
            }
        }
    }

//    private fun goToSettings() {
//        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
//            addCategory(Intent.CATEGORY_DEFAULT)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }.also { intent ->
//            startActivity(intent)
//        }
//    }
}
