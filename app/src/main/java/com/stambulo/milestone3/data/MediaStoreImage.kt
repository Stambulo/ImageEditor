package com.stambulo.milestone3.data

import android.net.Uri
import java.util.*

data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri,
    val isChecked: Boolean = false
)
