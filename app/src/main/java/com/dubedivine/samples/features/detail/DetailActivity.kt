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
import kotlinx.android.synthetic.main.activity_detail.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


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
   private lateinit var docsList: ArrayList<String>
    private lateinit var newFragment: AddFilesDialogFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mDetailPresenter.attachView(this)

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

        //listners
        btn_attach_files.setOnClickListener({
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val parentHasAnyChildren = add_q_linearlayout.childCount > 0
            newFragment = AddFilesDialogFragment.newInstance(parentHasAnyChildren, this)
            newFragment.show(ft, "dialog")

        })

        btn_answer_question.setOnClickListener({

        })
    }


    private fun createAlertDialogWithCustomView(alertDialogBuilder: AlertDialog.Builder) {

        alertDialogBuilder.show()
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

    override fun onItemClick(fileList: List<String>, type: Char) {
        lazy {
            docsList = ArrayList(fileList)
        }
        newFragment.dismiss()
        Log.d(TAG, "__onItemClick this has also been called $type and the fileList: $fileList")
        checkHoriElements()
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
                Log.d(AddQuestionActivity.TAG, "the path is ${fileList[0]}")
                val btnFile: CardView = BasicUtils.getFileViewInstance(this,
                        Media(file.name,
                                file.length(),
                                Media.VIDEO_TYPE,
                                file.absolutePath), {
                    Log.d(AddQuestionActivity.TAG, "the clicked file is $it")
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
                    Timber.i("Helllo the file has been selected $item")
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
    }

    private fun removeItem(item: String, it: View) {
        if (it.parent != null) {
            (it.parent as ViewGroup).removeView(it)
        }
        docsList.remove(item)
        checkHoriElements()
        removeItem(item, it)
    }

    private fun checkHoriElements() {
        if (add_q_linearlayout.childCount > 0) {
            hori_scroll_view.visibility = View.VISIBLE
        } else {
            hori_scroll_view.visibility = View.GONE
        }
    }





    companion object {

        val EXTRA_QUESTION = "EXTRA_QUESTION"
        val TAG = "__DetailAc"

        fun getStartIntent(context: Context, question: Question): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_QUESTION, question)
            return intent
        }
    }
}