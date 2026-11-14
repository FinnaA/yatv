package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.digitalavatar.secondlife.data.StatsApi
import ru.digitalavatar.secondlife.data.VideoCatalog
import java.io.File

@OptIn(UnstableApi::class)
class VideoPlayerActivity : Activity() {

    companion object {
        const val EXTRA_URL = "url"

        @Volatile
        private var cache: SimpleCache? = null

        private fun getCache(context: Context): SimpleCache =
            cache ?: synchronized(this) {
                cache ?: SimpleCache(
                    File(context.cacheDir, "video"),
                    LeastRecentlyUsedCacheEvictor(50L * 1024 * 1024),
                    StandaloneDatabaseProvider(context)
                ).also { cache = it }
            }
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
                    CoroutineScope(Dispatchers.IO).launch {
                        StatsApi.send(this@VideoPlayerActivity, "video_end", url)
                    }
                    finish()
                }
            }
        })
        player.play()
    }

    private fun buildPlayer(): ExoPlayer {
        val sourceFactory = CacheDataSource.Factory()
            .setCache(getCache(this))
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
