package com.par9uet.jm.di

import com.par9uet.jm.coil.createAsyncImageLoader
import com.par9uet.jm.coil.createPicImageLoader
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coilModule = module {
    single(named("AsyncImageLoader")) {
        createAsyncImageLoader(get())
    }
    single(named("PicImageLoader")) {
        createPicImageLoader(get())
    }
}