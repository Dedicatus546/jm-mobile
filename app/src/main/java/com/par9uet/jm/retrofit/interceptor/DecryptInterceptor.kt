package com.par9uet.jm.retrofit.interceptor;

import com.par9uet.jm.retrofit.decryptData
import com.par9uet.jm.utils.json
import jakarta.inject.Inject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlin.collections.set

class DecryptInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val originalBody = response.body

        val contentType = originalBody.contentType()
        if (contentType == null ||
            contentType.type != "application" ||
            contentType.subtype != "json"
        ) {
            return response
        }

        val jsonObject = json.decodeFromString<JsonObject>(response.body.string())
        val code = jsonObject["code"]?.jsonPrimitive?.int ?: 0
        val data = jsonObject["data"]
        return if (code == 200 && data != null) {
            val encryptedData = data.jsonPrimitive.content
            val decryptedData = decryptData(encryptedData)
            val resultJsonObject = jsonObject.toMutableMap().apply {
                this["data"] = json.decodeFromString<JsonElement>(decryptedData)
            }
            val newBody = json.encodeToString(JsonObject(resultJsonObject))
            val newResponseBody = response.newBuilder()
                .body(newBody.toResponseBody(response.body.contentType()))
                .build()
            newResponseBody
        } else {
            response
        }
    }
}
