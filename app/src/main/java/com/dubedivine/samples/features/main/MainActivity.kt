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
import timber.log.Timber
import javax.inject.Inject

class MainActivity :
        BaseActivity(),
        MainMvpView,
        PokemonAdapter.ClickListener,
        ErrorView.ErrorListener,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {


    @Inject lateinit var mPokemonAdapter: PokemonAdapter //The initialization of this one is injected here
    @Inject lateinit var mQuestionsSearchAdapter: QuestionsSearchAdapter
    @Inject lateinit var mMainPresenter: MainPresenter

    @BindView(R.id.view_error) @JvmField var mErrorView: ErrorView? = null
    @BindView(R.id.progress) @JvmField var mProgress: ProgressBar? = null
    @BindView(R.id.recycler_data) @JvmField var mPokemonRecycler: RecyclerView? = null
    @BindView(R.id.swipe_to_refresh) @JvmField var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.toolbar) @JvmField var mToolbar: Toolbar? = null
    @BindView(R.id.main_btn_search) @JvmField var mSearchButton: ImageButton? = null
    @BindView(R.id.main_auto_complete_input_search) @JvmField var mAutoCompleteSearchInputView: AutoCompleteTextView? = null

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

        mSearchButton?.setOnClickListener(this)

        //set the adapter for the auto complete search
        mAutoCompleteSearchInputView?.setAdapter(mQuestionsSearchAdapter)
        mAutoCompleteSearchInputView?.onItemSelectedListener = this
        mAutoCompleteSearchInputView?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.i("onTextChanged:  the chars is [$chars]")
                mMainPresenter.getSuggestions(chars)
            }
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

    override fun onClick(view: View?) {
        when (view?.id ) {
            R.id.main_btn_search -> {
                Timber.d("btn search clicked")
                mMainPresenter.getQuestions(mAutoCompleteSearchInputView?.text.toString()) // get question that match this term
            }
        }
    }

    //click listener for the item being selected from the drop down
    override fun onNothingSelected(adapterView: AdapterView<*>?) {
        Timber.d("onNothingSelected: nothing was selected yoh")
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = adapterView?.adapter?.getItem(position) as String
        Timber.d("onItemSelected: an item was selected a position %d and the value is %s",position ,
              selectedItem)
        mMainPresenter.getQuestions(selectedItem)
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
       Timber.i("onItemClick: Item was clicked fam  at %d", p2)
    }

    override fun showProgressOnAutoComplete(show: Boolean) {
        if(show) {
            // todo: should add a spinner to the search
            // todo: should make the item un clickable
            mQuestionsSearchAdapter.clear() // clear the available data and then just add one item that just says searching
            mQuestionsSearchAdapter.add("Searching...")
            mAutoCompleteSearchInputView?.onItemClickListener = null // so that when a person clik on the item disable the item
           // mAutoCompleteSearchInputView?.showDropDown()
        } else {
            mQuestionsSearchAdapter.clear() // just make the adapter ready for more input
            mAutoCompleteSearchInputView?.onItemClickListener = this
        }
    }

    override fun showSuggestions(charSequence: List<String>) {
        Timber.i("showSuggestions: is called with suggestions: $charSequence")
        mQuestionsSearchAdapter.addAll(charSequence)
    }


    override fun onReloadData() {
        mMainPresenter.getPokemon(POKEMON_COUNT)
    }

    companion object {

        private val POKEMON_COUNT = 20
    }
}

