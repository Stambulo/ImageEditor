package com.stambulo.milestone3.view.viewmodels.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stambulo.milestone3.view.viewmodels.GalleryViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)){
            return GalleryViewModel() as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
