package com.dubedivine.samples.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.d("NotificationService","po=$remoteMessage")
        if (remoteMessage?.notification != null) {
            // do with Notification payload...
            // remoteMessage.getNotification().getBody()
            Log.d("NotificationService","pow=${remoteMessage.notification?.body}")

        }

        if (remoteMessage?.data!!.isNotEmpty()) {
            Log.d("NotificationService","poi=${remoteMessage.data!!}")
        }
    }
}