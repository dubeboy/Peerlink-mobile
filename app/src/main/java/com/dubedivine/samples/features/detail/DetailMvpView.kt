package com.dubedivine.samples.features.detail

import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.Statistic
import com.dubedivine.samples.features.base.MvpView

interface DetailMvpView : MvpView {

    fun showPokemon(pokemon: Pokemon)

    fun showStat(statistic: Statistic)


}