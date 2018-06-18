package com.dubedivine.samples.data.local

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.features.signIn.SignIn
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.injection.ApplicationContext
import com.dubedivine.samples.util.log
import javax.inject.Inject
import javax.inject.Singleton

class PreferencesHelper
constructor( private val context: Context) {

    private val mPref: SharedPreferences

    init {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        mPref.edit().clear().apply()
    }

    fun editor(): SharedPreferences.Editor {
        return mPref.edit()
    }

    inline fun save(objectToBeSaved: SharedPreferences.Editor.() -> SharedPreferences.Editor) {
        objectToBeSaved(editor()).commit()
    }

    fun getString(key: String, defaultString: String = ""): String {
       return mPref.getString(key, defaultString)
    }

    fun getBoolean(key: String): Boolean {
        return mPref.getBoolean(key, false)
    }

    fun getUserId(): String {
        val userId = getString(SignInMoreDetails.P_ID)
        checkIfUserIsSignedIn(userId)
        return userId
    }

    private fun checkIfUserIsSignedIn(userId: String) {
        if (userId.isBlank()) {
            log("the user id is blank")
            context.startActivity(Intent(context, SignIn::class.java))
        }
    }

    companion object {
        val PREF_FILE_NAME = "mvpstarter_pref_file"
    }

}
