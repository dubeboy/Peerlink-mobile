package com.dubedivine.samples.features.main.fragment

import android.os.Bundle
import android.view.View
import com.dubedivine.samples.R
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
        Thread({
            val req = Request.Builder().url("ws://localhost:8080/send/message").build()
            webSocketListener = WebSocketListener()
            val  ws = client.newWebSocket(req, webSocketListener)
            client.dispatcher().executorService().shutdown()
        }).start()
    }
}
