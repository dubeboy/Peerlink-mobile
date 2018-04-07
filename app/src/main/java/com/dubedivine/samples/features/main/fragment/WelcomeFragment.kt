package com.dubedivine.samples.features.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.dubedivine.samples.R
import com.dubedivine.samples.R.id.btn_test_ws
import com.dubedivine.samples.features.base.BaseFragment
import com.dubedivine.samples.service.WebSocketListener
import kotlinx.android.synthetic.main.fragment_welcome.*
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by Divine on 3/24/18.
 */
class WelcomeFragment : BaseFragment() {

    private var webSocketListener: WebSocketListener? = null
    private lateinit var client: OkHttpClient

    override val layout: Int
        get() = R.layout.fragment_welcome

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        client = OkHttpClient()

        btn_test_ws.setOnClickListener({
            start()
        })
    }

    private fun start() {
        // did it so that I can debug it
        Thread(object : Runnable {
            override fun run() {
                Log.d("WELCOM_FRAG", "doing ws bro")
                val req = Request.Builder().url("ws://echo.websocket.org").build()
                webSocketListener = WebSocketListener()
                client.newWebSocket(req, webSocketListener)
            }
            //     client.dispatcher().executorService().shutdown()
        }).start()
    }
}
