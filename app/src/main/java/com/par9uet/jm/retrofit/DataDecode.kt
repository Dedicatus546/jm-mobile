package com.par9uet.jm.retrofit

import com.par9uet.jm.utils.json
import com.par9uet.jm.utils.log
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun parseHtml(htmlStr: String): List<String> {
    // 正则表达式匹配 result 对象
    val resultRegex = Regex("""const result\s*=\s*(\{[\s\S]*?\});""")
    val resultMatch = resultRegex.find(htmlStr)
    val originPicList = mutableListOf<String>()

    if (resultMatch != null) {
        try {
            val resultJson = resultMatch.groupValues[1]
            // 需要手动修复 json 格式
            // {
            //         images: ['1.webp', '2.webp'],
            // }
            val o = json.decodeFromString<JsonObject>(
                resultJson
                    .replace("images", "\"images\"")
                    .replace(Regex("'([^']*)'"), "\"$1\""),
            )
            val list = o["images"]?.jsonArray
            if (list != null) {
                for (i in list.indices) {
                    list[i].let {
                        if (it.jsonPrimitive.jsonPrimitive.isString) {
                            originPicList.add(it.jsonPrimitive.content)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log("api", "Error parsing result object: ${e.stackTraceToString()}")
        }
    }

    // 正则表达式匹配 config 对象
    val configRegex = Regex("""const config\s*=\s*(\{[\s\S]*?\});""")
    val configMatch = configRegex.find(htmlStr)
    var imgHost: String? = null
    var jmId: String? = null
    var cache: String? = null

    if (configMatch != null) {
        try {
            val resultJson = configMatch.groupValues[1]
            // 需要手动修复 json 格式
            // {
            //         jmid: '1421327',
            //         imghost: 'https://cdn-msp3.jmapiproxy1.cc',
            //         cache: ''
            // }
            val o = json.decodeFromString<JsonObject>(
                resultJson
                    .replace("jmid", "\"jmid\"")
                    .replace("imghost", "\"imghost\"")
                    .replace("cache", "\"cache\"")
                    .replace(Regex("'([^']*)'"), "\"$1\"")
            )
            imgHost = o["imghost"]?.let {
                if (it.jsonPrimitive.isString) {
                    it.jsonPrimitive.content
                } else {
                    null
                }
            }
            jmId = o["jmid"]?.let {
                if (it.jsonPrimitive.isString) {
                    it.jsonPrimitive.content
                } else {
                    null
                }
            }
            cache = o["cache"]?.let {
                if (it.jsonPrimitive.isString) {
                    it.jsonPrimitive.content
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            log("api", "Error parsing config object: ${e.stackTraceToString()}")
        }
    }

    if (originPicList.isEmpty() || imgHost == null || jmId == null || cache == null) {
        log("api", "解析漫画 html 页失败")
        return listOf()
    }

    return originPicList.toList().map { item ->
        "$imgHost/media/photos/$jmId/$item$cache"
    }
}

fun parseRange(htmlStr: String): Pair<Int, Int> {
    var left = 0
    var right = 0
    val r1 = Regex("""var aid\s*=\s*(\d+);""")
    val rs1 = r1.find(htmlStr)
    if (rs1 != null) {
        try {
            val str = rs1.groupValues[1]
            left = str.toInt()
        } catch (e: Exception) {
            log("parse", "Error parse range, result object: ${e.stackTraceToString()}")
        }
    }

    val r2 = Regex("""var scramble_id\s*=\s*(\d+);""")
    val rs2 = r2.find(htmlStr)
    if (rs2 != null) {
        try {
            val str = rs2.groupValues[1]
            right = str.toInt()
        } catch (e: Exception) {
            log("parse", "Error parse range, result object: ${e.stackTraceToString()}")
        }
    }
    return left to right
}

fun parseSpeed(htmlStr: String): String {
    var speed = ""
    val r1 = Regex("""var speed\s*=\s*'(.*)';""")
    val rs1 = r1.find(htmlStr)
    if (rs1 != null) {
        try {
            speed = rs1.groupValues[1]
        } catch (e: Exception) {
            log("parse", "Error parse speed, result object: ${e.stackTraceToString()}")
        }
    }
    return speed
}

fun decryptData(str: String): String {
    val secretKey = SecretKeySpec(API_TOKEN_HASH.toByteArray(Charset.forName("UTF-8")), "AES")

    // 配置 Cipher
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    // 解密数据
    val encryptedBytes = android.util.Base64.decode(str, android.util.Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(encryptedBytes)

    return String(decryptedBytes, Charset.forName("UTF-8"))
}