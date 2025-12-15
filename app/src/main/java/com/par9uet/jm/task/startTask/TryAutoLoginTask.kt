package com.par9uet.jm.task.startTask

import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.User
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.storage.SecureStorage
import com.par9uet.jm.task.Task
import com.par9uet.jm.task.TaskResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TryAutoLoginTask(
    private val userRepository: UserRepository,
    private val secureStorage: SecureStorage,
) : Task<User> {

    override suspend fun run(): TaskResult<User> {
        val isAutoLogin =
            secureStorage.get<Boolean>("autoLogin", object : TypeToken<Boolean>() {}.type) ?: false
        val username =
            secureStorage.get<String>("username", object : TypeToken<String>() {}.type) ?: ""
        val password =
            secureStorage.get<String>("password", object : TypeToken<String>() {}.type) ?: ""
        if (!isAutoLogin) {
            return TaskResult(isFailure = false)
        }
        return when (val data = withContext(Dispatchers.IO) {
            userRepository.login(username, password)
        }) {
            is NetWorkResult.Error -> {
                // 自动登录失败静默
                TaskResult(isFailure = true)
            }

            is NetWorkResult.Success<LoginResponse> -> {
                val user = data.data.toUser()
                TaskResult(isFailure = false, data = user)
            }
        }
    }
}