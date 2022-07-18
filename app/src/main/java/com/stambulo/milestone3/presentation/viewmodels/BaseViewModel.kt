package com.stambulo.milestone3.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

abstract class BaseViewModel<T> : ViewModel(){
    val intent = Channel<T>(Channel.UNLIMITED)
}
