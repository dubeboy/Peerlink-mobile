package com.dubedivine.samples.features.signIn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.dubedivine.samples.R
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlin.system.exitProcess

/**
 * Created by divine on 3/11/18.
 */

class SignIn : BaseActivity() {


    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
//                .requestIdToken() todo: should do this some time in the future
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)
       // updateUI(account)

        sign_in_button.setSize(SignInButton.SIZE_WIDE)
        sign_in_button.setOnClickListener({
            toast("Starting google sign in")
            signIn()
        })
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            val email = account.email
            val photoUrl = account.photoUrl
            Log.d(TAG, "$email and photo $photoUrl")
            startActivity(SignInMoreDetails.getStartIntent(this, email!!, photoUrl?.toString()))
        } else {
            Log.d(TAG, "the account is null")
            toast("Could not sign you in please try again")
        }
    }

    override val layout: Int
        get() = R.layout.activity_sign_in

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "the account is ${account.email} ${account.photoUrl}")
            // we want to send this data to the server if everything went well then we will
            // save the user data
            if (account != null) {
                toast("Yay!!!, You have successfully signed in.")
                updateUI(account)
            } else
                updateUI(null)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=${e.statusCode}")
            Log.e(TAG, e.toString())
            updateUI(null)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        const val RC_SIGN_IN = 100
        const val TAG = "__SIGN_IN__"

        fun getStartIntent(context: Context): Intent {

            val intent = Intent(context, SignIn::class.java)
//            activitySta

            // delete all the items on the activity stack
            return intent
        }

    }
}

