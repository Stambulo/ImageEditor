package com.stambulo.milestone3.presentation.states

data class EditingState(
    val type: Type = Type.IDLE,
    var imageName: String
){
    enum class Type {
        IDLE,
        Loading,
        ShowImage
    }
}
