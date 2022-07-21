package com.stambulo.milestone3.presentation.intents

import android.net.Uri

sealed class GalleryIntent {
    data class GoToEditing(val imageName: Uri?): GalleryIntent()
}
