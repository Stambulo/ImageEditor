package com.stambulo.milestone3.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.stambulo.milestone3.data.ImageRepositoryImpl
import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.presentation.adapter.GalleryPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    val repository: ImageRepositoryImpl
) : ViewModel() {

    val imagesWithPaging3: Flow<PagingData<MediaStoreImage>> = Pager(PagingConfig(pageSize = 300)) {
        GalleryPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}
