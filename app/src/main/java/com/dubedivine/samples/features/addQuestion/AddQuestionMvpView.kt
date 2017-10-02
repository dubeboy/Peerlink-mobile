package com.dubedivine.samples.features.addQuestion

import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.MvpView

/**
 * Created by divine on 2017/09/23.
 */
interface AddQuestionMvpView : MvpView {
    fun showTagsSuggestion(tags: List<Tag>,
                           typedWord: CharSequence,
                           tagStartIndex: Int,
                           tagStopIndex: Int)

    fun showQuestion(entity: Question)


}