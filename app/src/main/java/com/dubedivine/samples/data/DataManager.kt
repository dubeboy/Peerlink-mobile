package com.dubedivine.samples.data

import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.remote.MvpStarterService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


// singleton that manages data that
@Singleton
class DataManager @Inject
constructor(private val mMvpStarterService: MvpStarterService) {

    fun getPokemonList(limit: Int): Single<List<String>> {
        return mMvpStarterService.getPokemonList(limit)
                .toObservable()
                .flatMapIterable { (results) -> results } // returns an arbitary number of outputs but then its flat!
                .map { (name) -> name }
                .toList()
    }

    fun getPokemon(name: String): Single<Pokemon> {
        return mMvpStarterService.getPokemon(name)
    }

    fun getSuggestions(charSequence: CharSequence): Single<List<Question>>  {
        return mMvpStarterService.getSearchSuggestions(charSequence)
                .toObservable()
                .flatMapIterable { it }
                .map({title -> title})
                .toList()
    }

}