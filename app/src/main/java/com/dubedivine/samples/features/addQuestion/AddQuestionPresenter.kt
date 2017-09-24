package com.dubedivine.samples.features.addQuestion

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BasePresenter
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
                    .compose<List<Tag>>(SchedulerUtils.ioToMain<List<Tag>>())
                    .subscribe({
                        mvpView?.showTagsSuggestion(it, tag, tagStartIndex, tagStopIndex)
                        mvpView?.showProgress(false)
                    }, {
                        mvpView?.showError(it)
                        mvpView?.showProgress(false)
                    })
        }
    }

}
