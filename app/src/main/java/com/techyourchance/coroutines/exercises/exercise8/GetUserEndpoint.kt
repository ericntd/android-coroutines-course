package com.techyourchance.coroutines.exercises.exercise8

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class GetUserEndpoint {

    suspend fun getUser(userId: String): User = withContext(Dispatchers.IO + CoroutineName("network request")) {
//        println("getUser $userId")
        delay(1000)
        return@withContext User(userId, "user ${userId}")
    }
}