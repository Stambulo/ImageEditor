package com.stambulo.milestone3.view.viewmodels.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stambulo.milestone3.view.viewmodels.GalleryViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)){
            return GalleryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
