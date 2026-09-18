package com.par9uet.jm.data.factory

import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.data.models.Downloader
import dagger.assisted.AssistedFactory

@AssistedFactory
interface DownloaderFactory {
    fun create(comic: Comic, comicChapter: ComicChapter): Downloader
}