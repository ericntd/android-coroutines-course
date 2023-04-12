package com.techyourchance.coroutines.exercises.exercise8

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class FetchAndCacheUsersUseCase(
    private val getUserEndpoint: GetUserEndpoint,
    private val usersDao: UsersDao
) {

    suspend fun fetchAndCacheUsers(userIds: List<String>, parentJob: Job? = null, context: CoroutineContext? = null) =
        withContext(Dispatchers.Default + CoroutineName("usecase withContext")) {
            printCoroutineScopeInfo()
//        try {
            for (userId in userIds) {
                launch(CoroutineName("my coroutine")) {
//                    try {
                    println("fetching and caching user $userId")
                    printCoroutineScopeInfo()
//                    parentJob?.let { printJobsHierarchy(userId, it) }
                    val user = getUserEndpoint.getUser(userId)
                    usersDao.upsertUserInfo(user)
//                    } catch (e: CancellationException) {
//                        println("fetch and cache cancelled")
//                    }
                }
            }
//            parentJob?.let { printJobsHierarchy("other", it) }
            context?.let { printJobsHierarchy(context) }
//        } catch (e: CancellationException) {
//            println("withContext cancelled")
//        }
        }

    private fun printJobsHierarchy(prefix: String = "", job: Job, nestLevel: Int = 0) {
        val indent = "    ".repeat(nestLevel)
        println("$prefix - $indent- ${job}")
        for (childJob in job.children) {
            printJobsHierarchy(prefix, childJob, nestLevel + 1)
        }
        if (nestLevel == 0) {
            println()
        }
    }

    private fun printJobsHierarchy(context: CoroutineContext, nestLevel: Int = 0) {
        val indent = "    ".repeat(nestLevel)
        println("$indent- $context")
        for (childJob in context.get(Job)!!.children) {
            printJobsHierarchy((childJob as CoroutineScope).coroutineContext, nestLevel + 1)
        }
        if (nestLevel == 0) {
            println()
        }
    }

    fun CoroutineScope.printCoroutineScopeInfo() {
        println()
        println("CoroutineScope: $this")
        println("CoroutineContext: ${this.coroutineContext}")
//        println("Job: ${this.coroutineContext[Job]}")
        println()
    }
}