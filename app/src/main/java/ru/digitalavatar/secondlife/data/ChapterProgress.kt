package ru.digitalavatar.secondlife.data

import android.content.Context

object ChapterProgress {
    private const val PREFS = "kotik"
    private const val KEY = "last_chapter"
    const val TOTAL = 100 // по числу роликов — подставьте своё значение

    /**
     * Возвращает номер последней главы пользователя.
     * 0 = игра ещё не начиналась.
     */
    fun lastChapter(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY, 0)

    fun save(context: Context, chapter: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY, chapter).apply()
    }
}
