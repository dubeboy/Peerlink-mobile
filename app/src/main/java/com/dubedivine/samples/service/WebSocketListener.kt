package com.dubedivine.samples.service

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket

class WebSocketListener : okhttp3.WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response?) {
        webSocket.send("Hello there")
        webSocket.close(1000, "normal close")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d("WEBLIST","recieved message $text" )
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(1000, null)
        Log.d("WEBLIST", "Closing $code/$reason")
    }
}