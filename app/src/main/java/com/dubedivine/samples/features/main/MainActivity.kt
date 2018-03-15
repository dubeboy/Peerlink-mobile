package com.dubedivine.samples.features.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.dubedivine.samples.R
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.common.SearchArrayAdapter
import com.dubedivine.samples.features.searchResults.SearchActivity
import com.dubedivine.samples.features.signIn.SignIn
import com.dubedivine.samples.util.snack
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


//todo we should have better font thinner font please

class MainActivity :
        BaseActivity(),
        MainMvpView,
        PokemonAdapter.ClickListener,
        ErrorView.ErrorListener, SearchArrayAdapter.OnItemClickListener {


    @Inject
    lateinit var mPokemonAdapter: PokemonAdapter //The initialization of this one is injected here
    @Inject
    lateinit var mSearchArrayAdapter: SearchArrayAdapter
    @Inject
    lateinit var mMainPresenter: MainPresenter

    @BindView(R.id.view_error)
    @JvmField
    var mErrorView: ErrorView? = null
    @BindView(R.id.progress)
    @JvmField
    var mProgress: ProgressBar? = null
    @BindView(R.id.recycler_data)
    @JvmField
    var mPokemonRecycler: RecyclerView? = null
    @BindView(R.id.swipe_to_refresh)
    @JvmField
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.toolbar)
    @JvmField
    var mToolbar: Toolbar? = null
    @BindView(R.id.main_btn_search)
    @JvmField
    var mSearchButton: ImageButton? = null
    @BindView(R.id.main_auto_complete_input_search)
    @JvmField
    var mAutoCompleteSearchInputView: AutoCompleteTextView? = null
    @BindView(R.id.search_progress_bar)
    @JvmField
    var mSearchProgressBar: ProgressBar? = null

    private lateinit var mPreferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mMainPresenter.attachView(this)

        setSupportActionBar(mToolbar)

        // add some bread crumbs
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        //instantiate our prefs helper class
        mPreferencesHelper = PreferencesHelper(this)

        // start the sign in activity if the user is not signed in
        checkIfUserSignedUp()

        mSwipeRefreshLayout?.setProgressBackgroundColorSchemeResource(R.color.primary)
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.white)
        mSwipeRefreshLayout?.setOnRefreshListener { mMainPresenter.getPokemon(POKEMON_COUNT) }

        //mAutoCompleteSearchInputView ----------------------------------------------------
        mSearchArrayAdapter.onItemClick = this
        //set the adapter for the auto complete search
        mAutoCompleteSearchInputView?.setAdapter(mSearchArrayAdapter)

        mAutoCompleteSearchInputView?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                //todo: this get the chars 2..infinity which is bad might have to use rxBind
                mMainPresenter.getSuggestions(chars)
            }
        })

        mAutoCompleteSearchInputView?.setOnItemClickListener({ adapterView, view, i, l ->
            Timber.e("$adapterView is $view $i $l ayoba$$$$$$$$$$$$$$$$$$$")
        })

        nav_view.setNavigationItemSelectedListener(
                object : NavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true)
                        // close drawer when item is tapped
                        drawer_layout.closeDrawers()

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true
                    }
                })


    }

    private fun checkIfUserSignedUp() {
        if (mPreferencesHelper.getString(SignIn.P_EMAIL).isBlank()) {
          startActivity(SignIn.getStartIntent(this))
        }
    }

    override val layout: Int
        get() = R.layout.activity_main

    override fun onDestroy() {
        super.onDestroy()
        mMainPresenter.detachView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        // startActivity(DetailActivity.getStartIntent(this, pokemon))
    }

    @OnClick(R.id.main_btn_search)
    fun onSearchButtonClick() {
        Timber.d("btn search clicked")
        if (!mAutoCompleteSearchInputView!!.text.toString().isBlank()) {
            startActivity(SearchActivity.getStartIntent(this, null,
                    mAutoCompleteSearchInputView!!.text.toString()))
        } else {
            snack("Please type something to search :)")  // I need to add emojies here!
        }

    }


    // click event from the searchAdapter
    override fun onItemClick(question: Question) {
        startActivity(SearchActivity.getStartIntent(this, question, mAutoCompleteSearchInputView!!.text.toString()))
    }


    override fun showProgressOnAutoComplete(show: Boolean) {
        if (show) {
            // todo: should add a spinner to the search
            // todo: should make the item un clickable
            mSearchProgressBar?.visibility = View.VISIBLE
            mSearchArrayAdapter.clear() // clear the available data and then just add one item that just says searching
            // toast("Searching...", Toast.LENGTH_SHORT)
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

