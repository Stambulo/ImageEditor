package com.stambulo.milestone3.presentation.states

import android.net.Uri

data class GalleryState(
    val type: Type = Type.IDLE,
    var imageUri: Uri?
){
    enum class Type {
        IDLE,
        NavigateToEditing
    }
}
