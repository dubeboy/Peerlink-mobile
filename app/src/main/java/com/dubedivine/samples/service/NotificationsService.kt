package com.dubedivine.samples.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class NotificationsService : Service() {



    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
