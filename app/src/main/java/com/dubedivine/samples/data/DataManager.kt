package com.dubedivine.samples.data

import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.remote.MvpStarterService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton
import butterknife.OnClick



// singleton that manages data it just makes sense!! really it should because do u want duplicates of the same data??!!
@Singleton
class DataManager @Inject
constructor(private val mMvpStarterService: MvpStarterService) {

    val questions: ArrayList<Question> = ArrayList()

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
        val returnQuestions = mMvpStarterService.getSearchSuggestions(charSequence)
                .toObservable()
                .flatMapIterable { it }
                .map({ title -> title })
                .toList()

        returnQuestions.subscribeOn(Schedulers.computation()).subscribe(
                { questions.addAll(it) },
                { Timber.e("failed to add data to the questions array") })
        return returnQuestions
    }



}