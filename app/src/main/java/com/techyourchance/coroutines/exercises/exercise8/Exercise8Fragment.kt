package com.techyourchance.coroutines.exercises.exercise8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.techyourchance.coroutines.R
import com.techyourchance.coroutines.common.BaseFragment
import com.techyourchance.coroutines.common.ThreadInfoLogger.logThreadInfo
import com.techyourchance.coroutines.home.ScreenReachableFromHome
import kotlinx.coroutines.*

class Exercise8Fragment : BaseFragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private val specialJob = Job()

    override val screenTitle get() = ScreenReachableFromHome.EXERCISE_8.description

    private lateinit var fetchAndCacheUsersUseCase: FetchAndCacheUsersUseCase

    private lateinit var btnFetch: Button
    private lateinit var txtElapsedTime: TextView

    private val userIds = listOf<String>("bmq81", "gfn12", "gla34")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchAndCacheUsersUseCase = compositionRoot.fetchAndCacheUserUseCase
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_8, container, false)

        view.apply {
            txtElapsedTime = findViewById(R.id.txt_elapsed_time)
            btnFetch = findViewById(R.id.btn_fetch_users)
        }

        btnFetch.setOnClickListener {
            logThreadInfo("button callback")

            val updateElapsedTimeJob = coroutineScope.launch {
                updateElapsedTime()
            }

            coroutineScope.launch(specialJob) {
                try {
                    this.printCoroutineScopeInfo()
                    btnFetch.isEnabled = false
                    fetchAndCacheUsersUseCase.fetchAndCacheUsers(userIds, context = coroutineContext)
                    updateElapsedTimeJob.cancel()
                } catch (e: CancellationException) {
                    e.printStackTrace()
                    updateElapsedTimeJob.cancelAndJoin()
                    txtElapsedTime.text = ""
                } finally {
                    btnFetch.isEnabled = true
                }
            }
        }

        return view
    }

    fun CoroutineScope.printCoroutineScopeInfo() {
        println()
        println("CoroutineScope: $this")
        println("CoroutineContext: ${this.coroutineContext}")
        println("Job: ${this.coroutineContext[Job]}")
        println()
    }

    override fun onStop() {
        logThreadInfo("onStop()")
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
        specialJob.cancel()
    }


    private suspend fun updateElapsedTime() {
        val startTimeNano = System.nanoTime()
        while (true) {
            delay(100)
            val elapsedTimeNano = System.nanoTime() - startTimeNano
            val elapsedTimeMs = elapsedTimeNano / 1000000
            txtElapsedTime.text = "Elapsed time: $elapsedTimeMs ms"
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return Exercise8Fragment()
        }
    }
}