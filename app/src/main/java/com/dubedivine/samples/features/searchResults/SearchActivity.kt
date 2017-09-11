package com.dubedivine.samples.features.searchResults

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.ProgressBar
import butterknife.BindView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.main.MainActivity
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchMvpView, SearchAdapter.ClickListener {


    //todo: should not be repeationg all of this please, try to see wheather base activity can handle
    // all the common  injection i think that we should have another base baseActivity u feel!
    @Inject lateinit var searchPresenter: SearchPresenter
    @Inject lateinit var searchAdapter: SearchAdapter

    @BindView(R.id.view_error) @JvmField var mErrorView: ErrorView? = null
    @BindView(R.id.progress) @JvmField var mProgress: ProgressBar? = null
    @BindView(R.id.recycler_data) @JvmField var mSearchResultsRecycler: RecyclerView? = null
    @BindView(R.id.swipe_to_refresh) @JvmField var mSwipeRefreshLayout: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        searchPresenter.attachView(this)

        //todo repative code plase fix that is why we have dagger
        mSwipeRefreshLayout?.setProgressBackgroundColorSchemeResource(R.color.primary)
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.white)
        mSwipeRefreshLayout?.setOnRefreshListener { searchPresenter.getSearchResults(0) }

        searchAdapter.setClickListener(this)
        mSearchResultsRecycler?.layoutManager = LinearLayoutManager(this)
        mSearchResultsRecycler?.adapter = searchAdapter

    }

    override val layout: Int
        get() = R.layout.activity_search

    override fun onQuestionClick(question: Question) {
        Timber.i("the question: $question was clicked")
    }

    override fun showQuestionsSearchResults(questions: ArrayList<Question>) {
        searchAdapter.addQuestions(questions)
    }

}
