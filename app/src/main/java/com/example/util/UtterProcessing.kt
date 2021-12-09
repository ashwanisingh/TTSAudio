package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.content.ContextCompat
import jedi.functional.FunctionalPrimitives
import jedi.option.Option
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.LinkedHashMap

class UtterProcessing (context: Context) {
    val mContext: Context = context

    private val ttsItemPrep = LinkedHashMap<String, TTSPlaybackItem>()

    val ttsUtterParts = LinkedHashMap<Int, DataInfo>()
                get() = field

    var TAG:String = "UtterProcessing"

    fun splitUtterIntoFiles(utterStr:String, utterId:String) {
        val parts = TextUtil.splitOnPunctuation(utterStr)
        var offset = 0

        try {
            val ttsFolderOption = getTTSFolder()
            val ttsFolder = ttsFolderOption!!.unsafeGet()

            for ((index, stringPart) in parts.withIndex()) {
                val lastPart = index === parts.size - 1
                //Utterance ID doubles as the filename
                var fileName = ""

                try {
                    val pageFile = File(ttsFolder, createFileName(utterId, index))
                    fileName = pageFile.absolutePath
                    pageFile.createNewFile()
                    ttsUtterParts[index] = paramOfPartString(index, fileName, stringPart)
                } catch (io: IOException) {
                    io.message?.let { Log.i(TAG, it+"cannot create "+fileName) }
                    //showTTSFailed(message)
                    //mSpeechText = null
                    //sendUpdateMessage(null, TTSUtil.INIT_FAILED)
                }
            }

        } catch (e: Exception) {
            e.message?.let { Log.i(TAG, it) }
        }
    }

    fun paramOfPartString(index: Int, fileName:String, stringPart:String):DataInfo {
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = fileName
        val dataInfo = DataInfo(index, stringPart, params, fileName)
        return dataInfo
    }


    fun streamPartToDisk(fileName:String , part:String , offset:Int, totalLength:Int , endOfPage: Boolean ) {
        if (part.trim { it <= ' ' }.isNotEmpty() || endOfPage) {
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = fileName
            val item = TTSPlaybackItem(part, MediaPlayer(), totalLength, offset, endOfPage, fileName)
            ttsItemPrep[fileName] = item
        }
    }


    fun getTTSFolder(): Option<File>? {
        return FunctionalPrimitives.firstOption(
            Arrays.asList(
                *ContextCompat.getExternalCacheDirs(mContext)
            )
        )
    }

    private fun createFileName(articleId: String, index: Int): String? {
        return "tts_" + articleId + "_index_" + index + ".wav"
    }
}