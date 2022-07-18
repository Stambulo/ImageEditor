package com.stambulo.milestone3.presentation.mvi

data class GalleryState(
    val type: Type = Type.IDLE,
    var imageName: String
){
    enum class Type {
        IDLE,
        NavigateToEditing}
}
