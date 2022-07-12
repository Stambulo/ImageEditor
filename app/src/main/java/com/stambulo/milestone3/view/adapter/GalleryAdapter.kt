package com.stambulo.milestone3.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.data.ViewType
import com.stambulo.milestone3.databinding.DelimiterGalleryItemBinding
import com.stambulo.milestone3.databinding.GalleryItemBinding
import java.text.DateFormat
import java.util.*

class GalleryAdapter() :
    PagingDataAdapter<MediaStoreImage, RecyclerView.ViewHolder>(
        MediaStoreCallBack()
    ) {
    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

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
            }
            VIEW_TYPE_HEADER -> {
                val mediaStoreImage = getItem(position)
                val h = holder as DelimiterViewHolder
                mediaStoreImage?.id?.let { h.bind(it, mediaStoreImage?.dateAdded) }
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
                    super.onBindViewHolder(holder, position, payloads)
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
        init {
            imageView.setOnClickListener {
                binding.checkEnabled.isVisible = !binding.checkEnabled.isVisible
            }
        }
        fun bind(imageList: MediaStoreImage) {
            binding.checkEnabled.isVisible = imageList.isChecked
        }
        fun bindState(isChecked: Boolean) { binding.checkEnabled.isVisible = isChecked }
    }

    inner class DelimiterViewHolder(private val binding: DelimiterGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val data = binding.delimiterData
        val number = binding.delimiterNumber

        fun bind(delimiterData: Long, date: Date?){
            data.text = DateFormat.getDateInstance().format(date)
            number.text = delimiterData.toString()
        }
    }

    class MediaStoreCallBack : DiffUtil.ItemCallback<MediaStoreImage>() {
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
}
