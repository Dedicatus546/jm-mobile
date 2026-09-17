package com.par9uet.jm.ui.screens.readScreen

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ComicPicImageFactory {
    fun create(
        @Assisted("comicId") comicId: Int,
        @Assisted("originSrc") originSrc: String,
        @Assisted("scrambleId") scrambleId: Int,
        @Assisted("speed") speed: String,
    ): ComicPicImage
}