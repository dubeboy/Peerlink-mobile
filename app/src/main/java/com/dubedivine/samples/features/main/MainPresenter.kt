package com.dubedivine.samples.features.main

import android.util.Log
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.StatusResponse
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.service.FCMIDService
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import io.reactivex.Flowable
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ConfigPersistent
class MainPresenter @Inject
constructor(private val mDataManager: DataManager,
            private val preferencesHelper: PreferencesHelper) : BasePresenter<MainMvpView>() {

    override fun attachView(mvpView: MainMvpView) {
        super.attachView(mvpView)
    }


    fun pushFCMToken(): Boolean {
        if (!preferencesHelper.getBoolean(FCMIDService.FCM_TOKEN_PUSHED)) {
            if (preferencesHelper.getUserId().isNotBlank() &&
                    preferencesHelper.getString(FCMIDService.FCM_TOKEN).isNotBlank()) {

                Timber.i("pushing the fcm")
                val x = mDataManager.sendFCMTokenToUser(preferencesHelper.getString(FCMIDService.FCM_TOKEN),
                        User(preferencesHelper.getUserId(),preferencesHelper.getString(SignInMoreDetails.P_NICKNAME)))
                x.compose(SchedulerUtils.ioToMain<StatusResponse<Boolean>>())
                        .subscribe({
                            Timber.i("FCM pushed and updated")
                            preferencesHelper.save { putBoolean(FCMIDService.FCM_TOKEN_PUSHED, true) }
                        }, {
                            Timber.i("Server had an error updating the fcm")
                            it.printStackTrace()
                        })

                return true
            }
        }
        return false
    }

    fun getPokemon(limit: Int) {
        checkViewAttached()
        mvpView?.showProgress(true)
        /* mDataManager.getPokemonList(limit)
                 .compose(SchedulerUtils.ioToMain<List<String>>())
                 .subscribe({ pokemons ->
                     mvpView?.showProgress(false)
                     mvpView?.showPokemon(pokemons)
                 }) { throwable ->
                     mvpView?.showProgress(false)
                     mvpView?.showError(throwable)
                 }*/
    }

    fun getSuggestions(chars: CharSequence?) {
        checkViewAttached()
        mvpView?.showProgressOnAutoComplete(true);
        if (chars != null) {
            if (chars.length >= 3) {
                Timber.i("getSuggestions is being called with text $chars")
                Flowable
                        .just(chars) //todo NB: does not work
                        .debounce(2, TimeUnit.SECONDS) // debouncing when the user is inputting the text
                        .subscribe({
                            Timber.i("getSuggestions: calling the api for some data with this title $it")
                            mDataManager.getSuggestions(it)
                                    .compose(SchedulerUtils.ioToMain<List<Question>>())
                                    .subscribe({
                                        Timber.i("getSuggestions: got results for $it")
                                        mvpView?.showProgressOnAutoComplete(false)
                                        mvpView?.showSuggestions(it)
                                    }, {
                                        mvpView?.showProgressOnAutoComplete(false)
                                        //todo: do something useful from here
                                        mvpView?.showError(it)
                                        Timber.e(it)
                                    })
                        })
            }
        }
    }

    /**
     * @param question this is the one that will be passed to the search to be put on top
     * */
    fun getQuestions(question: Question?) {
        Timber.d("getQuestions: calling the api to get the questions with name ")
    }

    //todo : should inline function please
    fun fragmentGetTagsSubscribed(success: (status: Boolean, message: String, tags: List<Tag>?) -> Unit) {
        mDataManager.getTagsSubscribed(preferencesHelper.getUserId())
                .compose(SchedulerUtils.ioToMain())
                .subscribe({
                    if (it.status!!) {
                        success(true, "got subscribed tags", it.entity!!)
                    } else {
                        success(false, "could not get the tags somehow sorry please try again", null)
                    }
                }, {
                    it.printStackTrace()
                    success(false, "an error happened, please make sure you connected to the internet", null)
                })

    }


}