package com.stambulo.milestone3.presentation.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.data.model.ViewType
import com.stambulo.milestone3.databinding.DelimiterGalleryItemBinding
import com.stambulo.milestone3.databinding.GalleryItemBinding
import com.stambulo.milestone3.presentation.util.DiffUtils
import java.text.DateFormat
import java.util.*

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1

class GalleryAdapter(
    private var itemClickListener: OnImageClickListener
) : PagingDataAdapter<MediaStoreImage, RecyclerView.ViewHolder>(DiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding =
                GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding)
        } else {
            val binding = DelimiterGalleryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            DelimiterViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_ITEM -> {
                val mediaStoreImage = getItem(position)
                val h = holder as ImageViewHolder

                Glide.with(h.imageView)
                    .load(mediaStoreImage?.contentUri)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(h.imageView)

                h.bind(mediaStoreImage)
            }
            VIEW_TYPE_HEADER -> {
                val mediaStoreImage = getItem(position)
                val h = holder as DelimiterViewHolder
                mediaStoreImage?.id?.let { h.bind(it, mediaStoreImage.dateAdded) }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (holder.itemViewType) {
            VIEW_TYPE_ITEM -> {
                val h = holder as ImageViewHolder
                if (payloads.isEmpty()) {
                    super.onBindViewHolder(holder, position, payloads)
                } else {
                    if (payloads[0] == true) {
                        getItem(position)?.let { h.bindState(it.isChecked) }
                    }
                }
            }
            VIEW_TYPE_HEADER -> {
                val h = holder as DelimiterViewHolder
                if (payloads.isEmpty()) {
                    super.onBindViewHolder(h, position, payloads)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.viewType == ViewType.HEADER) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    inner class ImageViewHolder(
        private val binding: GalleryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.image

        fun bindState(isChecked: Boolean) {
            binding.checkEnabled.isVisible = isChecked
        }

        fun bind(mediaStoreImage: MediaStoreImage?) {
            imageView.setOnClickListener {
                mediaStoreImage?.contentUri.let { uri -> itemClickListener.onItemClick(uri) }
            }
        }
    }

    inner class DelimiterViewHolder(binding: DelimiterGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val data = binding.delimiterData
        private val number = binding.delimiterNumber

        fun bind(delimiterNumber: Long, date: Date?) {
            data.text = date?.let { DateFormat.getDateInstance().format(it) }
            number.text = delimiterNumber.toString()
        }
    }

    interface OnImageClickListener {
        fun onItemClick(imageUri: Uri?)
    }
}
