package com.stambulo.milestone3.presentation.image

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ImageLoader: IImageLoader<ImageView> {

    override fun loadInto(uri: Uri, container: ImageView) {
        Glide.with(container.context)
            .asBitmap()
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(container)
    }
}
