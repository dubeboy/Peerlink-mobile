package com.dubedivine.samples.features.detail

import com.dubedivine.samples.data.model.*
import com.dubedivine.samples.features.base.MvpView

interface DetailMvpView : MvpView {
    fun showQuestionsAndAnswers(pokemon: Pokemon)

    fun showStat(statistic: Statistic)

    fun addAnswers(answer: List<Answer>)

    fun showUserMessage(msg: String)

    fun showAnswer(entity: Answer)

    fun downVoteAnswerAndDisableButton(answerId: String, detailView: DetailAdapter.DetailView)

    fun upVoteAnswerAndDisableButton(answerId: String, detailView: DetailAdapter.DetailView)

    fun downVoteAndDisableButton(detailView: DetailAdapter.DetailView)

    fun upVoteAndDisableButton(detailView: DetailAdapter.DetailView)

    //TODO: @Deprecated detailView
    fun showCommentForQuestion(questionId: String, comment: Comment, detailView: DetailAdapter.DetailView)

    //TODO: @Deprecated detailView
    fun showCommentForAnswer(answerId: String, comment1: Comment, detailView: DetailAdapter.DetailView)

    fun showQuestion(question: Question)
}