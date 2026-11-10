package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.PlayerView
import ru.digitalavatar.secondlife.data.VideoCatalog

class IntroActivity : Activity() {

    private lateinit var player: ExoPlayer
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(DefaultHttpDataSource.Factory())
            )
            .build()
        findViewById<PlayerView>(R.id.player_view).player = player

        player.setMediaItem(MediaItem.fromUri(VideoCatalog.intro))
        player.prepare()
        player.play()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    goToMain()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    private fun goToMain() {
        if (finished) return
        finished = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // любой ключ пульта — пропуск интро
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        goToMain()
        return true
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
}
