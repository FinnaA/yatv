package ru.digitalavatar.secondlife

import android.app.Activity
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.PlayerView
import ru.digitalavatar.secondlife.data.VideoCatalog
import java.io.File

class VideoPlayerActivity : Activity() {

    companion object {
        const val EXTRA_URL = "url"
    }

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val url = intent.getStringExtra(EXTRA_URL)
            ?: VideoCatalog.intro

        player = buildPlayer()
        findViewById<PlayerView>(R.id.player_view).player = player
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    finish()
                }
            }
        })
        player.play()
    }

    private fun buildPlayer(): ExoPlayer {
        val cache = SimpleCache(
            File(cacheDir, "video"),
            LeastRecentlyUsedCacheEvictor(50L * 1024 * 1024)
        )
        val sourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())

        return ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(sourceFactory))
            .build()
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
}
