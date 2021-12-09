package com.example.ttsaudio

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.AudioCallback
import com.example.TTSManager
import com.example.TTSOnlyCallback
import com.example.ttsaudio.databinding.FragmentSecondBinding
import com.example.util.Constants
import com.example.util.UtterProcessing

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), TTSOnlyCallback, AudioCallback{

    val TAG: String = "SecondFragment"

    private var utterProcessing:UtterProcessing ?= null

    private var _binding:FragmentSecondBinding? = null

    var ttsManager: TTSManager?= null

    var utterFileSize = 0
    var utterNextIndex = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPause.visibility = View.GONE
        binding.buttonLoading.visibility = View.GONE
        binding.buttonPlay.visibility = View.VISIBLE

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.buttonPlay.setOnClickListener{
            if(ttsManager?.isAudioPause() == true) {
                showPauseUI()
                ttsManager?.resumeAudio()
            } else {
                showLoadingUI()
                ttsManager = TTSManager
                ttsManager?.ttsOnlyCallback = this
                ttsManager?.audioCallbacks = this
                activity?.let { it1 -> ttsManager?.initTTS(it1) }
            }
        }

        binding.buttonPause.setOnClickListener{
            ttsManager?.pauseAudio()
            showPlayUI()
        }

    }

    fun showPauseUI() {
        activity?.runOnUiThread {
            binding.buttonPlay.visibility = View.GONE
            binding.buttonPause.visibility = View.VISIBLE
            binding.buttonLoading.visibility = View.GONE
        }
    }

    fun showPlayUI() {
        activity?.runOnUiThread {
            binding.buttonPlay.visibility = View.VISIBLE
            binding.buttonPause.visibility = View.GONE
            binding.buttonLoading.visibility = View.GONE
        }
    }

    fun showLoadingUI() {
        activity?.runOnUiThread {
            binding.buttonPlay.visibility = View.GONE
            binding.buttonPause.visibility = View.GONE
            binding.buttonLoading.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        ttsManager?.audioCallbacks = this
    }

    override fun onPause() {
        super.onPause()
        ttsManager?.stop()
        ttsManager?.audioCallbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initialised() {
        utterProcessing = activity?.let { it->  UtterProcessing(it) }
        utterProcessing?.splitUtterIntoFiles(Constants.ttsText1, Constants.utterID_1)
        utterFileSize = utterProcessing?.ttsUtterParts?.size!!

        val ttsUtterParts = utterProcessing?.ttsUtterParts

        if (ttsUtterParts != null) {
            for((key, value) in ttsUtterParts) {
                ttsManager?.speakSynthesize(value.index,value.stringPart, value.params, value.fileName)
            }
        }

        Handler().postDelayed(object:Runnable {
            override fun run() {
                play(utterNextIndex, Constants.utterID_1)
            }
        }, 15000)

    }

    override fun play(index:Int, utterId: String) {
        Log.i(TAG, "play() "+utterId)
        if(utterNextIndex < utterFileSize) {
            showPauseUI()
            utterProcessing?.ttsUtterParts?.get(index)?.let { it->it.fileName }
                ?.let { it1 -> ttsManager?.playAudio(it1) }
            utterNextIndex++
        } else {
            ttsManager?.resetMediaPlayer()
            utterNextIndex = 0;
            showPlayUI()
        }

    }

    override fun utterNext() {
        Log.i(TAG, "utterNext() ")
        play(utterNextIndex!!,Constants.utterID_1)
    }

    override fun start(start: String?) {
        Log.i(TAG, "start() "+start)
    }

    override fun done(done: String?) {
        Log.i(TAG, "done() "+done)
    }

    override fun pause(index:Int, utterId: String) {
        Log.i(TAG, "pause() "+utterId)
    }

    override fun stop(index:Int, utterId: String) {
        Log.i(TAG, "stop() "+utterId)
    }

    override fun error(error: String?) {
        Log.i(TAG, "error() "+error)
    }

    override fun error(index:Int, utterId: String, error: String) {
        Log.i(TAG, "error() "+utterId + " :: error "+error)
    }




}