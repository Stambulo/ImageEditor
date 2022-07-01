package com.stambulo.milestone3.repository

import com.stambulo.milestone3.data.MediaStoreImage

interface IMediaStoreRepository {

    suspend fun queryImages(): List<MediaStoreImage>
}
