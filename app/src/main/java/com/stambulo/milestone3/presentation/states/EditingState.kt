package com.stambulo.milestone3.presentation.states

import android.net.Uri

data class EditingState(
    val type: Type = Type.IDLE,
    var imageName: Uri
){
    enum class Type {
        IDLE,
        Loading,
        ShowImage
    }
}
