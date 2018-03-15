package com.dubedivine.samples.features.signIn

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dubedivine.samples.R
import com.dubedivine.samples.features.base.BaseActivity

class SignInMoreDetails : BaseActivity() {
    override val layout: Int
        get() = R.layout.activity_sign_in_more_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SignInMoreDetails::class.java)
        }

    }
}
