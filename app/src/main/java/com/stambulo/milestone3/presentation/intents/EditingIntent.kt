package com.stambulo.milestone3.presentation.intents

import android.net.Uri

sealed class EditingIntent {
    data class ShowImage(val imageName: Uri): EditingIntent()
}
