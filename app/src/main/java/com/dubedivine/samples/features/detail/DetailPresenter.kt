package com.dubedivine.samples.features.detail

import android.app.Activity
import android.util.Log
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.*
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.features.detail.DetailActivity.Companion.TAG
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/*
*  lots of repeated code! needs real refactoring
* */
@ConfigPersistent
class DetailPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<DetailMvpView>() {


    // ugly code start ---------------------------------------------

    private lateinit var mPref: PreferencesHelper
    //   @Inject
    private lateinit var _context: Activity
    var context: Activity
        get() = _context
        set(value) {
            this._context = value
            mPref = PreferencesHelper(context)
        }

    // ugly code stop ------------------------------------------------


    override fun attachView(mvpView: DetailMvpView) {
        super.attachView(mvpView)
    }

    fun getFullQuestion(questionId: String) = doLongTaskOnView {
        mDataManager.getQuestion(questionId)
                .compose(SchedulerUtils.ioToMain<StatusResponse<Question>>())
                .subscribe({
                    // todo check status and show error page
                    mvpView?.showQuestion(it.entity!!)
                    mvpView?.showProgress(false)
                }) { throwable ->
                    mvpView?.showProgress(false)
                    mvpView?.showError(throwable)
                }
    }

    fun getPokemon(name: String) {
        checkViewAttached()
        mvpView?.showProgress(true)
        mDataManager.getPokemon(name)
                .compose<Pokemon>(SchedulerUtils.ioToMain<Pokemon>())
                .subscribe({ pokemon ->
                    // It should be always checked if MvpView (Fragment or Activity) is attached.
                    // Calling showProgress() on a not-attached fragment will throw a NPE
                    // It is possible to ask isAdded() in the fragment, but it's better to ask in the presenter
                    mvpView?.showProgress(false)
                    mvpView?.showQuestionsAndAnswers(pokemon)
                    for (statistic in pokemon.stats) {
                        mvpView?.showStat(statistic)
                    }
                }) { throwable ->
                    mvpView?.showProgress(false)
                    mvpView?.showError(throwable)
                }
    }

    fun getMoreAnswers(questionId: String, page: Int) {
        doLongTaskOnView {
            mDataManager.getMoreAnswers(questionId, page)
                    .compose(SchedulerUtils.ioToMain<List<Answer>>())
                    .subscribe({
                        mvpView!!.addAnswers(it)
                        mvpView!!.showProgress(false)
                    }, {
                        mvpView!!.showError(it)
                        mvpView!!.showProgress(false)
                    })
        }
    }

    fun addAnswer(questionId: String, answer: String, fileSet: HashSet<String>) {
        doLongTaskOnView {
            mDataManager.postAnswer(questionId,
                    Answer(answer, 0, false,
                            User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))))

                    .compose(SchedulerUtils.ioToMain())
                    .subscribe({ statusResponse ->
                        if (statusResponse.status!!) {

                            if (fileSet.isNotEmpty()) {
                                val distinctFiles: List<String> = fileSet.distinct()
                                val retrofitFileParts: MutableList<MultipartBody.Part> = BasicUtils.createMultiPartFromFile(distinctFiles)
                                mDataManager.postAnswerFiles(questionId, statusResponse.entity!!.id!!, retrofitFileParts)
                                        .compose(SchedulerUtils.ioToMain())
                                        .subscribe({
                                            mvpView!!.showProgress(false)
                                            mvpView!!.showAnswer(it.entity!!)
                                        }, {
                                            mvpView!!.showUserMessage("Failed to upload the file please try again")
                                        })
                            } else {
                                mvpView!!.showAnswer(statusResponse.entity!!)

                            }
                        } else {
                            mvpView!!.showProgress(false)
                            mvpView!!.showUserMessage("Failed to save answer please try again")

                        }
                    },
                            {
                                mvpView!!.showError(it)
                                mvpView!!.showProgress(false)
                            })
        }
    }

    /**
     * @param vote - is boolean indicating weather we voting up or down
     *when vote is true - we voting up else vote is false - voting down
     * @param qId - its the question Id
     *
     * */
    fun addVote(qId: String, vote: Boolean, detailView: DetailAdapter.DetailView) {
        doLongTaskOnView {
            mDataManager.addVote(qId, mPref.getUserId(), vote).compose(SchedulerUtils.ioToMain())
                    .subscribe({
                        mvpView!!.showProgress(false)
                        if (it.status!!) {
                            mvpView!!.showUserMessage("Your vote has been casted.")
                        } else {
                            //should only tell the user when there is an error and then do their opposite action
                            mvpView!!.showUserMessage("Sorry failed to cast your vote, please try again.")
                            //TODO Not required!!!
                            when (vote) {
                                true -> {
                                    mvpView!!.downVoteAndDisableButton(detailView)
                                }
                                false -> {
                                    mvpView!!.upVoteAndDisableButton(detailView)
                                }
                            }
                        }
                    }, {
                        Log.e(TAG, "Something went wrong big time while adding vote here is the error message:")
                        mvpView!!.showError(it)
                        mvpView!!.showProgress(false)
                    })
        }
    }

    fun addVoteToAnswer(qId: String, ansId: String?, vote: Boolean, detailView: DetailAdapter.DetailView) {
        doLongTaskOnView {
            mDataManager.addVoteToAnswer(qId, mPref.getUserId(), ansId!!, vote).compose(SchedulerUtils.ioToMain())
                    .subscribe({
                        mvpView!!.showProgress(false)
                        if (it.status!!) {
                        } else {
                            mvpView!!.showUserMessage("Sorry failed to cast your vote, please try again.")
                            when (vote) {
                                true -> {
                                    mvpView!!.downVoteAnswerAndDisableButton(ansId, detailView)
                                }
                                false -> {
                                    mvpView!!.upVoteAnswerAndDisableButton(ansId!!, detailView)
                                }
                            }
                        }
                    }, {
                        Log.e(TAG, "Something went wrong bit time while adding vote to answer here is the error message:")
                        mvpView!!.showError(it)
                        mvpView!!.showProgress(false)
                    })
        }
    }

    fun postCommentQuestion(questionId: String, body: String, detailView: DetailAdapter.DetailView) {
        if (body.isNotBlank()) {
            doLongTaskOnView {
                mDataManager.postCommentQuestion(questionId, Comment(body, 0, User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))))
                        .compose(SchedulerUtils.ioToMain())
                        .subscribe({
                            mvpView!!.showProgress(false)
                            if (it.status!!) {
                                mvpView!!.showCommentForQuestion(questionId, Comment(body, 0, User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))), detailView)
                            } else {
                                mvpView!!.showUserMessage("Failed to share comment, please try again")
                            }
                        }, {
                            mvpView!!.showProgress(false)
                            Log.e(TAG, "Something went wrong really bad")
                            mvpView!!.showError(it)
                        })

            }
        } else mvpView!!.showUserMessage("Please type a comment")
    }

    fun postCommentForAnswer(questionId: String, answerId: String, body: String, detailView: DetailAdapter.DetailView) {
        if (body.isNotBlank()) {
            doLongTaskOnView {
                mDataManager.postCommentForAnswer(questionId, answerId, Comment(body, 0, User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))))
                        .compose(SchedulerUtils.ioToMain())
                        .subscribe({
                            mvpView!!.showProgress(false)
                            if (it.status!!) {
                                //todo creating a new comment object really?
                                mvpView!!.showCommentForAnswer(answerId, Comment(body, 0, User(mPref.getUserId(), mPref.getString(SignInMoreDetails.P_NICKNAME))), detailView)
                            } else {
                                mvpView!!.showUserMessage("Failed to share comment, please try again")
                            }
                        }, {
                            mvpView!!.showProgress(false)
                            Log.e(TAG, "Something went wrong really bad")
                            mvpView!!.showError(it)
                        })
            }

        } else mvpView!!.showUserMessage("Please type a comment")
    }

    // for fragments the call back is on the method because it does not make sense right now to create
    // a mvp view just for this fragment

    fun fragmentFetchVideo(peerlinkDirectory: String, videoLocation: String,
                           success: (isSuccess: Boolean, message: String, file: File?) ->
                           Unit, progress: (prog: Long, fileSize: Long) -> Unit) {
        // sane check to see where there the string had just empty characters
        if (videoLocation.isNotBlank()) {
            mDataManager.getVideo(videoLocation)
                    .compose(SchedulerUtils.ioToMain())
                    .subscribe({
                        val fileReader = ByteArray(4096)
                        val fileSize = it.contentLength()
                        var fileSizeDownloaded = 0L
                        val inputStream = it.byteStream()
                        val parentFile = File(peerlinkDirectory)
                        parentFile.mkdirs()
                        val f = File(parentFile, "${videoLocation}_${System.currentTimeMillis()}")
                        val outputStream = FileOutputStream(f)
                        while (true) {
                            val read = inputStream.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read
                            progress(fileSizeDownloaded, fileSize)
//                            Log.d(TAG, "file size downloaded$fileSizeDownloaded of $fileSize")
                        }
                        outputStream.flush()
                        inputStream.close()
                        outputStream.close()
                        success(true, "file downloaded", f)
                    }, {
                        it.printStackTrace()
                        success(false, "failed to download the video", null)
                    })
        }
    }
}
