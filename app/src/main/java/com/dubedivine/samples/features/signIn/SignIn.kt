package com.dubedivine.samples.features.signIn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.dubedivine.samples.R
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.main.MainActivity
import com.dubedivine.samples.util.showProgressAlertDialog
import com.dubedivine.samples.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject

/**
 * Created by divine on 3/11/18.
 */
class SignIn : BaseActivity(), SignInMvpView {


    private lateinit var mGoogleSignInClient: GoogleSignInClient
    @Inject lateinit var mSignInPresenter: SignInPresenter
    private lateinit var mPreferencesHelper: PreferencesHelper // todo: should also inject this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mSignInPresenter.attachView(this)
        mPreferencesHelper = PreferencesHelper(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)

        sign_in_button.setSize(SignInButton.SIZE_WIDE)
        sign_in_button.setOnClickListener({
            signIn()
        })
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            startActivity(MainActivity.getStartIntent(this))
        } else {
            Log.d(TAG, "the account is null")
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
            Log.d(TAG, "the account is $account")
            if (account != null) {
                mSignInPresenter.sendUserTokenToServer(account)

            } else
                updateUI(null)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun persistUserDetails(account: GoogleSignInAccount) {
        val email = account.email
        val displayName = account.displayName
        val photoUrl = account.photoUrl

        mPreferencesHelper.save {
            putString(P_EMAIL, email)
            putString(P_DISPLAY_NAME, displayName)
            putString(P_PHOTO_URL, photoUrl.toString())
        }

    }

    override fun showProgress(show: Boolean) {

    }

    override fun showError(error: Throwable) {
        toast("Oops something went wrong")
    }

    override fun signedIn(account: GoogleSignInAccount) {
        persistUserDetails(account)
        updateUI(account)
    }

    override fun showProgressWithMessage(show: Boolean, title: String, msg: String) {
        showProgressAlertDialog(title, msg)
    }

    companion object {
        const val RC_SIGN_IN = 100
        const val P_EMAIL = "email"
        const val P_DISPLAY_NAME = "email"
        const val P_PHOTO_URL = "email"
        const val TAG = "__SIGN_IN__"
    }
}

