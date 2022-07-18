package com.stambulo.milestone3.presentation.mvi

sealed class GalleryIntent {
    data class GoToEditing(val imageName: String): GalleryIntent()
}
