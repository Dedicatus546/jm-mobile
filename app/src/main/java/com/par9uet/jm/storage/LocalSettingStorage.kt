package com.par9uet.jm.storage

import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.LocalSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalSettingStorage(
    private val secureStorage: SecureStorage
) {
    companion object {
        private const val STORAGE_KEY = "localSetting"
    }

    private var _state = MutableStateFlow<LocalSetting?>(null)
    val state = _state.asStateFlow()

    fun set(localSetting: LocalSetting) {
        _state.update {
            localSetting
        }
        secureStorage.set(STORAGE_KEY, this.state.value)
    }

    fun get(): LocalSetting {
        if (_state.value == null) {
            _state.update {
                secureStorage.get(STORAGE_KEY, object : TypeToken<LocalSetting>() {}.type)
                    ?: LocalSetting()
            }
        }
        return _state.value!!
    }

    fun remove() {
        _state.update {
            LocalSetting()
        }
        secureStorage.remove(STORAGE_KEY)
    }
}