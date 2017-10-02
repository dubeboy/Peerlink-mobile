package com.dubedivine.samples.features.addQuestion

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.StatusResponse
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import javax.inject.Inject

/**
 * Created by divine on 2017/09/23.
 */

class AddQuestionPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<AddQuestionMvpView>() {
    override fun attachView(mvpView: AddQuestionMvpView) {
        super.attachView(mvpView)
    }

    fun getTagSuggestion(tag: CharSequence, tagStartIndex: Int, tagStopIndex: Int) {
        doLongTaskOnView {
            mDataManager.getTagSuggestion(tag)
                    .compose(SchedulerUtils.ioToMain<List<Tag>>())
                    .subscribe({
                        mvpView?.showTagsSuggestion(it, tag, tagStartIndex, tagStopIndex)
                        mvpView?.showProgress(false)
                    }, {
                        mvpView?.showError(it)
                        mvpView?.showProgress(false)
                    })
        }
    }

    fun publishNewQuestion(question: Question, docsListPaths: List<String>) {
        doLongTaskOnView {
            val retrofitFileParts = BasicUtils.createMultiPartFromFile(docsListPaths)
            mDataManager.postQuestion(question, retrofitFileParts!!)
                    .compose(SchedulerUtils.ioToMain<StatusResponse<Question>>())
                    .subscribe({
                        when (it.status) {
                            true -> {
                                mvpView!!.showProgress(false)
                                mvpView!!.showQuestion(it.entity!!)
                            }
                            false -> {
                                mvpView!!.showError(Throwable("Sorry failed to upload Question"))
                            }
                        }
                    }, {
                        mvpView!!.showError(it)
                    })
        }
    }

}
