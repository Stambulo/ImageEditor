package com.stambulo.milestone3.presentation.image

interface IImageLoader<T> {
    fun loadInto(url: String, container: T)
}
