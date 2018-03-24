package com.dubedivine.samples.features.signIn

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.dubedivine.samples.R
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.main.MainActivity
import com.dubedivine.samples.util.getProgressBarInstance
import com.dubedivine.samples.util.toast
import kotlinx.android.synthetic.main.activity_sign_in_more_details.*
import javax.inject.Inject

class SignInMoreDetails : BaseActivity(), SignInMvpView {

    @Inject
    lateinit var mSignInPresenter: SignInPresenter
    private lateinit var mPreferencesHelper: PreferencesHelper // todo: should also inject this
    private lateinit var progressDialog: ProgressDialog


    override val layout: Int
        get() = R.layout.activity_sign_in_more_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mSignInPresenter.attachView(this)
        mPreferencesHelper = PreferencesHelper(this)
        progressDialog = getProgressBarInstance("Loading", "Signing in")


        // we pass the email only because that is what is needed to check if the user existss
        mSignInPresenter.startMainIfUserIsRegistered(
                User("",
                        intent.getStringExtra(P_EMAIL),
                        null,
                        "",
                        "",
                        null))


        btn_next.setOnClickListener({
            //upload all the data to the server
            val degree = et_degree.text.toString()
            val nickname = et_nickname.text.toString()

            if (degree.isNotBlank() && nickname.isNotBlank()) {
                val user = User(nickname,
                        intent.getStringExtra(P_EMAIL),
                        intent.getStringExtra(P_PHOTO_URL),
                        degree)

                Log.d(TAG, "sending this user data $user")
                mSignInPresenter.sendUserTokenToServer(user)

            } else {
                if (nickname.isBlank()) {
                    et_degree.error = "Please enter nickname"
                    toast("Please enter degree")
                    return@setOnClickListener
                }
                if (degree.isBlank()) {
                    et_degree.error = "Please enter degree"
                    toast("Please enter degree")
                    return@setOnClickListener
                }
            }
        })
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun showError(error: Throwable) {
        toast("Oops something went wrong, please try again")
        //todo: print error to firebase
        error.printStackTrace()
    }

    override fun showProgressWithMessage(show: Boolean, title: String, msg: String) {

    }

    override fun signedIn(user: User) {
        persistUserDetails(user)
        toast("Signed in")
        startActivity(MainActivity.getStartIntent(this))
    }

    private fun persistUserDetails(user: User) {
        val (nickname, email, photoUrl, degree, id) = user

        mPreferencesHelper.save {
            putString(P_EMAIL, email)
            putString(P_NICKNAME, nickname)
            putString(P_PHOTO_URL, photoUrl)
            putString(P_DEGREE, degree)
            putString(P_ID, id)
        }

    }

    companion object {
        const val P_EMAIL = "email"
        const val P_NICKNAME = "nickname"
        const val P_PHOTO_URL = "photoUrl"
        const val P_DEGREE = "degree"
        const val P_ID = "id"
        const val TAG = "__SIGN_IN_MORE_DET__"

        fun getStartIntent(context: Context, email: String, photoUrl: String?): Intent {
            val intent = Intent(context, SignInMoreDetails::class.java)
            intent.putExtra(P_EMAIL, email)
            intent.putExtra(P_PHOTO_URL, photoUrl)
            return intent
        }
    }
}

