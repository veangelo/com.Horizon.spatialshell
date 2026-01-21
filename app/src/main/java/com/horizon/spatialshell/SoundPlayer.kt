package com.horizon.spatialshell

import android.content.Context
import android.media.SoundPool

class SoundPlayer(context: Context) {

    private val soundPool = SoundPool.Builder().setMaxStreams(2).build()
    private val soundId: Int = soundPool.load(context, R.raw.ui_select, 1)

    fun play() {
        soundPool.play(soundId, 0.4f, 0.4f, 1, 0, 1f)
    }
}
