package com.dubedivine.samples.features.main

import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.injection.ConfigPersistent
import com.dubedivine.samples.features.base.BasePresenter
import com.dubedivine.samples.util.rx.scheduler.SchedulerUtils
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

}