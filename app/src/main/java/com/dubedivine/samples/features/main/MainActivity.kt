package com.dubedivine.samples.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import butterknife.BindView
import com.bumptech.glide.Glide
import com.dubedivine.samples.R
import com.dubedivine.samples.data.local.PreferencesHelper
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.SearchArrayAdapter
import com.dubedivine.samples.features.main.fragment.subscribe.TagsSubscribedFragment
import com.dubedivine.samples.features.main.fragment.WelcomeFragment
import com.dubedivine.samples.features.searchResults.SearchActivity
import com.dubedivine.samples.features.signIn.SignIn
import com.dubedivine.samples.features.signIn.SignInMoreDetails
import com.dubedivine.samples.util.snack
import com.dubedivine.samples.util.toast
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import javax.inject.Inject


//todo we should have better font thinner font please

class MainActivity :
        BaseActivity(),
        MainMvpView,
        PokemonAdapter.ClickListener,
        SearchArrayAdapter.OnItemClickListener {

    @Inject
    lateinit var mSearchArrayAdapter: SearchArrayAdapter
    @Inject
    lateinit var mMainPresenter: MainPresenter

    @BindView(R.id.progress)
    @JvmField
    var mProgress: ProgressBar? = null
    @BindView(R.id.swipe_to_refresh)
    @JvmField
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @BindView(R.id.toolbar)
    @JvmField
    var mToolbar: Toolbar? = null
    @BindView(R.id.main_auto_complete_input_search)
    @JvmField
    var mAutoCompleteSearchInputView: AutoCompleteTextView? = null
    @BindView(R.id.search_progress_bar)
    @JvmField
    var mSearchProgressBar: ProgressBar? = null

    private lateinit var mPreferencesHelper: PreferencesHelper
    private var hasBackBeenPressed: Boolean = false


    override val layout: Int
        get() = R.layout.activity_main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mMainPresenter.attachView(this)
        setSupportActionBar(mToolbar)
        // add some bread crumbs!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        //instantiate our prefs helper class
        mPreferencesHelper = PreferencesHelper(this)
       // FirebaseMessaging.getInstance().subscribeToTopic("Peerlink")

        // start the sign in activity if the user is not signed in
        checkIfUserSignedUp()
        mMainPresenter.pushFCMToken()
        setupSideNavigation()


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



        mAutoCompleteSearchInputView?.setOnItemClickListener { adapterView, view, i, l ->
            Timber.e("$adapterView is $view $i $l ayoba$$$$$$$$$$$$$$$$$$$")
        }

        // setup the main action bar search button
        main_btn_search.setOnClickListener({
            onSearchButtonClick()
        })

        // initially the fragment that is there is the main fragment with the details about the app
        addFragment(savedInstanceState, 1)


        nav_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    addFragment(savedInstanceState, 1)
                }
                R.id.nav_interests -> {
                    addFragment(savedInstanceState, 2)
                }
                R.id.nav_trending -> {
                    addFragment(savedInstanceState, 3)
                }
                R.id.nav_logout -> {
                    mPreferencesHelper.clear()
                    checkIfUserSignedUp()
                    snack("Successfully Logged out.")
                }
            }
            // close drawer when item is tapped
            drawer_layout.closeDrawers()
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }
    }

    private fun setupSideNavigation() {
        val headerView = nav_view.getHeaderView(0)
        val tvUserName: TextView = headerView.findViewById(R.id.tv_user_name)
        val tvUserEmail: TextView = headerView.findViewById(R.id.tv_user_email)
        val tvUserDegree: TextView = headerView.findViewById(R.id.tv_user_degree)
        val tvUserUserProfilePhoto: ImageView = headerView.findViewById(R.id.img_user_profile)

        tvUserName.text = mPreferencesHelper.getString(SignInMoreDetails.P_NICKNAME)
        tvUserEmail.text = mPreferencesHelper.getString(SignInMoreDetails.P_EMAIL)
        tvUserDegree.text = mPreferencesHelper.getString(SignInMoreDetails.P_DEGREE)

        val photoUrl = mPreferencesHelper.getString(SignInMoreDetails.P_PHOTO_URL)
        Timber.d("the pic url is $photoUrl")
        Glide.with(this)
                .load(photoUrl)
                .into(tvUserUserProfilePhoto)
    }


    private fun isUserLoggedIn(): Boolean {
        return mPreferencesHelper.getString(SignInMoreDetails.P_EMAIL).isBlank()
    }

    private fun checkIfUserSignedUp() {
        if (isUserLoggedIn()) {
            startActivity(SignIn.getStartIntent(this))
        }
    }

    private fun addFragment(savedInstanceState: Bundle?, num: Int) {
        //this is when we are rotating the screen bruv so that we dont overlapt the fragments
        if (savedInstanceState != null) return
        val transaction = supportFragmentManager.beginTransaction()
        when (num) {
            1 -> {
                transaction.replace(R.id.fragment_framelayout, WelcomeFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            2 -> {
                transaction.replace(R.id.fragment_framelayout, TagsSubscribedFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            3 -> {
                transaction.replace(R.id.fragment_framelayout, TagsSubscribedFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    // when another activity come into the foreground
    // we reset the state that the back was ever pressed
    override fun onPause() {
        super.onPause()
        hasBackBeenPressed = false
    }


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

    override fun onBackPressed() {

        if (isUserLoggedIn()) {
            if (!hasBackBeenPressed) {
                toast("press again to exit")
                hasBackBeenPressed = true
                return
            }
        }
        // go back as normal
        super.onBackPressed()
    }

    //pveriden method from mvpView
    override fun showPokemon(pokemon: List<String>) {
//        mPokemonAdapter.setPokemon(pokemon)
//        mPokemonAdapter.notifyDataSetChanged()
//
//        mPokemonRecycler?.visibility = View.VISIBLE
//        mSwipeRefreshLayout?.visibility = View.VISIBLE
    }

    override fun showProgress(show: Boolean) {
        Timber.i("show progress is being called dwag for lo")
//        if (show) {
//            if (mPokemonRecycler?.visibility == View.VISIBLE && mPokemonAdapter.itemCount > 0) {
//                mSwipeRefreshLayout?.isRefreshing = true
//            } else {
//                mProgress?.visibility = View.VISIBLE
//
//                mPokemonRecycler?.visibility = View.GONE
//                mSwipeRefreshLayout?.visibility = View.GONE
//            }
//
//        } else {
        mSwipeRefreshLayout?.isRefreshing = false
        mProgress?.visibility = View.GONE
//        }
    }

    override fun showError(error: Throwable) {
//        mPokemonRecycler?.visibility = View.GONE
        mSwipeRefreshLayout?.visibility = View.GONE
        //if the keyboard is currently active just show toast just in case
        Toast.makeText(this, "There was an error", Toast.LENGTH_LONG).show()
        Timber.e(error, "There was an error retrieving the pokemon")
    }

    override fun onPokemonClick(pokemon: String) {
        // startActivity(DetailActivity.getStartIntent(this, pokemon))
    }

    fun onSearchButtonClick() {
        Timber.d("btn search clicked")
        if (mAutoCompleteSearchInputView!!.text.toString().isNotBlank()) {
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

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

