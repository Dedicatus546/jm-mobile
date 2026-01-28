package com.par9uet.jm.store

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.database.dao.DownloadComicDao
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.worker.DownloadComicWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DownloadManager(
    private val context: Context,
    private val downloadComicDao: DownloadComicDao,
    private val scope: CoroutineScope,
    private val toastManager: ToastManager,
) {
    fun downloadComic(comic: Comic) {
        scope.launch(Dispatchers.IO) {
            downloadComicDao.insert(
                DownloadComic(
                    id = comic.id,
                    name = comic.name,
                    authorList = comic.authorList,
                    coverPath = "",
                    zipPath = "",
                    progress = 0f,
                    status = "pending",
                    createTime = System.currentTimeMillis()
                )
            )
            toastManager.showAsync("创建下载任务成功")
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 必须有网
                .build()
            val downloadRequest = OneTimeWorkRequestBuilder<DownloadComicWorker>()
                .setConstraints(constraints)
                .setInputData(
                    workDataOf(
                        "comicId" to comic.id
                    )
                ) // 传递参数
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS) // 重试策略
                .build()
            WorkManager.getInstance(context).enqueue(downloadRequest)
        }
    }
}