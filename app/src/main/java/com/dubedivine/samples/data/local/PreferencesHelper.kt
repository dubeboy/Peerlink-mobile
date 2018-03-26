package com.dubedivine.samples.data.local

import android.content.Context
import android.content.SharedPreferences
import com.dubedivine.samples.injection.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject
constructor(@ApplicationContext context: Context) {

    private val mPref: SharedPreferences

    init {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        mPref.edit().clear().commit()
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



    companion object {
        val PREF_FILE_NAME = "mvpstarter_pref_file"
    }

}
