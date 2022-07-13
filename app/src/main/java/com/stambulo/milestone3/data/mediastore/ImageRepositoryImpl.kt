package com.stambulo.milestone3.data.mediastore

import com.stambulo.milestone3.data.MediaStoreImage

class ImageRepositoryImpl(private val service: MediaStoreService){

    suspend fun queryImages(): List<MediaStoreImage> {
        return service.queryImages()
    }
}
