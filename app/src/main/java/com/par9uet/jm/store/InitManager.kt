package com.par9uet.jm.store

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CompletableDeferred

@Singleton
class InitManager @Inject constructor() {
    val deferred = CompletableDeferred<String>()
}