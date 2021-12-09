package com.example.ttsaudio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.SpeechCallback
import com.example.TTSManager
import com.example.TTSOnlyCallback
import com.example.ttsaudio.databinding.FragmentFirstBinding
import com.example.util.DataInfo

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), TTSOnlyCallback,SpeechCallback {

    val TAG: String = "FirstFragment"


    var ttsManager: TTSManager ?= null

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonPause.visibility = View.GONE
        binding.buttonLoading.visibility = View.GONE
        binding.buttonPlay.visibility = View.VISIBLE

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonPlay.setOnClickListener{

            binding.buttonPause.visibility = View.GONE
            binding.buttonLoading.visibility = View.VISIBLE
            binding.buttonPlay.visibility = View.GONE

            ttsManager =  TTSManager
            ttsManager?.ttsOnlyCallback = this
            activity?.let { it1 -> ttsManager?.initTTS(it1) }
        }

        binding.buttonPause.setOnClickListener{
            ttsManager?.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        ttsManager?.ttsOnlyCallback = this
    }

    override fun onPause() {
        super.onPause()
        ttsManager?.ttsOnlyCallback = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initialised() {
        Log.i(TAG, "initialised()")
        play();
    }

    override fun error(error: String?) {
        Log.i(TAG, "error()")
        TODO("Not yet implemented")
    }

    override fun done(done: String?) {
        Log.i(TAG, "done()")
        activity?.runOnUiThread {
            binding.buttonPause.visibility = View.GONE
            binding.buttonLoading.visibility = View.GONE
            binding.buttonPlay.visibility = View.VISIBLE
        }

    }

    override fun start(start: String?) {
        Log.i(TAG, "start()")
        activity?.runOnUiThread {
            binding.buttonPause.visibility = View.VISIBLE
            binding.buttonLoading.visibility = View.GONE
            binding.buttonPlay.visibility = View.GONE
        }
    }

    override fun play() {
        Log.i(TAG, "play()")
        val utter = "Not yet implemented Not yet implemented Not yet implemented  "
        ttsManager?.play(utter, "123")
    }

    override fun isPlaying() {
        Log.i(TAG, "isPlaying()")
        TODO("Not yet implemented")
    }

    override fun pause() {
        Log.i(TAG, "pause()")
        activity?.runOnUiThread {
            binding.buttonPause.visibility = View.GONE
            binding.buttonLoading.visibility = View.GONE
            binding.buttonPlay.visibility = View.VISIBLE
        }

    }


    override fun currentPlaying(): DataInfo {
        Log.i(TAG, "currentPlaying()")
        TODO("Not yet implemented")
    }
}