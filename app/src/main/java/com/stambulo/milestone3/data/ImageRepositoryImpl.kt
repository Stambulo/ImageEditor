package com.stambulo.milestone3.data

import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.domain.IImageRepositoryImpl
import com.stambulo.milestone3.domain.IMediaStoreService

class ImageRepositoryImpl(private val service: IMediaStoreService) : IImageRepositoryImpl {
    override suspend fun queryImages(): List<MediaStoreImage> {
        return service.queryImages()
    }
}
