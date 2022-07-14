package com.stambulo.milestone3.domain

import com.stambulo.milestone3.data.model.MediaStoreImage

interface IMediaStoreService {
    suspend fun queryImages(): List<MediaStoreImage>
}
