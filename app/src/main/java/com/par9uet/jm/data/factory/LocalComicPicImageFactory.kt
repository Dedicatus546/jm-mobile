package com.par9uet.jm.data.factory

import android.net.Uri
import com.par9uet.jm.data.models.LocalComicPicImage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LocalComicPicImageFactory {
    fun create(
        @Assisted("comicId") comicId: Int,
        @Assisted("path") path: Uri,
    ): LocalComicPicImage
}