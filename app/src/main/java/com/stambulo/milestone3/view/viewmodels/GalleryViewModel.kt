package com.stambulo.milestone3.view.viewmodels

import android.app.Application
import android.database.ContentObserver
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.milestone3.MediaStoreImage
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application): BaseViewModel(application) {

    private val _images = MutableLiveData<List<MediaStoreImage>>()
    val images: LiveData<List<MediaStoreImage>> get() = _images
    private var contentObserver: ContentObserver? = null

    fun loadImages() {
        Log.i(">>>", "ViewModel -> loadImages")
        viewModelScope.launch {
            val imageList = queryImages()
            _images.postValue(imageList)

            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadImages()
                }
            }
        }
    }
}
