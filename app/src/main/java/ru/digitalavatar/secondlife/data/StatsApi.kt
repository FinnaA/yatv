package ru.digitalavatar.secondlife.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

object StatsApi {
    private const val ENDPOINT = "https://functions.yandexcloud.net/d4eh48825hlu66j59ijj"

    private val client = OkHttpClient()
    private val json = "application/json; charset=utf-8".toMediaType()

    fun deviceId(ctx: Context): String {
        val prefs = ctx.getSharedPreferences("kotik", Context.MODE_PRIVATE)
        return prefs.getString("device_id", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("device_id", it).apply()
        }
    }

    suspend fun send(ctx: Context, event: String, video: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = """
                    {"device_id":"${deviceId(ctx)}","event":"$event","video":"$video"}
                """.trimIndent().toRequestBody(json)
                val request = Request.Builder().url(ENDPOINT).post(body).build()
                client.newCall(request).execute().use { it.code }
            }.getOrDefault(0)
        }
}
