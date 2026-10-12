package com.par9uet.jm.store

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Singleton
class ToastManager @Inject constructor(
    private val coroutineScope: CoroutineScope
) {
    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun show(text: String) {
        coroutineScope.launch {
            _message.emit(text)
        }
    }
}