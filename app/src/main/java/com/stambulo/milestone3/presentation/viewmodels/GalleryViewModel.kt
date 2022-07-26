package com.stambulo.milestone3.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.presentation.adapter.GalleryPagingSource
import com.stambulo.milestone3.presentation.intents.GalleryIntent
import com.stambulo.milestone3.presentation.states.GalleryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : BaseViewModel<GalleryIntent>() {

    private val _galleryState = MutableStateFlow(GalleryState(GalleryState.Type.IDLE, null))
    val galleryState: StateFlow<GalleryState> get() = _galleryState

    init {
        handleIntent()
    }

    val imagesWithPaging3: Flow<PagingData<MediaStoreImage>> = Pager(PagingConfig(pageSize = 300)) {
        GalleryPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is GalleryIntent.GoToEditing -> {
                        navigateToEditing(it.imageName)
                    }
                }
            }
        }
    }

    private fun navigateToEditing(imageUri: Uri?) {
        _galleryState.value = GalleryState(
            GalleryState.Type.NavigateToEditing,
            imageUri = imageUri
        )
    }
}
