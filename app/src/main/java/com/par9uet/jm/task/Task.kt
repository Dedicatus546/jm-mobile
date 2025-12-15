package com.par9uet.jm.task

data class TaskResult<out T>(
    val isFailure: Boolean = false,
    val data: T? = null,
)

interface Task<T> {
    suspend fun run(): TaskResult<T>
}