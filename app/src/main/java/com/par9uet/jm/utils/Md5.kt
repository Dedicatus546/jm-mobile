package com.par9uet.jm.utils

import java.io.File
import java.io.RandomAccessFile
import java.security.MessageDigest

fun md5(str: String): String {
    return MessageDigest.getInstance("MD5").digest(str.toByteArray())
        .joinToString("") { "%02x".format(it) }.lowercase()
}

// 文件部分散列
fun md5(file: File): String {
    val fileSize = file.length()
    val threshold = 1024 * 1024 // 1MB
    val chunkSize = 300 * 1024 // 300KB

    return try {
        MessageDigest.getInstance("MD5").run {
            if (fileSize <= threshold) {
                // 全量计算
                file.inputStream().use { inputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        update(buffer, 0, bytesRead)
                    }
                }
            } else {
                // 采样计算：开头 + 中间 + 结尾
                RandomAccessFile(file, "r").use { raf ->
                    val buffer = ByteArray(chunkSize)
                    // 读取开头 300KB
                    raf.readFully(buffer)
                    update(buffer)
                    // 读取中间 300KB
                    val midStart = (fileSize - chunkSize) / 2
                    raf.seek(midStart)
                    raf.readFully(buffer)
                    update(buffer)
                    // 读取结尾 300KB
                    val endStart = fileSize - chunkSize
                    raf.seek(endStart)
                    raf.readFully(buffer)
                    update(buffer)
                }
            }
            digest().joinToString("") { "%02x".format(it) }
        }
    } catch (e: Throwable) {
        log("md5", "文件 ${file.name} MD5 失败，${e.stackTraceToString()}")
        ""
    }
}