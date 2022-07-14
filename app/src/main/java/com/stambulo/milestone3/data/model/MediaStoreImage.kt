package com.stambulo.milestone3.data.model

import android.net.Uri
import java.util.*

data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri,
    val viewType: ViewType = ViewType.ITEM,
    val isChecked: Boolean = false
)

enum class ViewType { ITEM, HEADER }
