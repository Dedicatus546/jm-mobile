package com.par9uet.jm.data.factory

import com.par9uet.jm.data.models.OnlineComicPicImage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface OnlineComicPicImageFactory {
    fun create(
        @Assisted("comicId") comicId: Int,
        @Assisted("originSrc") originSrc: String,
        @Assisted("scrambleId") scrambleId: Int,
        @Assisted("speed") speed: String,
    ): OnlineComicPicImage
}