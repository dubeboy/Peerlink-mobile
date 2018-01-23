package com.dubedivine.samples.features.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.*
import com.dubedivine.samples.features.addQuestion.AddQuestionActivity
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.common.EndlessRecyclerViewScrollListener
import com.dubedivine.samples.features.common.ErrorView
import com.dubedivine.samples.features.detail.dialog.AddFilesDialogFragment
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.log
import com.dubedivine.samples.util.snack
import com.dubedivine.samples.util.toast
import kotlinx.android.synthetic.main.activity_detail.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


// in the future we should also get the question answers increnmentally so the
// serialized Question should contain a max of 5 children each comments and answers and then
// we lazily load the the other children
//todo look into the boom menu: https://github.com/Nightonke/BoomMenu
//todo: should only search when the text is not a empty(space) key
class DetailActivity : BaseActivity(), DetailMvpView, ErrorView.ErrorListener, AddFilesDialogFragment.OnItemClick {


    @Inject lateinit var mDetailPresenter: DetailPresenter
    @Inject lateinit var mDetailAdapter: DetailAdapter

    @BindView(R.id.view_error)
    @JvmField
    var mErrorView: ErrorView? = null
    @BindView(R.id.progress)
    @JvmField
    var mProgress: ProgressBar? = null
    @BindView(R.id.toolbar)
    @JvmField
    var mToolbar: Toolbar? = null
    @BindView(R.id.recycler_data)
    @JvmField
    var mRecyclerData: RecyclerView? = null


    private var mQuestion: Question? = null
    private val docsList: HashSet<String> = HashSet(0)//not sure weather there should be 0 , 1 or default init capacity, 0 for now
    private lateinit var newFragment: AddFilesDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mDetailPresenter.attachView(this)
        // todo: should look into delegate and lazy loading so that this is only create if the user wants to send file and its created once
        newFragment = AddFilesDialogFragment.newInstance(this)
        // newFragment.retainInstance = true // how do i manually kill it, onDetach?
//        val alertDialogBuilder = AlertDialog.Builder(this)
//        alertDialogBuilder.setTitle("Attach photos, videos or files ")
//        val view =  layoutInflater.inflate(R.layout.alert_dialog_select_files, null)
//        alertDialogBuilder.setView(view)
//        setUpOnClickListenersForAlertButtons(view)
//        alertDialogBuilder.create()

        val q = Question("Hello", "what does hello mean", 100, listOf<Tag>(Tag("hello", Date()), Tag("hello", Date())), "Q")
        q.id = "29292929"
//        mQuestion = intent.getSerializableExtra(EXTRA_QUESTION) as Question
        mQuestion = q

        if (mQuestion == null) {
            throw IllegalArgumentException("Detail Activity requires a Question Instance")
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
        mRecyclerData!!.addOnScrollListener(scrollListener)

        //create a dialog that shows the files to attach
        btn_attach_files.setOnClickListener({
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            // Create and show the dialog.
            newFragment.show(ft, "dialog")

        })

        btn_answer_question.setOnClickListener({
            val answer = et_answer_input.text

            if (answer.isBlank()) {
                snack("incorrect answer!")
            } else {
                Log.d(TAG, "posting answer and the posted files are $docsList")

                mDetailPresenter.addAnswer(answer.toString(), answer.toString() , docsList)
            }
        })
    }

    override fun showUserError(error: String) {
       toast(error)
    }

    override fun showAnswer(entity: Answer) {
        mDetailAdapter.addAnswer(entity)
    }
//    private fun setUpOnClickListenersForAlertButtons(view: View) {
//        val btnAttachPhotos = view.findViewById<FloatingActionButton>(R.id.btn_attach_photos)
//        val btnAttachVideos = view.findViewById<FloatingActionButton>(R.id.btn_attach_video)
//        val btnAttachFiles = view.findViewById<FloatingActionButton>(R.id.btn_attach_filez)
//
//        btnAttachPhotos.setOnClickListener({
//
//            FilePickerBuilder.getInstance()
//                    .setMaxCount(10)
//                    .setActivityTheme(R.style.AppTheme)
//                    .pickPhoto(this)
//        })
//
//        btnAttachVideos.setOnClickListener({
//            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//            if (takeVideoIntent.resolveActivity(packageManager) != null) {
//                Log.d(AddQuestionActivity.TAG, " the data is ${takeVideoIntent.data}")
//                startActivityForResult(takeVideoIntent, AddQuestionActivity.REQUEST_VIDEO_CAPTURE)
//            }
//        })
//
//        btnAttachFiles.setOnClickListener({
//            FilePickerBuilder.getInstance()
//                    .setMaxCount(10)
//                    .setActivityTheme(R.style.AppTheme)
//                    .pickFile(this)
//        })
//    }
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
        toast("Oops could not connect to the internet")
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
        // todo: implement this
    }

    //onclick event from the add files dialog fragment which gives this parent the required parameters to complete the task
    override fun onItemClick(fileList: List<String>, type: Char) {
        docsList.addAll(fileList)
        newFragment.dismiss()
        Log.d(TAG, "__onItemClick this has also been called $type and the fileList: $fileList")
        when (type) {
            Media.PICTURE_TYPE -> {
                fileList.forEach { item ->
                    val imagePreviewInstance = BasicUtils.getImagePreviewInstance(this, item,
                     {
                        removeItem(item, it)
                    })
                    add_q_linearlayout.addView(imagePreviewInstance)
                }
            }
            Media.VIDEO_TYPE -> {
                val file = File(fileList[0])
                Log.d(TAG, "the path is ${fileList[0]}")
                val btnFile: CardView = BasicUtils.getFileViewInstance(this,
                        Media(file.name,
                                file.length(),
                                Media.VIDEO_TYPE,
                                file.absolutePath), {
                    Log.d(TAG, "the clicked file is $it")
//                            val bottomSheetDialogFragment = VideoViewFragment.newInstance(vid_url.path)
//                            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
                    val vidIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fileList[0]))
                    vidIntent.setDataAndType(Uri.parse(fileList[0]), "video/*")
                    startActivity(vidIntent)
                }, {
                    removeItem(fileList[0], it)
                })

                add_q_linearlayout.addView(btnFile)
            }
            Media.DOCS_TYPE -> {
                fileList.forEach { item ->
                    Timber.i("Hello the file has been selected $item")
                    val fileViewInstance = BasicUtils.getFileViewInstance(this,
                            Media(item.substringAfterLast("/"), 0, Media.DOCS_TYPE, item),
                            { _ -> },
                            {
                              removeItem(item, it)
                            }
                    )
                    add_q_linearlayout.addView(fileViewInstance)
                }
            }
        }
        checkHoriElements()
    }

    private fun removeItem(item: String, it: View) {
        if (it.parent != null) {
            (it.parent as ViewGroup).removeView(it)
        }
        docsList.remove(item)
        checkHoriElements()
    }

    private fun checkHoriElements() {
        if (add_q_linearlayout.childCount > 0) {
            hori_scroll_view.visibility = View.VISIBLE
        } else {
            hori_scroll_view.visibility = View.GONE
            newFragment.enableAllButtons()
        }
    }

    companion object {

        private const val EXTRA_QUESTION = "EXTRA_QUESTION"
        const val TAG = "__DetailAc"

        fun getStartIntent(context: Context, question: Question): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_QUESTION, question)
            return intent
        }
    }
}