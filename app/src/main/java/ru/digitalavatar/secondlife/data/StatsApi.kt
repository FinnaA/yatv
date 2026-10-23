package ru.digitalavatar.secondlife.data

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

object StatsApi {
    // URL после деплоя функции
    private const val BASE_URL = "https://functions.yandexcloud.net/xxxxx"

    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    // session_id живёт в SharedPreferences — без логинов и профилей
    fun sessionId(context: Context): String {
        val prefs = context.getSharedPreferences("kotik", Context.MODE_PRIVATE)
        return prefs.getString("session_id", null)
            ?: UUID.randomUUID().toString().also {
                prefs.edit().putString("session_id", it).apply()
            }
    }

    fun sendChoice(context: Context, step: String, choice: String) {
        val body = JSONObject()
            .put("session_id", sessionId(context))
            .put("step", step)
            .put("choice", choice)
            .toString()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(body.toRequestBody(jsonType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // демо: тихо игнорируем, игре это не мешает
            }
            override fun onResponse(call: Call, response: okhttp3.Response) {
                response.close()
            }
        })
    }

    fun fetchStats(onResult: (List<Pair<String, Int>>) -> Unit) {
        val request = Request.Builder().url(BASE_URL).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onResult(emptyList())
            override fun onResponse(call: Call, response: okhttp3.Response) {
                val rows = try {
                    val json = JSONObject(response.body!!.string())
                    val arr = json.getJSONArray("stats")
                    (0 until arr.length()).map { i ->
                        val r = arr.getJSONObject(i)
                        // человекочитаемая подпись: "Покормили" / "Позвали"
                        val label = if (r.getString("choice") == "feed") "Покормили" else "Позвали"
                        "${r.getString("step")}: $label" to r.getInt("count")
                    }
                } catch (e: Exception) {
                    emptyList()
                } finally {
                    response.close()
                }
                onResult(rows)
            }
        })
    }
}
