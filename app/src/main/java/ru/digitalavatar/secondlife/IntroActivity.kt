package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import ru.digitalavatar.secondlife.data.VideoCatalog

@OptIn(UnstableApi::class)
class IntroActivity : Activity() {

    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(DefaultHttpDataSource.Factory())
            )
            .build()
        // медиа-сессия до старта воспроизведения — для голосовых «пауза/продолжи»
        mediaSession = MediaSession.Builder(this, player).build()
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

    // OK / Enter / Back — пропуск интро; остальные клавиши (кнопка Алисы,
    // громкость, медиа-кнопки) отдаём системе, иначе голос и пульт «глохнут»
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_BACK -> {
                goToMain()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }

    override fun onStop() {
        super.onStop()
        mediaSession?.release()
        mediaSession = null
        player.release()
    }
}
