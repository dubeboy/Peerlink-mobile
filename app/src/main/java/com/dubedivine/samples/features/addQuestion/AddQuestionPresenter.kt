package com.dubedivine.samples.features.addQuestion

import android.app.Activity
import android.util.Log
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.StatusResponse
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.data.model.User
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * Created by divine on 2017/09/23.
 */

class AddQuestionPresenter @Inject
constructor(private val mDataManager: DataManager, activity: Activity) : BasePresenter<AddQuestionMvpView>() {

    private var mPref: PreferencesHelper = PreferencesHelper(activity)

    override fun attachView(mvpView: AddQuestionMvpView) {
        super.attachView(mvpView)
    }

    fun getTagSuggestion(tag: CharSequence, tagStartIndex: Int, tagStopIndex: Int) {
        checkViewAttached()
        mvpView!!.showProgress(false)  // diable the default behaviour
        mvpView!!.showTagSuggestionProgress(true)
        mDataManager.getTagSuggestion(tag)
                .compose(SchedulerUtils.ioToMain<List<Tag>>())
                .subscribe({
                    mvpView!!.showTagSuggestionProgress(false)
                    mvpView!!.showTagsSuggestion(it, tag, tagStartIndex, tagStopIndex)
                }, {
                    mvpView!!.showError(it)
                    mvpView!!.showTagSuggestionProgress(false)
                })
    }

    fun publishNewQuestion(question: Question, files: List<String>? = null) {
        doLongTaskOnView {
            // set the user of this question
            question.user = User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))
            mDataManager.postQuestion(question)
                    .compose(SchedulerUtils.ioToMain<StatusResponse<Question>>())
                    .subscribe({
                        Log.d(TAG, "the return v is $it")
                        when (it.status) {
                            true -> {
                                mvpView!!.showProgress(false)
                                if (files != null && files.isNotEmpty()) {
                                    mvpView!!.showProgress(true)
                                    val retrofitFileParts: MutableList<MultipartBody.Part> = BasicUtils.createMultiPartFromFile(files)
                                    mDataManager.postQuestionFiles(it.entity!!.id!!, retrofitFileParts)
                                            .compose(SchedulerUtils.ioToMain<StatusResponse<Question>>())
                                            .subscribe(
                                                    {
                                                        // todo should check status here
                                                        Log.d(TAG, "We are now here we got the question $it")
                                                        mvpView!!.showProgress(false)
                                                        mvpView!!.showQuestion(it.entity!!)
                                                    },
                                                    {
                                                        mvpView!!.showError(it)
                                                        mvpView!!.showProgress(false)
                                                    }
                                            )
                                } else {
                                    mvpView!!.showQuestion(it.entity!!)
                                    mvpView?.showProgress(false)
                                }
                            }
                            false -> {
                                mvpView!!.showError(Throwable("Sorry failed to upload Question"))
                                mvpView?.showProgress(false)

                            }
                        }
                    }, {
                        mvpView!!.showError(it)
                        mvpView?.showProgress(false)

                    })
        }
    }

    companion object {
        val TAG = "__AddQuestionPresenter"
    }

}
