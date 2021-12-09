package com.example

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

object TTSManager  {

    private var textToSpeech: TextToSpeech ?= null
    private var mediaPlayer:MediaPlayer ?= null
    var currentWavFile : String ?= null
    var currentWavIndex = 0
    var isEnabledSynthesize = false

    private var MEDIA_TAG = "MEDIA_TAG"
    private var TTS_TAG = "TTS_TAG"

    /**
     * TTS Callback
     */
    var ttsOnlyCallback: TTSOnlyCallback ?= null
        get() = field
        set(value) {field = value}

    /**
     * Speech Callback
     */
    var speechCallback: SpeechCallback ?= null
                get() = field
                set(value) {field = value}

    /**
     * Audio Callback
     */
    var audioCallbacks: AudioCallback ?= null
        get() = field
        set(value) {field = value}

    /**
     * Initialing Text To Speech Object
     */
    fun initTTS(context: Context) {
        if (textToSpeech == null) {
            TextToSpeech(context, TextToSpeech.OnInitListener { status ->
                if(status != TextToSpeech.ERROR) {
                    ttsProgressListener();
                    textToSpeech?.setLanguage(Locale.UK)
                    ttsOnlyCallback?.initialised()

                } else {
                    ttsOnlyCallback?.error(null)
                }
            }).also { textToSpeech = it }
        } else {
            ttsOnlyCallback?.initialised()
            ttsProgressListener();
        }
    }

    /**
     * Utterance Progress Listener
     */
    fun ttsProgressListener() {
        textToSpeech?.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                ttsOnlyCallback?.start(utteranceId)
            }

            override fun onDone(utteranceId: String?) {
                ttsOnlyCallback?.done(utteranceId)
            }

            override fun onError(utteranceId: String?) {
                ttsOnlyCallback?.error(utteranceId)
            }
        })
    }


    fun pauseAudio() {
        mediaPlayer?.pause();
    }

    fun resumeAudio() {
        mediaPlayer?.start();
    }

    fun isAudioPause(): Boolean? {
        return mediaPlayer?.isPlaying() == false && mediaPlayer!!.getCurrentPosition() > 1
    }

    fun resetMediaPlayer() {
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null

    }

    fun playAudio(currentWavFile: String) {
        if(currentWavFile == null) {
            return
        }
        this.currentWavFile = currentWavFile
            if (mediaPlayer == null) {
                Log.i(MEDIA_TAG, "MP Initialised ")
                mediaPlayer = MediaPlayer()
                MPListener(mediaPlayer!!)
            }
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(currentWavFile)
            mediaPlayer?.prepareAsync()
    }


    fun MPListener(mediaPlayer:MediaPlayer) {
        mediaPlayer.setOnPreparedListener {
            Log.i(MEDIA_TAG, "OnPrepared => ")
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            Log.i(MEDIA_TAG, "OnCompletion => ")
            audioCallbacks?.utterNext()
        }
        mediaPlayer.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                Log.i(MEDIA_TAG, "OnInfo => "+"what :: "+what)
                Log.i(MEDIA_TAG, "OnInfo => "+"extra :: "+extra)
                return true
            }

        })

        mediaPlayer.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(paramMediaPlayer: MediaPlayer?, paramInt1: Int, paramInt2: Int): Boolean {
                Log.i(MEDIA_TAG, "OnError => "+"paramInt1 :: "+paramInt1)
                Log.i(MEDIA_TAG, "OnError => "+"paramInt2 :: "+paramInt2)
                return true
            }
        })
    }

    fun speakSynthesize(waveIndex: Int, stringPart:String, params:HashMap<String, String>, fileName:String) {
        isEnabledSynthesize = true
        currentWavIndex = waveIndex;
        currentWavFile = fileName;
        textToSpeech?.synthesizeToFile(stringPart, params, fileName);

    }


    fun release() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    fun play(utter: String, id: String ?= null) {
        textToSpeech?.speak(utter, TextToSpeech.QUEUE_FLUSH, null,id)
    }

    fun stop() {
        textToSpeech?.stop()
    }


}