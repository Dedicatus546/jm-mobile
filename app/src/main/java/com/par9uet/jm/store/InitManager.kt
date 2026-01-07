package com.par9uet.jm.store

import kotlinx.coroutines.CompletableDeferred

class InitManager {
    val deferred = CompletableDeferred<String>()
}