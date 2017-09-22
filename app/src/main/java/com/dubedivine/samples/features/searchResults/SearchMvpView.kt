package com.dubedivine.samples.features.searchResults

import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.base.MvpView

/**
 * Created by divine on 2017/09/10.
 */
interface SearchMvpView : MvpView {
    fun showQuestionsSearchResults(questions: List<Question>)
}