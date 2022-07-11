package com.stambulo.milestone3.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stambulo.milestone3.BuildConfig
import com.stambulo.milestone3.R
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.databinding.DelimiterGalleryItemBinding
import com.stambulo.milestone3.databinding.FragmentGalleryBinding
import com.stambulo.milestone3.databinding.GalleryItemBinding
import com.stambulo.milestone3.view.adapter.GalleryAdapter
import com.stambulo.milestone3.view.viewmodels.GalleryViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

class GalleryFragment : BaseFragment<FragmentGalleryBinding>(FragmentGalleryBinding::inflate) {
    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1
    private val viewModel: GalleryViewModel by viewModels()
    private val galleryAdapter by lazy { GalleryAdapter { image -> deleteImage(image) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        setViews()
    }

    private fun setViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imagesWithPaging3.collectLatest {
                galleryAdapter.submitData(it)
            }
        }
    }

    private fun setViews() {
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
        binding.welcomeView.isVisible = false
        binding.permissionRationaleView.isVisible = false
        binding.recyclerView.isVisible = true
        binding.fab.isVisible = true
    }

    private fun showNoAccess() {
        binding.welcomeView.isVisible = false
        binding.permissionRationaleView.isVisible = true
    }

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
            startActivity(intent)
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            requestCameraPermission()
            return true
        } else {
            // no camera on this device
            return false
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

    private fun deleteImage(image: MediaStoreImage) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_message, image.displayName))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                viewModel.deleteImage(image)
            }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(">>>", "Granted")
            takePhoto()
        } else {
            Log.i(">>>", "Denied")
        }
    }

    private fun takePhoto() {
        Log.i(">>>", "takePhoto()")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoResultLaunch.launch(intent)
    }

    var photoResultLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(">>>", "Activity Result - $result")
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as Bitmap
                saveImage(bitmap, requireContext(), "Milestone3")
            } else {
                /** some error to be shown here */
            }
        }

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(
                Environment.getExternalStorageDirectory().toString() + File.separator + folderName
            )
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
