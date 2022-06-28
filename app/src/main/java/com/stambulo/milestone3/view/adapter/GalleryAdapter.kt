package com.stambulo.milestone3.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.databinding.GalleryLayoutBinding

class GalleryAdapter(val onClick: (MediaStoreImage) -> Unit) :
    ListAdapter<MediaStoreImage, GalleryAdapter.ImageViewHolder>(MediaStoreCallBack()) {

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

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads[0] == true){
                holder.bindState(getItem(position).isChecked)
            }
        }
    }

    inner class ImageViewHolder(
        val binding: GalleryLayoutBinding, onClick: (MediaStoreImage) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.image

        init {
            imageView.setOnClickListener {
                binding.checkEnabled.isVisible = !binding.checkEnabled.isVisible
            }
        }

        fun bind(imageList: MediaStoreImage) {
            binding.checkEnabled.isVisible = imageList.isChecked
        }

        fun bindState(isChecked: Boolean) {
            binding.checkEnabled.isVisible = isChecked
        }
    }

    class MediaStoreCallBack : DiffUtil.ItemCallback<MediaStoreImage>() {
        override fun areItemsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage) =
            oldItem == newItem
        override fun getChangePayload(oldItem: MediaStoreImage, newItem: MediaStoreImage): Any? {
            return if (oldItem.id != newItem.id) true else null
        }
    }
}
