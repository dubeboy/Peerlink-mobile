package com.dubedivine.samples.features.searchResults

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.features.main.MainMvpView
import com.dubedivine.samples.injection.ConfigPersistent
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

    fun getSearchResults(page: Int) {

        mvpView?.showQuestionsSearchResults(mDataManager.questions)
    }
}