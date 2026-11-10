package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import ru.digitalavatar.secondlife.data.VideoCatalog

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFeed = findViewById<Button>(R.id.btn_feed)
        val btnCall = findViewById<Button>(R.id.btn_call)

        btnFeed.setOnClickListener {
            openVideo(VideoCatalog.feed)
        }
        btnCall.setOnClickListener {
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
