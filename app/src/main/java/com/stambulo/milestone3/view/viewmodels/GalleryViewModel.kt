package com.stambulo.milestone3.view.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.data.repository.ImageRepositoryImpl
import com.stambulo.milestone3.view.adapter.GalleryPagingSource
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(application: Application): BaseViewModel(application) {

    private val repository = ImageRepositoryImpl(application)

    val imagesWithPaging3: Flow<PagingData<MediaStoreImage>> = Pager(PagingConfig(pageSize = 20)){
        GalleryPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}
