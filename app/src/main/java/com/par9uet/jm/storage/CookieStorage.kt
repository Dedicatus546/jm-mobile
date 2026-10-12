package com.par9uet.jm.storage

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import okhttp3.Cookie

// 由于 okhttp3.Cookie 无法加上 @Serializable ，序列化有问题，这里使用自定义的类来维持序列化
@Serializable
private data class SerializableCookie(
    val name: String,
    val value: String,
    val domain: String,
    val path: String,
    val expiresAt: Long,
    val secure: Boolean,
    val httpOnly: Boolean,
    val persistent: Boolean,
    val hostOnly: Boolean
)

private fun Cookie.toSerializable(): SerializableCookie {
    return SerializableCookie(
        name = this.name,
        value = this.value,
        domain = this.domain,
        path = this.path,
        expiresAt = this.expiresAt,
        secure = this.secure,
        httpOnly = this.httpOnly,
        persistent = this.persistent,
        hostOnly = this.hostOnly
    )
}

private fun SerializableCookie.toOkHttpCookie(): Cookie {
    return Cookie.Builder()
        .name(this.name)
        .value(this.value)
        .domain(this.domain)
        .path(this.path)
        .expiresAt(this.expiresAt)
        .let {
            if (this.secure) {
                it.secure()
            } else {
                it
            }
        }.let {
            if (this.httpOnly) {
                it.httpOnly()
            } else {
                it
            }
        }
        .hostOnlyDomain(this.domain)
        .build()
}

@Singleton
class CookieStorage @Inject constructor(
    private val secureStorage: SecureStorage
) {
    companion object {
        private const val STORAGE_KEY = "cookie"
    }

    private var _state = MutableStateFlow<List<Cookie>?>(null)
    val state = _state.asStateFlow()

    fun set(cookieList: List<Cookie>) {
        _state.update {
            cookieList
        }
        secureStorage.set(STORAGE_KEY, cookieList.map { it.toSerializable() })
    }

    fun get(): List<Cookie> {
        if (_state.value == null) {
            val serializableCookieList = secureStorage.get<List<SerializableCookie>>(STORAGE_KEY)
                ?: listOf()
            _state.update {
                serializableCookieList.map { it.toOkHttpCookie() }
            }
        }
        return _state.value!!
    }

    fun remove() {
        _state.update {
            listOf()
        }
        secureStorage.remove(STORAGE_KEY)
    }
}