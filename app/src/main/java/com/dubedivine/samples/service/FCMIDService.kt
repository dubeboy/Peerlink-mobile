package com.dubedivine.samples.service

import android.util.Log
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.local.PreferencesHelper
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import javax.inject.Inject
import android.content.ComponentName
import android.app.ActivityManager
import android.content.Context
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.signIn.SignInMoreDetails


class FCMIDService : FirebaseInstanceIdService() {

    @Inject
     public lateinit var dataManager: DataManager

    override fun onTokenRefresh() {

        val pref = PreferencesHelper(this)

        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("FCMIDService", "Refreshed token: $refreshedToken")

        //
        if (refreshedToken != null) {
            //save the token bro!!
            pref.save {
                putString(FCM_TOKEN, refreshedToken)
                putBoolean(FCM_TOKEN_PUSHED, false)
            }

         //   sendRegistrationToServer(pref, refreshedToken)
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    private fun sendRegistrationToServer(pref: PreferencesHelper, refreshedToken: String) {
        if (pref.getString(SignInMoreDetails.P_ID).isNotBlank()) {
            dataManager.sendFCMTokenToUser(refreshedToken, User(pref.getUserId()))
            Log.d("FCMIDService", "saved user fcm token")
            pref.save { putBoolean(FCM_TOKEN_PUSHED, true) }
        }


        Log.d("FCMIDService", "Cannot push the phone fcm token")
    }

    companion object {
        const val FCM_TOKEN = "fcm_token"
        //used to check weather the firebase token was pushed bro
        const val FCM_TOKEN_PUSHED = "fcm_token_pushed"
    }
}