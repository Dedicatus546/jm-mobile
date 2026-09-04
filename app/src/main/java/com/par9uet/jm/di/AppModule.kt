package com.par9uet.jm.di

import android.util.Log
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.store.HistorySearchManager
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.store.RemoteSettingManager
import com.par9uet.jm.store.UserManager
import com.par9uet.jm.task.AppInitTask
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Default +
                    CoroutineExceptionHandler { _, throwable ->
                        // TODO fix 日志
                        Log.e("GlobalCoroutine", "全局协程捕获到了异常: $throwable")
                    }
        )
    }

    @Provides
    @Singleton
    fun provideAppInitTaskList(
        historySearchManager: HistorySearchManager,
        localSettingManager: LocalSettingManager,
        remoteSettingManager: RemoteSettingManager,
        userManager: UserManager,
        retrofit: Retrofit,
        // 加上注解 @JvmSuppressWildcards 确保 kotlin 不生成 List<? extends AppInitTask> 而是 List<AppInitTask>
        // 不然注入会失败
    ): List<@JvmSuppressWildcards AppInitTask> {
        return listOf(
            historySearchManager,
            localSettingManager,
            remoteSettingManager,
            userManager,
            retrofit
        )
    }
}

