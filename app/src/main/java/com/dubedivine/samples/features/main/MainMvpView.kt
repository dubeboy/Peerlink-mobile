package com.dubedivine.samples.features.main

import com.dubedivine.samples.features.base.MvpView

interface MainMvpView : MvpView {

    fun showPokemon(pokemon: List<String>)

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showProgressOnAutoComplete(show: Boolean)
    fun  showSuggestions(charSequence: List<String>)

}