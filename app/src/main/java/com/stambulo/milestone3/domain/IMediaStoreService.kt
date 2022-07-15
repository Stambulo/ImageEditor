package com.stambulo.milestone3.domain

import android.content.Context
import android.graphics.Bitmap
import com.stambulo.milestone3.data.model.MediaStoreImage

interface IMediaStoreService {
    suspend fun queryImages(): List<MediaStoreImage>
    fun saveImage(bitmap: Bitmap, context: Context, folderName: String)
}
