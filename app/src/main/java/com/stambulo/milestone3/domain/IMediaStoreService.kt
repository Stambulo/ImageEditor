package com.stambulo.milestone3.domain

import com.stambulo.milestone3.data.MediaStoreImage

interface IMediaStoreService {
    suspend fun queryImages(): List<MediaStoreImage>
}
