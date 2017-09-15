package com.dubedivine.samples.features.main

import com.dubedivine.samples.R
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.detail.DetailActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.OnClick
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.SearchArrayAdapter
import com.dubedivine.samples.features.searchResults.SearchActivity
import com.dubedivine.samples.util.snack
import com.dubedivine.samples.util.toast
import timber.log.Timber
import javax.inject.Inject

class MainActivity :
        BaseActivity(),
        MainMvpView,
        PokemonAdapter.ClickListener,
        ErrorView.ErrorListener, SearchArrayAdapter.OnItemClickListener {


    @Inject lateinit var mPokemonAdapter: PokemonAdapter //The initialization of this one is injected here
    @Inject lateinit var mSearchArrayAdapter: SearchArrayAdapter
    @Inject lateinit var mMainPresenter: MainPresenter

    @BindView(R.id.view_error) @JvmField var mErrorView: ErrorView? = null
    @BindView(R.id.progress) @JvmField var mProgress: ProgressBar? = null
    @BindView(R.id.recycler_data) @JvmField var mPokemonRecycler: RecyclerView? = null
    @BindView(R.id.swipe_to_refresh) @JvmField var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.toolbar) @JvmField var mToolbar: Toolbar? = null
    @BindView(R.id.main_btn_search) @JvmField var mSearchButton: ImageButton? = null
    @BindView(R.id.main_auto_complete_input_search) @JvmField var mAutoCompleteSearchInputView: AutoCompleteTextView? = null
    @BindView(R.id.search_progress_bar) @JvmField var mSearchProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mMainPresenter.attachView(this)

        setSupportActionBar(mToolbar)

        mSwipeRefreshLayout?.setProgressBackgroundColorSchemeResource(R.color.primary)
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.white)
        mSwipeRefreshLayout?.setOnRefreshListener { mMainPresenter.getPokemon(POKEMON_COUNT) }

        mPokemonAdapter.setClickListener(this)
        mPokemonRecycler?.layoutManager = LinearLayoutManager(this)
        mPokemonRecycler?.adapter = mPokemonAdapter

        mErrorView?.setErrorListener(this)

        mMainPresenter.getPokemon(POKEMON_COUNT) // gets the pokemon !!!

//        mSearchButton?.setOnClickListener(this)  // no need butterKnife has my back

        //mAutoCompleteSearchInputView ----------------------------------------------------
        mSearchArrayAdapter.onItemClick = this
        //set the adapter for the auto complete search
        mAutoCompleteSearchInputView?.setAdapter(mSearchArrayAdapter)

        mAutoCompleteSearchInputView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                Timber.i("afterTextChanged:  the chars is [${text?.toString()}]")
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Timber.i("beforeTextChanged:  the chars is [${text?.toString()}]")

            }

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                //todo: this get the chars 2..infinity which is bad might have to use rxBind
                Timber.i("onTextChanged:  the chars is [$chars]")
                mMainPresenter.getSuggestions(chars)
            }
        })

        mAutoCompleteSearchInputView?.setOnItemClickListener({ adapterView, view, i, l ->
            Timber.e("$adapterView is $view $i $l ayoba$$$$$$$$$$$$$$$$$$$")
        })

    }

    override val layout: Int
        get() = R.layout.activity_main

    override fun onDestroy() {
        super.onDestroy()
        mMainPresenter.detachView()
    }

    //pveriden method from mvpView
    override fun showPokemon(pokemon: List<String>) {
        mPokemonAdapter.setPokemon(pokemon)
        mPokemonAdapter.notifyDataSetChanged()

        mPokemonRecycler?.visibility = View.VISIBLE
        mSwipeRefreshLayout?.visibility = View.VISIBLE
    }

    override fun showProgress(show: Boolean) {
        Timber.i("show progress is being called dwag for lo")
        if (show) {
            if (mPokemonRecycler?.visibility == View.VISIBLE && mPokemonAdapter.itemCount > 0) {
                mSwipeRefreshLayout?.isRefreshing = true
            } else {
                mProgress?.visibility = View.VISIBLE

                mPokemonRecycler?.visibility = View.GONE
                mSwipeRefreshLayout?.visibility = View.GONE
            }

            mErrorView?.visibility = View.GONE
        } else {
            mSwipeRefreshLayout?.isRefreshing = false
            mProgress?.visibility = View.GONE
        }
    }

    override fun showError(error: Throwable) {
        mPokemonRecycler?.visibility = View.GONE
        mSwipeRefreshLayout?.visibility = View.GONE
        mErrorView?.visibility = View.VISIBLE
        //if the keyboard is currently active just show toast just in case
        Toast.makeText(this, "There was an error", Toast.LENGTH_LONG).show()
        Timber.e(error, "There was an error retrieving the pokemon")
    }

    override fun onPokemonClick(pokemon: String) {
        startActivity(DetailActivity.getStartIntent(this, pokemon))
    }

    @OnClick(R.id.main_btn_search)
    fun onSearchButtonClick() {
        Timber.d("btn search clicked")
        if (!mAutoCompleteSearchInputView!!.text.toString().isNullOrBlank()) {
            startActivity(SearchActivity.getStartIntent(this, null,
                    mAutoCompleteSearchInputView!!.text.toString()))
        } else {
            snack("Please type something to search :)")  // I need to add emojies here!
        }

    }


    override fun onItemClick(question: Question) {
//        mMainPresenter.getQuestions(question)
        startActivity(SearchActivity.getStartIntent(this, question, mAutoCompleteSearchInputView!!.text.toString()))
    }


    override fun showProgressOnAutoComplete(show: Boolean) {
        if (show) {
            // todo: should add a spinner to the search
            // todo: should make the item un clickable
            mSearchProgressBar?.visibility = View.VISIBLE
            mSearchArrayAdapter.clear() // clear the available data and then just add one item that just says searching
            toast("Searching...", Toast.LENGTH_SHORT)
        } else {
            mSearchProgressBar?.visibility = View.GONE
            mSearchArrayAdapter.clear() // just make the adapter ready for more input
        }
    }

    override fun showSuggestions(question: List<Question>) {
        mSearchProgressBar?.visibility = View.GONE
        Timber.i("showSuggestions: is called with suggestions: $question")
        mSearchArrayAdapter.addAll(question)
    }

    override fun onReloadData() {
        mMainPresenter.getPokemon(POKEMON_COUNT)
    }

    companion object {

        private val POKEMON_COUNT = 20
    }
}

