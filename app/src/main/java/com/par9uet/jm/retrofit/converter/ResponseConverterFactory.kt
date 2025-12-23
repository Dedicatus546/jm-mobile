package com.par9uet.jm.retrofit.converter

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.retrofit.decryptData
import com.par9uet.jm.retrofit.model.ResponseWrapper
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ResponseConverterFactory(
    private val gson: Gson = Gson()
) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val elementAdapter = Gson().getAdapter(TypeToken.get(type))
        return Converter<ResponseBody, Any> { responseBody ->
            val body = responseBody.string()
            val json = gson.fromJson(body, JsonObject::class.java)
            val code = json["code"].asInt
            if (code == 200 && json["data"] != null) {
                val encryptedData = json["data"].asString
                val decryptedData = decryptData(encryptedData)
                val data = gson.fromJson(decryptedData, JsonElement::class.java)
                json.add("data", data);
//                Log.d("ResponseBodyConverter", "解密后数据：$data")
                val result = elementAdapter.fromJsonTree(json)
                return@Converter result
            }
            val msg = json["errorMsg"]?.asString ?: "接口未返回错误"
            return@Converter ResponseWrapper.Error(
                code = code,
                errorMsg = msg
            )
        }
    }
}