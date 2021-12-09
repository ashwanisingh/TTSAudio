package com.example

import com.example.util.DataInfo

interface SpeechCallback {
    fun isPlaying()
    fun play()
    fun pause()
    fun currentPlaying(): DataInfo

}