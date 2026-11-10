package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.digitalavatar.secondlife.data.StatsApi
import ru.digitalavatar.secondlife.data.VideoCatalog

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFeed = findViewById<Button>(R.id.btn_feed)
        val btnCall = findViewById<Button>(R.id.btn_call)

        btnFeed.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                StatsApi.send(this@MainActivity, "click_feed", VideoCatalog.feed)
            }
            openVideo(VideoCatalog.feed)
        }
        btnCall.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                StatsApi.send(this@MainActivity, "click_call", VideoCatalog.call)
            }
            openVideo(VideoCatalog.call)
        }

        btnFeed.requestFocus()
    }

    private fun openVideo(url: String) {
        startActivity(
            Intent(this, VideoPlayerActivity::class.java)
                .putExtra(VideoPlayerActivity.EXTRA_URL, url)
        )
    }
}
