package com.stambulo.milestone3.domain

import com.stambulo.milestone3.data.MediaStoreImage

interface IImageRepositoryImpl {
    suspend fun queryImages(): List<MediaStoreImage>
}
