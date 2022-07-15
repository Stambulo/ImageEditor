package com.stambulo.milestone3.data

import android.content.Context
import android.graphics.Bitmap
import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.domain.IImageRepositoryImpl
import com.stambulo.milestone3.domain.IMediaStoreService

class ImageRepositoryImpl(private val service: IMediaStoreService) : IImageRepositoryImpl {

    override suspend fun queryImages(): List<MediaStoreImage> {
        return service.queryImages()
    }

    override fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        service.saveImage(bitmap, context, folderName)
    }
}
