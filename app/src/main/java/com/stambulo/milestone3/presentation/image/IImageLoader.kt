package com.stambulo.milestone3.presentation.image

import android.net.Uri

interface IImageLoader<T> {
    fun loadInto(uri: Uri, container: T)
}
