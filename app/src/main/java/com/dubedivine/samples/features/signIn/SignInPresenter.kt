package com.dubedivine.samples.features.signIn

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by divine on 3/11/18.
 */

@ConfigPersistent
class SignInPresenter  @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<SignInMvpView>() {

    fun sendUserTokenToServer(user: User) {
        doLongTaskOnView {
            mDataManager.signInUserWithServer(user)
                    .compose(SchedulerUtils.ioToMain())
                    .subscribe({
                        Timber.i("the result is $it")
                        mvpView?.signedIn(it.entity!!)
                        mvpView?.showProgress(false)
                    }, {  // when there is an error
                        mvpView?.showError(it)
                        mvpView?.showProgress(false)
                    })
        }
    }
}