package com.dubedivine.samples.features.searchResults
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.detail.DetailActivity
import com.dubedivine.samples.util.snack
import kotlinx.android.synthetic.main.content_fab_add.*
import timber.log.Timber
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchMvpView, SearchAdapter.ClickListener {

    //todo: should not be repeationg all of this please, try to see wheather base activity can handle
    // all the common  injection i think that we should have another base baseActivity u feel!
    @Inject lateinit var searchPresenter: SearchPresenter
    @Inject lateinit var searchAdapter: SearchAdapter
    @BindView(R.id.progress) @JvmField var mProgress: ProgressBar? = null
    @BindView(R.id.recycler_data) @JvmField var mSearchResultsRecycler: RecyclerView? = null
    @BindView(R.id.swipe_to_refresh) @JvmField var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    private var mQuestion: Question? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        searchPresenter.attachView(this)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mQuestion = intent.getSerializableExtra(SELECTED_TOP_QUESTION) as Question?
        val searchTerm = intent.getStringExtra(SEARCH_TERM)

        //todo relative code please fix that is why we have dagger
        mSwipeRefreshLayout?.setProgressBackgroundColorSchemeResource(R.color.primary)
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.white)
        mSwipeRefreshLayout?.setOnRefreshListener {
            searchAdapter.clear() // clear items and then try again
            searchPresenter.getSearchResults(searchTerm, 0)
        }

        searchPresenter.getSearchResults(searchTerm, 0)  // initialize the questions

        searchAdapter.setClickListener(this)

        mSearchResultsRecycler?.layoutManager = LinearLayoutManager(this)

        if (mQuestion != null) {
            searchAdapter.setTopQuestion(mQuestion!!)
        }
        mSearchResultsRecycler?.adapter = searchAdapter

        fab_add.setOnClickListener({
            startActivity(AddQuestionActivity.getStartIntent(this))
        })
    }

    override fun showProgress(show: Boolean) {
       if(show) mProgress?.visibility = View.VISIBLE else mProgress?.visibility = View.GONE
    }

    override fun showError(error: Throwable) {
        snack("An error happened sorry!")
        Timber.e(error)
    }

    override val layout: Int
        get() = R.layout.activity_search

    override fun onQuestionClick(question: Question) {
       startActivity(DetailActivity.getStartIntent(this, question))
    }

    override fun showQuestionsSearchResults(questions: List<Question>) {
        Timber.i("the passed questions from the presenter is: $questions")
        mProgress?.visibility = View.VISIBLE
        searchAdapter.addQuestions(questions)
        mProgress?.visibility = View.GONE
        mSwipeRefreshLayout?.isRefreshing = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        searchAdapter.clear();
    }

    override fun onDestroy() {
        super.onDestroy()
        searchPresenter.detachView()
    }

    companion object {

        val SELECTED_TOP_QUESTION = "selected_top_question"
        val SEARCH_TERM = "search_term"

        fun getStartIntent(context: Context, question: Question?, searchTerm: String): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(SELECTED_TOP_QUESTION , question)
            intent.putExtra(SEARCH_TERM, searchTerm)
            return intent
        }
    }
}
