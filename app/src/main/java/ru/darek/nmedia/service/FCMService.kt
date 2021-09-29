package ru.darek.nmedia.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.darek.nmedia.R
import ru.darek.nmedia.dto.Post
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

   override fun onMessageReceived(message: RemoteMessage) {

       message.data[action]?.let {
           val name = when (message.data[action]){
               "LIKE" -> "LIKE"
               "NEWPOST" -> "NEWPOST"
               else -> "OTHER"
           }
           when (Action.values().first() {it.name == name}){
               Action.LIKE -> handleLike(gson.fromJson(message.data[content],Like::class.java))
               Action.NEWPOST -> handlePost(gson.fromJson(message.data[content],NewPost::class.java))
               else -> handleUnknown(it)
           }

          /* try {
           val enum = Action.valueOf(it)
               when (enum) {
                   Action.LIKE -> handleLike(gson.fromJson(message.data[content],
                       Like::class.java))
                   Action.NEWPOST -> handlePost(gson.fromJson(message.data[content],
                       NewPost::class.java))
               }
           } catch (e: IllegalArgumentException) {
               handleUnknown(it)
               return@let
           } */
       }
   }
    private fun handleUnknown(unknow: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_unknow,
                    unknow,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
    override fun onNewToken(token: String) {
        println("Token: $token")
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           // .setLargeIcon(R.drawable.ic_notification_favorite)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
    private fun handlePost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_new_post,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // .setLargeIcon(R.drawable.ic_notification_favorite)
             .setStyle(NotificationCompat.BigTextStyle()
             .bigText(content.text))
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE,NEWPOST,OTHER
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)
data class NewPost(
    val postAuthor: String,
    val text: String
)

/*
class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {

        println(Gson().toJson(message))
    }

    override fun onNewToken(token: String) {
        println("Token: $token")
    }
} */