package com.par9uet.jm.controller

import com.par9uet.jm.utils.log
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicInteger

class DownloadConcurrencyController(
    initialLimit: Int
) {
    private val limit = AtomicInteger(initialLimit)
    private val availableSlots = Channel<Unit>(Channel.UNLIMITED)
    private val _activeCount = AtomicInteger(0)
    val activeCount: Int get() = _activeCount.get()

    init {
        repeat(initialLimit) {
            availableSlots.trySend(Unit)
        }
    }

    fun setLimit(newLimit: Int) {
        val oldLimit = limit.getAndSet(newLimit)
        when {
            newLimit > oldLimit -> {
                repeat(newLimit - oldLimit) {
                    availableSlots.trySend(Unit)
                }
            }

            newLimit < oldLimit -> {
                // TODO
                val toRemove = oldLimit - newLimit
            }
        }
    }

    suspend fun acquire() {
        log("尝试获取下载令牌")
        availableSlots.receive()
        log("成功获取下载令牌")
        _activeCount.incrementAndGet()
    }

    fun release() {
        _activeCount.decrementAndGet()
        log("释放下载令牌")
        if (_activeCount.get() < limit.get()) {
            log("当前活跃的下载令牌小于限制数量 $${limit.get()} ，补充下载令牌到管道中")
            availableSlots.trySend(Unit)
        }
    }
}