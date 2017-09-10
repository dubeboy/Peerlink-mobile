package com.dubedivine.samples.features.main

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
import io.reactivex.Flowable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ConfigPersistent
class MainPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<MainMvpView>() {

    override fun attachView(mvpView: MainMvpView) {
        super.attachView(mvpView)
    }

    fun getPokemon(limit: Int) {
        checkViewAttached()
        mvpView?.showProgress(true)
        mDataManager.getPokemonList(limit)
                .compose(SchedulerUtils.ioToMain<List<String>>())
                .subscribe({ pokemons ->
                    mvpView?.showProgress(false)
                    mvpView?.showPokemon(pokemons)
                }) { throwable ->
                    mvpView?.showProgress(false)
                    mvpView?.showError(throwable)
                }
    }

    fun getSuggestions(chars: CharSequence?) {
        checkViewAttached()
        mvpView?.showProgressOnAutoComplete(true);
        if (chars != null) {
            if (chars.length >= 5) {
                Timber.i("getSuggestions is being called with text $chars")
                Flowable
                        .just(chars)
                        .debounce(2, TimeUnit.SECONDS) // debouncing when the user is inputting the text
                        .subscribe({
                            Timber.i("getSuggestions: calling the api for some data with this title $it")
                            mDataManager.getSuggestions(it)
                                    .compose(SchedulerUtils.ioToMain<List<Question>>())
                                    .subscribe({
                                        Timber.i("getSuggestions: got results for $it")
                                        mvpView?.showProgressOnAutoComplete(false)
                                        mvpView?.showSuggestions(it.map { it.title })
                                    }, {
                                        mvpView?.showProgressOnAutoComplete(false)
                                        //todo: do something useful from here
                                        mvpView?.showError(it)
                                        Timber.e(it)
                                    })
                        })
            }
        }
    }

    fun getQuestions(questionName: String) {
        Timber.d("calling the api to get the questions with name $questionName")
    }

}