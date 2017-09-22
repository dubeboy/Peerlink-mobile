package com.dubedivine.samples.features.searchResults

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by divine on 2017/09/10.
 */

@ConfigPersistent //active till the life time of  aactivity
class SearchPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<SearchMvpView>() {



    //attaching the view to the class
    override fun attachView(mvpView: SearchMvpView) {
        super.attachView(mvpView)
    }

    fun getSearchResults(questionName: String, page: Int) {
//        val questions = mDataManager.questions
//        Timber.i("getSearchResults: the questions is $questions")
//        mvpView?.showQuestionsSearchResults(questions)

        // quick check u know like contract sota thing!! so that we dont do useless network calls
        if (questionName.isBlank()) return

        doLongTaskOnView {
            mDataManager.searchForQuestionUsingName(questionName, page)
                    .compose(SchedulerUtils.ioToMain<List<Question>>())
                    .subscribe({
                        Timber.i("the questions is $it")
                        mvpView?.showQuestionsSearchResults(it)
                        mvpView?.showProgress(false)
                    }, {  // when there is an error
                        mvpView?.showError(it)
                        mvpView?.showProgress(false)
                    })
        }

    }
}