package com.dubedivine.samples.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.dubedivine.samples.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.dubedivine.samples.features.detail.DetailActivity
import com.google.firebase.crash.FirebaseCrash
import org.json.JSONObject
import java.io.Serializable


private const val CHANNEL_ID = "MY_CHANNEL"
private const val ITEM_ID = "ITEM_ID"
private const val ITEM_TYPE = "ITEM_TYPE"
class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        super.onMessageReceived(remoteMessage)
        Log.d("NotificationService", "po=$remoteMessage")
        if (remoteMessage?.notification != null ) {
            Log.d("NotificationService", "body=${remoteMessage.notification?.body}")
            if (remoteMessage.data!!.isNotEmpty()) {
                val title = remoteMessage.notification!!.title!!
                val body  = remoteMessage.notification!!.body!!
                val data = JSONObject(remoteMessage.data["key-1"])
                createNotification(title, body, data.getString("itemId"),  data.getString("entity"))
            } else {
                FirebaseCrash.log("The notification arrived with out data fields")
            }
        }
    }


    private fun createNotification(textTitle: String, contentText: String, itemID: String, itemType: String) {

        fun createPendingIntent(itemID: String, itemType: String): PendingIntent? {
            // Create an explicit intent for an Activity in your app
            val intent = Intent(this, DetailActivity::class.java) // we want to show the detail activity
            intent.putExtra(ITEM_ID, itemID)
            intent.putExtra(ITEM_TYPE, itemType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Peerlink", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Peerlink notification"
            notificationManager.createNotificationChannel(channel)
        }


        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_name)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setContentTitle(textTitle)
                .setContentText(contentText)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setContentIntent(createPendingIntent(itemID, itemType))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                .setAutoCancel(true)

        val notification = mBuilder.build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

//duplicate from server
enum class ENTITY_TYPE: Serializable {
    QUESTION, ANSWER, QUESTION_COMMENT, ANSWER_COMMENT, QUESTION_VOTE, ANSWER_VOTE
}

data class Data(private val itemId: String, private val entity_TYPE: ENTITY_TYPE,  private val msg: String? = null)
