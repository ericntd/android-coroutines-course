package com.techyourchance.coroutines.demonstrations.basiccoroutines

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.techyourchance.coroutines.R
import com.techyourchance.coroutines.common.BaseFragment
import com.techyourchance.coroutines.common.ThreadInfoLogger
import com.techyourchance.coroutines.home.ScreenReachableFromHome
import kotlinx.coroutines.*

class BasicCoroutinesDemoFragment : BaseFragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override val screenTitle get() = ScreenReachableFromHome.BASIC_COROUTINES_DEMO.description

    private lateinit var btnStart: Button
    private lateinit var txtRemainingTime: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_loop_iterations_demo, container, false)

        txtRemainingTime = view.findViewById(R.id.txt_remaining_time)

        btnStart = view.findViewById(R.id.btn_start)
        btnStart.setOnClickListener {
            logThreadInfo("button callback")

            coroutineScope.launch {
                val iterationsCount = executeBenchmark() // do sth async in background and return result
                // Do something while waiting
                doOtherThingsInBackground()
                btnStart.isEnabled = false
                Toast.makeText(requireContext(), "something that needs to happen right away", Toast.LENGTH_SHORT).show()
                // Process the result from async work
                Toast.makeText(requireContext(), "$iterationsCount", Toast.LENGTH_SHORT).show()
                btnStart.isEnabled = true
            }

        }

        return view
    }

    private suspend fun doOtherThingsInBackground() {
        withContext(Dispatchers.IO) {
            var iterationsCount: Long = 0
            while (iterationsCount < 10) {
                logThreadInfo("other work")
                iterationsCount++
            }
        }
    }

    private suspend fun executeBenchmark(): Long {
        val benchmarkDurationSeconds = 5

        updateRemainingTime(benchmarkDurationSeconds)

        return withContext(Dispatchers.Default) {
            logThreadInfo("benchmark started")

            val stopTimeNano = System.nanoTime() + benchmarkDurationSeconds * 1_000_000_000L

            var iterationsCount: Long = 0
            while (System.nanoTime() < stopTimeNano) {
                iterationsCount++
            }

            logThreadInfo("benchmark completed")

            iterationsCount
        }
    }

    private fun updateRemainingTime(remainingTimeSeconds: Int) {
        logThreadInfo("updateRemainingTime: $remainingTimeSeconds seconds")

        if (remainingTimeSeconds > 0) {
            txtRemainingTime.text = "$remainingTimeSeconds seconds remaining"
            Handler(Looper.getMainLooper()).postDelayed({
                updateRemainingTime(remainingTimeSeconds - 1)
            }, 1000)
        } else {
            txtRemainingTime.text = "done!"
        }

    }

    private fun logThreadInfo(message: String) {
        ThreadInfoLogger.logThreadInfo(message)
    }

    companion object {
        fun newInstance(): Fragment {
            return BasicCoroutinesDemoFragment()
        }
    }
}