package com.par9uet.jm.di

import com.par9uet.jm.controller.DownloadConcurrencyController
import org.koin.dsl.module

val downloadModule = module {
    single {
        DownloadConcurrencyController(3)
    }
}