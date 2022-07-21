package com.stambulo.milestone3.presentation.viewmodels

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

    private val _editingState = MutableStateFlow(EditingState(EditingState.Type.IDLE, ""))
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

    private fun showImage(imageName: String) {
        _editingState.value = EditingState(EditingState.Type.Loading, "")
        _editingState.value = EditingState(EditingState.Type.ShowImage, imageName)
    }
}
