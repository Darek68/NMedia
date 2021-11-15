package ru.darek.nmedia.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.darek.nmedia.R
import ru.darek.nmedia.api.PostsApi
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.dto.*
import kotlin.random.Random

class PushMes{
    val recipientId: Long ? = 0
    val content = ""
}

class FCMService : FirebaseMessagingService() {
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
      //  println(Gson().toJson(message))
        val myId = AppAuth.getInstance().authStateFlow.value.id
        message.data[content]?.let {
            val mess = gson.fromJson(message.data[content], PushMes::class.java)
            val recipientId = mess.recipientId
            println("myId >>>   " + myId.toString())
            println("recipientId >>>   " + mess.recipientId.toString())
            println("content >>>   " + mess.content)

            if (recipientId == myId || recipientId == null) {
                // показываем Notification
                showPush(mess)
            } else {
                // переотправить свой push token
                getPushTokenAndSend() //тут и запросим и отправим
              //  FirebaseMessaging.getInstance().token.addOnSuccessListener {
                  //  println("Получен новый Token >>>   \n " + it)
                   // AppAuth.sendPushToken()

               // }
            }
        }
    }
         fun showPush(mes:PushMes) {
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_test_push,
                        mes.content,
                        mes.recipientId.toString(),
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    //println("Token >>>   " + it)
}
/*
class FCMService : FirebaseMessagingService() {
    private val content = "content"
    private val channelId = "remote"
    private val recipientId = "recipientId"
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
        /* если recipientId = тому, что в AppAuth, то всё ok, показываете Notification;
если recipientId = 0 (и не равен вашему), значит сервер считает, что у вас анонимная аутентификация и вам нужно переотправить свой push token;
если recipientId != 0 (и не равен вашему), значит сервер считает, что на вашем устройстве другая аутентификация и вам нужно переотправить свой push token;
если recipientId = null, то это массовая рассылка, показываете Notification. */
        /* MyId - это id пользователя из AppAuth. Теперь вам не нужно никаких хитрых flatmap, поэтому просто обращайтесь к AppAuth.getInstance и берите оттуда id. */
        val appAuth = AppAuth.getInstance()
        val myId = appAuth.authStateFlow.value.id
         message.data[recipientId]?.let {

            /* if (it == )
             when (Action.values().first() {it.name == name}){
                 Action.LIKE -> handleLike(gson.fromJson(message.data[content],Like::class.java))
                 Action.NEWPOST -> handlePost(gson.fromJson(message.data[content],NewPost::class.java))
                 else -> handleUnknown(it)
             }


            val name = when (message.data[recipientId]){
                "LIKE" -> "LIKE"
                "NEWPOST" -> "NEWPOST"
                else -> "OTHER"
            }
            when (Action.values().first() {it.name == name}){
                Action.LIKE -> handleLike(gson.fromJson(message.data[content],Like::class.java))
                Action.NEWPOST -> handlePost(gson.fromJson(message.data[content],NewPost::class.java))
                else -> handleUnknown(it)
            }

             try {
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
        println("content >>>   " + message.data["content"])
        println("recipientId >>>   " + message.data["recipientId"])
        //println("Token >>>   " + it)
    }

    override fun onNewToken(token: String) {
       // AppAuth.getInstance().sendPushToken(token)
        println("Token: $token")
    }
} */

/*
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
      // println(Gson()).toJson(message))

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
    override fun onNewToken(token: String) {
        println("Token: $token")
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
*/

