package com.dubedivine.samples.features.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Answer
import com.dubedivine.samples.data.model.Pokemon
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.Statistic
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.EndlessRecyclerViewScrollListener
import com.dubedivine.samples.features.common.ErrorView
//import kotlinx.android.synthetic.main.activity_detail.*
//import kotlinx.android.synthetic.main.content_error_and_progress_view.*
//import kotlinx.android.synthetic.main.content_swipe_refresh.*
import timber.log.Timber
import javax.inject.Inject


// in the future we should also get the question answers increnmentally so the
// serialized Question should contain a max of 5 children each comments and answers and then
// we lazily load the the other children

//todo: should only search when the text is not a empty(space) key
class DetailActivity : BaseActivity(), DetailMvpView, ErrorView.ErrorListener {

    @Inject lateinit var mDetailPresenter: DetailPresenter
    @Inject lateinit var mDetailAdapter: DetailAdapter

    @BindView(R.id.view_error) @JvmField var mErrorView: ErrorView? = null
    @BindView(R.id.progress)  @JvmField var mProgress: ProgressBar? = null
    @BindView(R.id.toolbar)  @JvmField  var mToolbar: Toolbar? = null
    @BindView(R.id.recycler_data)  @JvmField var mRecyclerData: RecyclerView? = null

    private var mQuestion: Question? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mDetailPresenter.attachView(this)

        mQuestion = intent.getSerializableExtra(EXTRA_QUESTION) as Question

        if (mQuestion == null) {
            throw IllegalArgumentException("Detail Activity requires a pokemon name@")
        }

        setSupportActionBar(mToolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        title = mQuestion!!.title

        mErrorView!!.setErrorListener(this)
        //  mDetailPresenter.getPokemon(mQuestion)

        mDetailAdapter.mQuestion = mQuestion!!
//        mDetailAdapter.
        val layoutManager = LinearLayoutManager(this)
        mRecyclerData!!.layoutManager = layoutManager
        val scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                //call the API here to add more answers to the question boss
                if (page > 0)
                    mDetailPresenter.getMoreAnswers(mQuestion!!.id!!, page)
            }
        }


        mRecyclerData!!.adapter = mDetailAdapter
    }


    override val layout: Int
        get() = R.layout.activity_detail

    override fun showQuestionsAndAnswers(pokemon: Pokemon) {
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
        error.printStackTrace()
    }

    override fun onReloadData() {
        // mDetailPresenter.getPokemon(mQuestion as String)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDetailPresenter.detachView()
    }

    override fun addAnswers(answer: List<Answer>) {

    }


    companion object {

        val EXTRA_QUESTION = "EXTRA_QUESTION"

        fun getStartIntent(context: Context, question: Question): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_QUESTION, question)
            return intent
        }
    }
}