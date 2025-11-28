package com.par9uet.jm.retrofit.converter

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.par9uet.jm.retrofit.API_TOKEN_HASH
import com.par9uet.jm.retrofit.annotation.OriginData
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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

class ResponseConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val gsonConverter =
            GsonConverterFactory.create(Gson())
                .responseBodyConverter(type, annotations, retrofit)
        return Converter<ResponseBody, Any> { responseBody ->
            val body = responseBody.string()
            val hasOriginDataAnnotation = annotations.any { it is OriginData }
            Log.d("api", "有无 OriginData ：$hasOriginDataAnnotation")
            if (hasOriginDataAnnotation) {
                val json = JsonObject()
                json.addProperty("value", body)
                gsonConverter?.convert(json.toString().toResponseBody(responseBody.contentType()))
            } else {
                val json = Gson().fromJson(body, JsonObject::class.java)
                if (json["code"].asInt == 200 && json["data"] != null) {
                    val encryptedData = json["data"].asString
                    val decryptedData = decryptData(encryptedData)
                    val data = Gson().fromJson(decryptedData, JsonElement::class.java)
                    Log.d("api", "解密后数据：$data")
                    json.add("data", data)
                }
                gsonConverter?.convert(json.toString().toResponseBody(responseBody.contentType()))
            }
        }
    }
}