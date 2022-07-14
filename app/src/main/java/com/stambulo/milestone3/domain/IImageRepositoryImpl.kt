package com.stambulo.milestone3.domain

import com.stambulo.milestone3.data.model.MediaStoreImage

interface IImageRepositoryImpl {
    suspend fun queryImages(): List<MediaStoreImage>
}
