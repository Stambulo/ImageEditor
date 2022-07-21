package com.stambulo.milestone3.presentation.intents

sealed class EditingIntent {
    data class ShowImage(val imageName: String): EditingIntent()
}
