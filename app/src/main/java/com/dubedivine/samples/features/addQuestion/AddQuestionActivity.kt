package com.dubedivine.samples.features.addQuestion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.snack
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_question.*
import kotlinx.android.synthetic.main.content_fab_add.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import com.flipboard.bottomsheet.BottomSheetLayout




// todo: this class breaks the constency rull one its not using timber!!
// and it not using ButterKnife myabe there shold be a revolution
class AddQuestionActivity : BaseActivity(), AddQuestionMvpView {

    @Inject lateinit var mAddQuestionPresenter: AddQuestionPresenter

    lateinit var tagsSuggestionsAdapter: ArrayAdapter<String>
    private var tagsSuggestionsView: View? = null
    private var tagsSuggestionListView: ListView? = null
    lateinit var popUpWindow: PopupWindow


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mAddQuestionPresenter.attachView(this)
        //fab_add set the drawable here of a tick

        Log.d(TAG, "guys you are awesome")
        Timber.i(TAG, "Timber is not so awesome")

        tagsSuggestionsAdapter = ArrayAdapter(this@AddQuestionActivity,
                android.R.layout.simple_spinner_item)

        tagsSuggestionsView = getInflatedTagsSuggestionView()
        tagsSuggestionListView = tagsSuggestionsView?.findViewById(R.id.tags_suggestion_listview)
        popUpWindow = PopupWindow(this)
        configurePopUpWindow(popUpWindow, tagsSuggestionsView!!)
        fab_add.setOnClickListener({
            // save data on server here
        })

        q_add.addTextChangedListener(object : TextWatcher {  // its a singleton so good
            var tagStartIndex = 0 // assume that the tag starts at 0
            var tagStopIndex = 0 // assume that the tag stop at 0 meaning no tag, basic math!! hahaha
            var isTypingHashTag = false //initally assume the the person is  typing any hash tags

            override fun afterTextChanged(afterChange: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            // all of these computation should be done on by the presenter a so that we just display the results
            override fun onTextChanged(text: CharSequence?, numChars: Int, p2: Int, p3: Int) {
                text.let {
                    if (text!!.isNotBlank()) {
                        if (text.last() == '#') {
                            Log.d(TAG, "start typing hash tag bro: $text")
                            isTypingHashTag = true
                            tagStartIndex = numChars
                        }
                        if (text.last() == ' ') {
                            Log.d(TAG, "we have stopped typing the tags $text")
                            isTypingHashTag = false
                            tagStopIndex = numChars
                            popUpWindow.dismiss()   // dismiss the popup window here
                        }
                        if (isTypingHashTag) {
                            Log.d(TAG, "this is the has tag dwag $text")
                            //get the tags suggestion here
                            if (numChars >= tagStartIndex) {
                                val tag = text.subSequence(tagStartIndex - 1, text.lastIndex + 1)
                                Log.d(TAG, "the actual tag is: $tag")
                                mAddQuestionPresenter.getTagSuggestion(tag, tagStartIndex, text.lastIndex + 1)
                            }
                        } else {
                            Log.d(TAG, "this is the normal text: $text")
                            // stop the suggestion or disconnect the adapter what you think!
                        }
                    }
                }
            }
        })

        btn_add_files.setOnClickListener({
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickFile(this)
        })

        btn_add_video.setOnClickListener({
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takeVideoIntent.resolveActivity(packageManager) != null) {
                Log.d(TAG, " the data is ${takeVideoIntent.data}")
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        })

        btn_add_picture.setOnClickListener({
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this)
        })
    }

    override val layout: Int
        get() = R.layout.activity_add_question

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "the user has been destroyed boss")
    }

    //https://stackoverflow.com/questions/23464232/how-would-you-create-a-popover-view-in-android-like-facebook-comments
    override fun showTagsSuggestion(tags: List<Tag>,
                                    typedWord: CharSequence,
                                    tagStartIndex: Int,
                                    tagStopIndex: Int) {

        tagsSuggestionsAdapter.clear()
        for (tag in tags) {
            tagsSuggestionsAdapter.add(tag.name)
        }
        tagsSuggestionListView?.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapter: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val wordToReplace = adapter?.getItemAtPosition(position) as String
                q_add.text.replace(tagStartIndex, tagStopIndex, "#$wordToReplace ")
                popUpWindow.dismiss()
            }
        }
        tagsSuggestionListView?.adapter = tagsSuggestionsAdapter
        val (x, y) = getTheXAndYForWord(q_add, typedWord)
        Log.d(TAG, "the x and y are  ($x, $y)")
        popUpWindow.showAtLocation(q_add, Gravity.TOP, x, y)
    }

    override fun showProgress(show: Boolean) {
        snack("showing progress $show")
    }

    override fun showError(error: Throwable) {
        snack("showing error")
        error.printStackTrace()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        Log.d(TAG, "the result is ${intent?.data}")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_VIDEO_CAPTURE -> {
                    if (intent?.data != null) {
                        val vid_url = intent.data!!
                        // q_vid.setVideoURI(intent.data!!)
                        Log.d(TAG, "Initializing Video media")
                        val file = File(vid_url.path)
                        val btnFile: Button = BasicUtils.getFileViewInstance(this,
                                Media(file.name,
                                        file.length(),
                                        Media.VIDEO_TYPE,
                                        file.absolutePath), {
                            Log.d(TAG, "the clicked file is $it")
                            val bottomSheet = findViewById(R.id.bottomsheet) as BottomSheetLayout
                            bottomSheet.showWithSheetView(LayoutInflater.from(this@AddQuestionActivity).inflate(R.layout.fragment_video_view, bottomSheet, false));
                        }, {
                            add_q_linearlayout.removeView(it)
                            //count the children of this layout if 0 ena all enable other views
                        })
                        add_q_linearlayout.addView(btnFile)
                        // count children of this layout its 0 enable the and then disable
                    }
                }
                FilePickerConst.REQUEST_CODE_PHOTO -> {
                    if (intent != null) {
                        val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                        Log.d(TAG, "the data that we got: Photos ${photosPaths}")
                    }
                }
                FilePickerConst.REQUEST_CODE_DOC -> {
                    if (intent != null) {
                        val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                        Log.d(TAG, "the data that we got ${intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)}")
                    }
                }

            }

        }
    }


    //--------------------------private methods ------------------------
    private fun configurePopUpWindow(popUpWindow: PopupWindow, tagsSuggestionsView: View): PopupWindow {
        popUpWindow.contentView = tagsSuggestionsView
        popUpWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popUpWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popUpWindow.isFocusable = false
        popUpWindow.isOutsideTouchable = true
//        popUpWindow.setBackgroundDrawable((android.R.drawable.spinner_dropdown_background))

        return popUpWindow
    }

    private fun getTheXAndYForWord(textView: EditText, text: CharSequence): Pair<Int, Int> {
        val bounds = Rect()
        val tpaint = textView.paint
        tpaint.getTextBounds(text.toString(), 0, text.length, bounds)
        val height = bounds.height()
        val width = bounds.width()
        return Pair(width, height)
    }


    private fun getDisplay(): Point {
        val display = this.windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        return point

    }

    private fun getInflatedTagsSuggestionView(): View? {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.activity_popup_tag_suggestion, null)
    }

    companion object {
        val TAG = "__AddQuestionActivity"
        val REQUEST_VIDEO_CAPTURE = 1
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, AddQuestionActivity::class.java)
            return intent
        }
    }
}
