package com.stambulo.milestone3.presentation.util

import androidx.recyclerview.widget.DiffUtil
import com.stambulo.milestone3.data.MediaStoreImage

class DiffUtils : DiffUtil.ItemCallback<MediaStoreImage>() {
    override fun areItemsTheSame(
        oldItem: MediaStoreImage,
        newItem: MediaStoreImage
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: MediaStoreImage,
        newItem: MediaStoreImage
    ) = oldItem == newItem

    override fun getChangePayload(
        oldItem: MediaStoreImage,
        newItem: MediaStoreImage
    ): Any? {
        return if (oldItem.id != newItem.id) true else null
    }
}
