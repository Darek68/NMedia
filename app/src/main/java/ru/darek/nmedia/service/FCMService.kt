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
import dagger.hilt.android.AndroidEntryPoint
import ru.darek.nmedia.R
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.dto.*
import javax.inject.Inject
import kotlin.random.Random

class PushMes{
    val recipientId: Long ? = 0
    val content = ""
}

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    @Inject
    lateinit var auth: AppAuth

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
        //val myId = AppAuth.getInstance().authStateFlow.value.id
        val myId = auth.authStateFlow.value.id
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
