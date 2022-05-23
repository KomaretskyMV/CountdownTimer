package com.kmv.countdowntimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.kmv.countdowntimer.databinding.FragmentTimerBinding
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val INSTANCE_STATE = "instance state"

/**
 * A simple [Fragment] subclass.
 * Use the [TimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var currentProgress: Int = 0
    private var maxProgress: Int = 10
    private var isTimerRun = false
        get() = timerStart.isActive
    private val timerScope = CoroutineScope(Dispatchers.Main)
    private var timerStart = timerScope.launch { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            currentProgress = it.getInt(INSTANCE_STATE)
            maxProgress = it.getInt(INSTANCE_STATE)
            isTimerRun = it.getBoolean(INSTANCE_STATE)
        }

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater)
        return binding.root
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentProgress = savedInstanceState!!.getInt(INSTANCE_STATE)
        maxProgress = savedInstanceState.getInt(INSTANCE_STATE)
    }*/

    override fun onStart() {
        super.onStart()

        setProgress()

        binding.startButton.setOnClickListener {
            startTimer()
        }

        binding.stopButton.setOnClickListener {
            stopTimer()
            timerStart.cancel()
        }

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            maxProgress = binding.slider.value.toInt()
            binding.progressCircular.max = maxProgress
            setProgress()
        }

        binding.themeButton.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INSTANCE_STATE, currentProgress)
        outState.putInt(INSTANCE_STATE, maxProgress)
        outState.putBoolean(INSTANCE_STATE, isTimerRun)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startTimer() {
        binding.startButton.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.slider.isEnabled = false
        currentProgress = binding.slider.value.toInt()

        timerStart = timerScope.launch {
            for (i in currentProgress - 1 downTo 0) {
                delay(1000)
                binding.progressCircular.progress--
                binding.timerText.text = i.toString()
            }
            stopTimer()
        }
    }

    private fun stopTimer() {
        binding.stopButton.visibility = View.INVISIBLE
        binding.startButton.visibility = View.VISIBLE
        binding.slider.isEnabled = true
        binding.progressCircular.progress = maxProgress
        binding.timerText.text = maxProgress.toString()
        timerStart.cancel()
        Toast.makeText(activity,"Timer Task Finished", Toast.LENGTH_SHORT).show()
    }

    private fun setProgress() {
        binding.progressCircular.progress = maxProgress
        binding.timerText.text = maxProgress.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}