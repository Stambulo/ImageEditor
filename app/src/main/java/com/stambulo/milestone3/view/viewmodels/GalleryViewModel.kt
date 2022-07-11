package com.stambulo.milestone3.view.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.data.repository.ImageRepositoryImpl
import com.stambulo.milestone3.view.adapter.GalleryPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application): BaseViewModel(application) {

    private val repository = ImageRepositoryImpl(application)

    val imagesWithPaging3: Flow<PagingData<MediaStoreImage>> = Pager(PagingConfig(pageSize = 20)){
        GalleryPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun deleteImage(image: MediaStoreImage) {
        viewModelScope.launch {
            performDeleteImage(image)
        }
    }
}
