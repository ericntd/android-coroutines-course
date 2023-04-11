package com.techyourchance.coroutines.exercises.exercise7

import com.techyourchance.coroutines.common.TestUtils
import com.techyourchance.coroutines.common.TestUtils.printCoroutineScopeInfo
import com.techyourchance.coroutines.common.TestUtils.printJobsHierarchy
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.Exception
import kotlin.coroutines.EmptyCoroutineContext

class Exercise7Test {

    /*
    Write nested withContext blocks, explore the resulting Job's hierarchy, test cancellation
    of the outer scope
     */
    @Test
    fun nestedWithContext() {
        runBlocking {
            val scopeJob = Job()
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            val job = scope.launch {
                withContext(CoroutineName("withContext")) {
                    try {
                        withContext(CoroutineName("inner withContext")) {
                            try {
                                printJobsHierarchy(scopeJob)
                                delay(110)
                                println("inner withContext done")
                            } catch (e: CancellationException) {
                                println("inner withContext cancelled")
                            }
                        }
                        println("with context done")
                    } catch (e: CancellationException) {
                        println("withContext cancelled")
                    }
                }
                println("outer scope done")
            }

//            scope.printCoroutineScopeInfo()
            scope.launch {
//                printCoroutineScopeInfo()
                delay(100)
                // scopeJob.cancel()
                scope.cancel()
            }

            job.join()
            println("test done")
        }
    }

    /*
    Launch new coroutine inside another coroutine, explore the resulting Job's hierarchy, test cancellation
    of the outer scope, explore structured concurrency
     */
    @Test
    fun nestedLaunchBuilders() {
        runBlocking {
            val scopeJob = Job()
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            val job = scope.launch {
                withContext(CoroutineName("withContext")) {
                    try {
                        launch(CoroutineName("nested coroutine") + Dispatchers.Default) {
                            try {
//                                printJobsHierarchy(coroutineContext[Job]!!)
                                printJobsHierarchy(scopeJob)
                                delay(80)
                                println("nested coroutine done")
                            } catch (e: CancellationException) {
                                println("nested coroutine cancelled")
                            }
                        }
                        println("with context done")
                    } catch (e: CancellationException) {
                        println("withContext cancelled")
                    }
                }
                println("outer scope done")
            }

//            scope.printCoroutineScopeInfo()
            scope.launch {
//                printCoroutineScopeInfo()
                delay(100)
                scopeJob.cancel()
            }

            job.join()
            println("test done")
        }
    }

    /*
    Launch new coroutine on "outer scope" inside another coroutine, explore the resulting Job's hierarchy,
    test cancellation of the outer scope, explore structured concurrency
     */
    @Test
    fun nestedCoroutineInOuterScope() {
        runBlocking {
            val scopeJob = Job()
            val scope = CoroutineScope(scopeJob + CoroutineName("outer scope") + Dispatchers.IO)

            val job = scope.launch {
                withContext(CoroutineName("withContext")) {
                    try {
                        /*
                        Bypassing/ breaking structured concurrency here
                         */
//                        launch(scopeJob + CoroutineName("nested coroutine") + Dispatchers.Default) {
                        scope.launch( CoroutineName("nested coroutine") + Dispatchers.Default) {
                            try {
                                printJobsHierarchy(coroutineContext[Job]!!)
                                printJobsHierarchy(scopeJob)
                                delay(90)
                                println("inner withContext done")
                            } catch (e: CancellationException) {
                                println("inner withContext cancelled")
                            }
                        }
//                        job.join()
                        delay(100)
                        println("with context done")
                    } catch (e: CancellationException) {
                        println("withContext cancelled")
                    }
                }
                println("outer scope done")
            }

//            scope.printCoroutineScopeInfo()
            scope.launch {
//                printCoroutineScopeInfo()
                delay(100)
                scopeJob.cancel()
            }

            job.join()
            println("test done")
        }
    }
}