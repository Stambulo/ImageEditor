package com.stambulo.milestone3.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stambulo.milestone3.MediaStoreImage
import com.stambulo.milestone3.databinding.GalleryLayoutBinding

class GalleryAdapter(val onClick: (MediaStoreImage) -> Unit) :
    ListAdapter<MediaStoreImage, GalleryAdapter.ImageViewHolder>(MediaStoreImage.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = GalleryLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val mediaStoreImage = getItem(position)
        holder.bind(mediaStoreImage)

        Glide.with(holder.imageView)
            .load(mediaStoreImage.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }

    inner class ImageViewHolder(
        val binding: GalleryLayoutBinding, onClick: (MediaStoreImage) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.image

        fun bind(imageList: MediaStoreImage) {
            binding.galleryTextItem.text = imageList.displayName
        }
    }
}
