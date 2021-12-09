package ru.darek.nmedia.dto

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.darek.nmedia.api.PostsApiService

data class PushToken(
    val token: String,
)
fun getPushTokenAndSend(
    postsApiService: PostsApiService
) { // Всегда сперва запросим токен у FCM и отправим на сервак
    CoroutineScope(Dispatchers.Default).launch {
        try {
            val pushToken = PushToken(Firebase.messaging.token.await())
            postsApiService.save(pushToken)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
/*
fun getPushTokenAndSend() { // Всегда сперва запросим токен у FCM и отправим на сервак
    CoroutineScope(Dispatchers.Default).launch {
        try {
            val pushToken = PushToken(Firebase.messaging.token.await())
            PostsApi.retrofitService.save(pushToken)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} */
