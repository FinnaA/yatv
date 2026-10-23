package ru.digitalavatar.secondlife

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import ru.digitalavatar.secondlife.data.ChapterProgress

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lastChapter = ChapterProgress.lastChapter(this)
        val btnContinue = findViewById<Button>(R.id.btn_continue)
        val btnStart = findViewById<Button>(R.id.btn_start)

        if (lastChapter == 0) {
            // игра не начиналась — кнопки «Продолжить» нет
            btnContinue.visibility = View.GONE
            btnStart.text = "Начать игру"
            btnStart.contentDescription = "Начать игру с начала, с первой главы"
        } else {
            btnContinue.visibility = View.VISIBLE
            btnContinue.text = "Продолжить (глава $lastChapter)"
            btnContinue.contentDescription = "Продолжить игру с последней главы"
            btnStart.text = "Начать заново"
            btnStart.contentDescription = "Начать игру заново, с начала, с первой главы"
        }

        btnContinue.setOnClickListener {
            openChapter(lastChapter.coerceAtLeast(1))
        }
        btnStart.setOnClickListener {
            ChapterProgress.save(this, 1)
            openChapter(1)
        }

        // фокус при запуске — на первую доступную кнопку
        if (btnContinue.visibility == View.VISIBLE) {
            btnContinue.requestFocus()
        } else {
            btnStart.requestFocus()
        }
    }

    private fun openChapter(n: Int) {
        startActivity(
            Intent(this, VideoPlayerActivity::class.java)
                .putExtra("chapter", n)
        )
    }
}
