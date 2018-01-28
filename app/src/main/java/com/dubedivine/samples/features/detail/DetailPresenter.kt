package com.dubedivine.samples.features.detail

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Answer
import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import okhttp3.MultipartBody
import javax.inject.Inject

@ConfigPersistent
class DetailPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<DetailMvpView>() {

    override fun attachView(mvpView: DetailMvpView) {
        super.attachView(mvpView)
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
            mDataManager.postAnswer(questionId, Answer(answer, 0, false))
                    .compose(SchedulerUtils.ioToMain())
                    .subscribe({
                        if (it.status!!) {

                            if (fileSet.isNotEmpty()) {
                                val distinctFiles: List<String> = fileSet.distinct()
                                val retrofitFileParts: MutableList<MultipartBody.Part>
                                        = BasicUtils.createMultiPartFromFile(distinctFiles)
                                mDataManager.postAnswerFiles(questionId, it.entity!!.id!!, retrofitFileParts)
                                        .compose(SchedulerUtils.ioToMain())
                                        .subscribe({
                                            mvpView!!.showProgress(false)
                                            mvpView!!.showAnswer(it.entity!!)
                                        },{
                                            mvpView!!.showUserError("Failed to upload the file please try again")
                                        })
                            } else {
                                mvpView!!.showAnswer(it.entity!!)

                            }
                        } else {
                            mvpView!!.showProgress(false)
                            mvpView!!.showUserError("Failed to save answer please try again")

                        }
                    },
                    {
                        mvpView!!.showError(it)
                        mvpView!!.showProgress(false)
                    })
        }
    }
}
