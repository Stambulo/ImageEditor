package com.stambulo.milestone3.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.stambulo.milestone3.data.ImageRepositoryImpl
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

abstract class BaseViewModel<T> : ViewModel(){

    @Inject
    lateinit var repository: ImageRepositoryImpl
    val intent = Channel<T>(Channel.UNLIMITED)
}
