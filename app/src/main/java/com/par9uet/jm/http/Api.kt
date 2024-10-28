package com.par9uet.jm.http

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.ApiResponse
import com.par9uet.jm.data.models.CollectComicListResponseData
import com.par9uet.jm.data.models.ComicDetailResponseData
import com.par9uet.jm.data.models.ComicListItemResponseData
import com.par9uet.jm.data.models.ComicListResponseData
import com.par9uet.jm.data.models.HistoryComicListResponseData
import com.par9uet.jm.data.models.LoginResponseData
import com.par9uet.jm.data.models.PromoteComicListItemResponseData
import com.par9uet.jm.data.models.SettingResponseData
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun resolveUrl(path: String): String {
    return "https://www.jmapiproxyxxx.vip/$path";
}

val ts = System.currentTimeMillis() / 1000;
val version = "1.7.4"
val token = "185Hcomic3PAPP7R"
val tokenHash =
    MessageDigest.getInstance("md5").digest("$ts$token".toByteArray()).joinToString("") {
        "%02x".format(it)
    }
val commonHeaders: Headers
    get() = Headers.Builder()
        .set("token", tokenHash)
        .set("tokenparam", "$ts,$token")
        .set("cookie", loginCookieList.joinToString(";"))
        .build()
val loginCookieList = arrayListOf<String>()

fun decode(data: String): String {
    val encryptedBytes = Base64.getDecoder().decode(data)
    val keyBytes = tokenHash.toByteArray(Charsets.UTF_8)
    val secretKey = SecretKeySpec(keyBytes, "AES")

    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val decryptedBytes = cipher.doFinal(encryptedBytes)
    return String(decryptedBytes, Charsets.UTF_8)
}

@Suppress("UNCHECKED_CAST")
fun <T> request(
    method: String,
    path: String,
    query: Map<String, String> = mapOf(),
    body: Map<String, String> = mapOf(),
    type: Type,
): ApiResponse<T> {
    val client = OkHttpClient()
    val reqBuilder = resolveUrl(path).toHttpUrl().newBuilder()
    query.forEach { (key, value) ->
        reqBuilder.addQueryParameter(key, value)
    }
    val req = Request.Builder().headers(commonHeaders).url(reqBuilder.build()).let {
        val formBodyBuilder = FormBody.Builder()
        body.forEach { (key, value) ->
            formBodyBuilder.add(key, value)
        }
        if (method == "post") {
            it.post(formBodyBuilder.build()).build()
        } else {
            it.get().build()
        }
    }
    val response = client.newCall(req).execute()
    val resStr = response.body?.string() ?: throw Exception("没有结果数据")
    if (path == "chapter_view_template") {
        return ApiResponse(200, null, resStr) as ApiResponse<T>
    }
    println("method=$method path=$path query=$query body=$body resStr=$resStr")
    val json = JsonParser.parseString(resStr).asJsonObject
    val code = json.get("code").asInt
    if (code != 200) {
        val errorMsg = json.get("errorMsg").asString
        throw Exception("request error, reason: $errorMsg")
    }
    val encodeType = object : TypeToken<ApiResponse<String>>() {}.type
    val res: ApiResponse<String> = Gson().fromJson(resStr, encodeType)
    if (method == "post" && path == "login") {
        loginCookieList.clear()
        val setCookies = response.headers("Set-Cookie")
        for (cookie in setCookies) {
            Cookie.parse(reqBuilder.build(), cookie)
                ?.let { loginCookieList.add("${it.name}=${it.value}") }
        }
    }
    val decodeStr = decode(res.data)
    val decodeObject = Gson().fromJson<T>(decodeStr, type);
    return ApiResponse(res.code, res.errorMsg, decodeObject)
}

fun <T> requestGet(
    path: String,
    query: Map<String, String> = mapOf(),
    type: Type,
): ApiResponse<T> {
    return request(
        method = "get",
        path = path,
        query = query,
        type = type
    )
}

fun <T> requestPost(
    path: String,
    body: Map<String, String> = mapOf(),
    type: Type,
): ApiResponse<T> {
    return request(
        method = "post",
        path = path,
        body = body,
        type = type
    )
}

suspend fun getSettingApi(): ApiResponse<SettingResponseData> {
    return requestGet(
        "setting",
        mapOf(),
        object : TypeToken<SettingResponseData>() {}.type
    )
}

suspend fun loginApi(username: String, password: String): ApiResponse<LoginResponseData> {
    return requestPost(
        "login",
        mapOf("username" to username, "password" to password),
        object : TypeToken<LoginResponseData>() {}.type
    )
}

suspend fun getComicListApi(
    page: Int,
    order: String,
    content: String
): ApiResponse<ComicListResponseData> {
    return requestGet(
        "search",
        mapOf("page" to page.toString(), "order" to order, "content" to content),
        object : TypeToken<ComicListResponseData>() {}.type
    )
}

suspend fun getCollectComicListApi(
    page: Int,
    order: String,
): ApiResponse<CollectComicListResponseData> {
    return requestGet(
        "favorite",
        mapOf("page" to page.toString(), "folder_id" to "0", "o" to order),
        object : TypeToken<CollectComicListResponseData>() {}.type
    )
}

suspend fun getHistoryComicListApi(
    page: Int,
    order: String,
): ApiResponse<HistoryComicListResponseData> {
    return requestGet(
        "watch_list",
        mapOf("page" to page.toString(), "folder_id" to "0", "o" to order),
        object : TypeToken<HistoryComicListResponseData>() {}.type
    )
}

suspend fun getPromoteComicListApi(): ApiResponse<List<PromoteComicListItemResponseData>> {
    return requestGet(
        path = "promote",
        type = object : TypeToken<List<PromoteComicListItemResponseData>>() {}.type
    )
}

suspend fun getLatestComicListApi(
    page: Int,
): ApiResponse<List<ComicListItemResponseData>> {
    return requestGet(
        path = "latest",
        query = mapOf("page" to page.toString()),
        type = object : TypeToken<List<ComicListItemResponseData>>() {}.type
    )
}

suspend fun getComicDetailApi(
    id: Int,
): ApiResponse<ComicDetailResponseData> {
    return requestGet(
        "album",
        mapOf("id" to id.toString()),
        object : TypeToken<ComicDetailResponseData>() {}.type
    )
}

suspend fun getComicPicListApi(
    id: Int,
    shuntKey: Int? = null
): ApiResponse<List<String>> {
    val res: ApiResponse<String> = requestGet(
        "chapter_view_template",
        mapOf(
            "id" to id.toString(),
            "mode" to "vertical",
            "app_img_shunt" to (shuntKey?.toString() ?: ""),
            "express" to "off",
            "v" to System.currentTimeMillis().toString(),
        ),
        object : TypeToken<String>() {}.type
    )
    val html = res.data
    val regex = Regex("""data-original="(.*)"""")
    val matches = regex.findAll(html)
        .map { it.groupValues[1] }
        .toList()
    val list = matches.filter { it.contains(".webp") }
    return ApiResponse(200, null, list)
}

fun main() {
    runBlocking {
        launch {
//            println("ts = $ts")
//            println("tokenHash = $tokenHash")
//            val r1 = loginApi("", "")
//            println("res = $r1")
//            val r2 = getHistoryComicListApi(1, "mr")
//            println("res = $r2")

            val res = getComicPicListApi(637279)
            println("res = $res")
        }
    }
}