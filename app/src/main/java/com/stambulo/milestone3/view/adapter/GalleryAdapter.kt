package com.stambulo.milestone3.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stambulo.milestone3.MediaStoreImage
import com.stambulo.milestone3.databinding.GalleryLayoutBinding

class GalleryAdapter(val onClick: (MediaStoreImage) -> Unit) :
//    ListAdapter<MediaStoreImage, GalleryAdapter.ImageViewHolder>(MediaStoreImage.DiffCallback) {
RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        Log.i(">>>", "Adapter -> onCreateViewHolder")
        val view = GalleryLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.i(">>>", "Adapter -> onBindViewHolder - $position")
//        val mediaStoreImage = getItem(position)
//        holder.bind(getItem(position), position)
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 12
    }

    inner class ImageViewHolder(
        val binding: GalleryLayoutBinding, onClick: (MediaStoreImage) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.galleryTextItem.text = position.toString()
            Log.i(">>>", "bind - pos: $position")
        }
    }


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater.inflate(R.layout.gallery_layout, parent, false)
//        val view = GalleryLayoutBinding.inflate(layoutInflater, parent, false)
//        return ImageViewHolder(view, onClick)
//    }
//
//    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//        val mediaStoreImage = getItem(position)
//        holder.rootView.tag = mediaStoreImage
//
//        Glide.with(holder.imageView)
//            .load(mediaStoreImage.contentUri)
//            .thumbnail(0.33f)
//            .centerCrop()
//            .into(holder.imageView)
//    }
//
//    class ImageViewHolder(itemView: View, onClick: (MediaStoreImage) -> Unit):
//        RecyclerView.ViewHolder(itemView) {
//
//        val rootView = itemView
//        val imageView: ImageView = itemView.findViewById(R.id.image)
//
//        init {
//            imageView.setOnClickListener {
//                val image = rootView.tag as? MediaStoreImage ?: return@setOnClickListener
//                onClick(image)
//            }
//        }
//    }
}
