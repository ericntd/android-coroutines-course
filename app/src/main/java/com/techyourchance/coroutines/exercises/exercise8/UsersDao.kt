package com.techyourchance.coroutines.exercises.exercise8

import kotlinx.coroutines.*

class UsersDao {

    suspend fun upsertUserInfo(user: User) = withContext(Dispatchers.IO + CoroutineName("db")) {
//        try {
//            println("upsertUserInfo ${user.name}")
            delay(1000)
//        } catch (e: CancellationException) {
//            println("upsert cancelled")
//        }
    }
}