package com.par9uet.jm.retrofit.converter

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class PrimitiveToRequestBodyConverterFactory : Converter.Factory() {
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return when (type) {
            Int::class.java -> Converter<Int, RequestBody> { value ->
                value.toString().toRequestBody("text/plain".toMediaType())
            }

            Long::class.java -> Converter<Long, RequestBody> { value ->
                value.toString().toRequestBody("text/plain".toMediaType())
            }

            Float::class.java -> Converter<Float, RequestBody> { value ->
                value.toString().toRequestBody("text/plain".toMediaType())
            }

            Double::class.java -> Converter<Double, RequestBody> { value ->
                value.toString().toRequestBody("text/plain".toMediaType())
            }

            Boolean::class.java -> Converter<Boolean, RequestBody> { value ->
                value.toString().toRequestBody("text/plain".toMediaType())
            }

            String::class.java -> Converter<String, RequestBody> { value ->
                value.toRequestBody("text/plain".toMediaType())
            }

            else -> null
        }
    }
}