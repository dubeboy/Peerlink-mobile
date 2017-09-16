package com.dubedivine.samples.features.detail

import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.Statistic
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.detail.widget.StatisticView
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.content_error_and_progress_view.*
import kotlinx.android.synthetic.main.content_swipe_refresh.*
import timber.log.Timber
import javax.inject.Inject


// in the future we should also get the question answers increnmentally so the
// serialized Question should contain a max of 5 children each comments and answers and then
// we lazly load the the other children
class DetailActivity : BaseActivity(), DetailMvpView, ErrorView.ErrorListener {

     @Inject lateinit var mDetailPresenter: DetailPresenter

     @JvmField var mErrorView: ErrorView = view_error
     @JvmField var mProgress: ProgressBar = progress
     @JvmField var mToolbar: Toolbar = toolbar
     @JvmField var mRecyclerData: RecyclerView = recycler_data

    private var mPokemonName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mDetailPresenter.attachView(this)

        mPokemonName = intent.getStringExtra(EXTRA_POKEMON_NAME)
        if (mPokemonName == null) {
            throw IllegalArgumentException("Detail Activity requires a pokemon name@")
        }

        setSupportActionBar(mToolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        title = mPokemonName?.substring(0, 1)?.toUpperCase() + mPokemonName?.substring(1)

        mErrorView.setErrorListener(this)

        mDetailPresenter.getPokemon(mPokemonName as String)
    }

    override val layout: Int
        get() = R.layout.activity_detail

    override fun showQuestionsAndAnswers(pokemon: Pokemon) {

        //-> we populate the recycler view and
        // -> show that data placing the question as the first item
//        if (pokemon.sprites.frontDefault != null) {
//            Glide.with(this)
//                    .load(pokemon.sprites.frontDefault)
//                    .into(mPokemonImage)
//        }
//        mPokemonLayout.visibility = View.VISIBLE
    }

    override fun showStat(statistic: Statistic) {
//        val statisticView = StatisticView(this)
//        statisticView.setStat(statistic)
//        mStatLayout?.addView(statisticView)
    }

    override fun showProgress(show: Boolean) {
        mErrorView?.visibility = View.GONE
        mProgress?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showError(error: Throwable) {
//        mPokemonLayout?.visibility = View.GONE
//        mErrorView?.visibility = View.VISIBLE
        Timber.e(error, "There was a problem retrieving the pokemon...")
    }

    override fun onReloadData() {
        mDetailPresenter.getPokemon(mPokemonName as String)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDetailPresenter.detachView()
    }

    companion object {

        val EXTRA_POKEMON_NAME = "EXTRA_POKEMON_NAME"

        fun getStartIntent(context: Context, pokemonName: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_POKEMON_NAME, pokemonName)
            return intent
        }
    }
}