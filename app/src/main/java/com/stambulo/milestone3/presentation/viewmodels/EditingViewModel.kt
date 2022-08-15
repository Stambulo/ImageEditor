package com.stambulo.milestone3.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.stambulo.milestone3.presentation.intents.EditingIntent
import com.stambulo.milestone3.presentation.states.EditingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditingViewModel @Inject constructor() : BaseViewModel<EditingIntent>() {

    private val _editingState = MutableStateFlow(EditingState(EditingState.Type.IDLE, Uri.EMPTY))
    val editingState: StateFlow<EditingState> get() = _editingState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is EditingIntent.ShowImage -> {
                        showImage(it.imageName)
                    }
                }
            }
        }
    }

    private fun showImage(imageName: Uri) {
        _editingState.value = EditingState(EditingState.Type.Loading, Uri.EMPTY)
        _editingState.value = EditingState(EditingState.Type.ShowImage, imageName)
    }
}
