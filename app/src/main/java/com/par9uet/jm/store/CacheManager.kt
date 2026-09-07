package com.par9uet.jm.store

import android.content.Context
import com.par9uet.jm.dir.getComicCoverCacheDir
import com.par9uet.jm.dir.getComicPicCacheDir
import com.par9uet.jm.dir.getComicPicDecodeCacheDir
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

@Singleton
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val _comicCoverCacheSize = MutableStateFlow(0L)
    val comicCoverCacheSize = _comicCoverCacheSize.asStateFlow()

    fun getComicCoverCacheSize() {
        val coverCacheDir = getComicCoverCacheDir(context)
        _comicCoverCacheSize.update {
            getDirFileListLength(coverCacheDir)
        }
    }

    private val _comicPicCacheSize = MutableStateFlow(0L)
    val comicPicCacheSize = _comicPicCacheSize.asStateFlow()

    fun getComicPicCacheSize() {
        val coverCacheDir = getComicPicCacheDir(context)
        _comicPicCacheSize.update {
            getDirFileListLength(coverCacheDir)
        }
    }

    private val _comicPicDecodeCacheSize = MutableStateFlow(0L)
    val comicPicDecodeCacheSize = _comicPicDecodeCacheSize.asStateFlow()

    fun getComicPicDecodeCacheSize() {
        val coverCacheDir = getComicPicDecodeCacheDir(context)
        _comicPicDecodeCacheSize.update {
            getDirFileListLength(coverCacheDir)
        }
    }

    private fun getDirFileListLength(dir: File): Long {
        val files = dir.listFiles() ?: return 0L

        return files.sumOf { file ->
            if (file.isDirectory) {
                getDirFileListLength(file)
            } else {
                file.length()
            }
        }
    }
}