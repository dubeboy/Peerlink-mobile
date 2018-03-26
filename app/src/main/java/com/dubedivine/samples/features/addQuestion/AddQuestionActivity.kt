package com.dubedivine.samples.features.addQuestion

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.features.detail.DetailActivity
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.BasicUtils.getRealPathFromURI
import com.dubedivine.samples.util.snack
import com.dubedivine.samples.util.toast
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_add_question.*
import kotlinx.android.synthetic.main.content_fab_add.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject


//todo: this class breaks the consistency rule one its not using timber!!
//todo : should the question title have suggestions?
//and it not using ButterKnife myabe there shold be a revolution
class AddQuestionActivity : BaseActivity(), AddQuestionMvpView {

    @Inject lateinit var mAddQuestionPresenter: AddQuestionPresenter

    private lateinit var tagsSuggestionsAdapter: ArrayAdapter<String>
    private var tagsSuggestionsView: View? = null
    private var tagsSuggestionListView: ListView? = null
    private lateinit var popUpWindow: PopupWindow
    private var mediaFiles: Map<Char, List<String>>? = null //Maps media type to Files
    private lateinit var prog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mAddQuestionPresenter.attachView(this)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        checkPermissions()
        tagsSuggestionsAdapter = ArrayAdapter(this@AddQuestionActivity,
                android.R.layout.simple_spinner_item)

        tagsSuggestionsView = getInflatedTagsSuggestionView()
        tagsSuggestionListView = tagsSuggestionsView?.findViewById(R.id.tags_suggestion_listview)
        popUpWindow = PopupWindow(this) //https://www.google.com/search?client=ubuntu&channel=fs&q=android+popup+window+relative+to+the+coursor&ie=utf-8&oe=utf-8
        configurePopUpWindow(popUpWindow, tagsSuggestionsView!!)

        prog = ProgressDialog(this)

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
                                val tag = text.subSequence(tagStartIndex, text.lastIndex + 1)
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

        btn_add_files.setOnClickListener {

            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .setActivityTheme(R.style.AppTheme)
                    .pickFile(this)

        }

        btn_add_video.setOnClickListener {
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (takeVideoIntent.resolveActivity(packageManager) != null) {
                Log.d(TAG, " the data is ${takeVideoIntent.data}")
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }

        btn_add_picture.setOnClickListener {
            FilePickerBuilder.getInstance()
                    .setMaxCount(10)
                    .showGifs(true)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this)
        }

        fab_add.setOnClickListener {
            publishNewQuestion()
        }
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
        tagsSuggestionListView?.onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
            val wordToReplace = adapter?.getItemAtPosition(position) as String
            //should do a check here
            q_add.text.replace(tagStartIndex, tagStopIndex, "#$wordToReplace ") // there is a problem here
            popUpWindow.dismiss()
        }
        tagsSuggestionListView?.adapter = tagsSuggestionsAdapter
        val (x, y) = getTheXAndYForWord(q_add, typedWord)
        Log.d(TAG, "the x and y are  ($x, $y)")
        popUpWindow.showAtLocation(q_add, Gravity.TOP, x, y)
    }

    private fun getProgressBarInstance(title: String, msg: String): ProgressDialog {
        prog.setMessage(msg)
        prog.setTitle(title)
        return prog
    }

    override fun showProgress(show: Boolean) {
        val prog = getProgressBarInstance("Asking question", "Please wait asking question...")
        if (show) prog.show() else prog.dismiss()
    }

    override fun showProgress(show: Boolean, msg: String) {
        val prog = getProgressBarInstance("Attaching files", "Now saving attached question file(s)., just a sec...")
        if (show) prog.show() else prog.dismiss()
    }

    override fun showQuestion(entity: Question) {
        val intent = DetailActivity.getStartIntent(this, entity)
        startActivity(intent)
    }

    override fun showTagSuggestionProgress(show: Boolean) {
        toast("Suggesting tags")
    }

    override fun showError(error: Throwable) {
        if (error.message != null) {
            snack(error.message!!)
        }
        error.printStackTrace()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        Log.d(TAG, "the result is ${intent?.data}")
        if (resultCode == Activity.RESULT_OK) {
            if (add_q_linearlayout.childCount == 0) { //enable all the button if the item are removed to 0 meaning here in the following switches we modify this state
                enableAddButtons(isPic = true, isVid = true, isFiles = true)
                q_add.hint = "You can just type your question here, like you do on google, no need to greet we are all here to get answers :)"
            }
            processTheInputRequest(requestCode, intent)
        }
    }

    //--------------------------private methods ------------------------
    private fun processTheInputRequest(requestCode: Int, intent: Intent?) {
        when (requestCode) {
            REQUEST_VIDEO_CAPTURE -> {
                if (intent?.data != null) {
                    q_add.hint = "add a tag to the attached video so that people can find it\n#math1b #drawingGraphs"
                    enableAddButtons(isVid = true)
                    if (add_q_linearlayout.childCount > 0) {
                        snack("You can only add one video. press X to remove the previously add video to add another one")
                        return
                    }
                    val vidUri = intent.data!!
                    val vidUrl = getRealPathFromURI(vidUri, this)
                    enableAddButtons(false) // disable the add buttons
                    // q_vid.setVideoURI(intent.data!!)
                    Log.d(TAG, "Initializing Video media")
                    val file = File(vidUrl)
                    Log.d(TAG, "the path is $vidUrl")
                    val btnFile: CardView = BasicUtils.getFileViewInstance(this,
                            Media(file.name,
                                    file.length(),
                                    Media.VIDEO_TYPE,
                                    file.absolutePath), {
                        Log.d(TAG, "the clicked file is $it")
//                            val bottomSheetDialogFragment = VideoViewFragment.newInstance(vid_url.path)
//                            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
                        val vidIntent = Intent(Intent.ACTION_VIEW, vidUri)
                        vidIntent.setDataAndType(vidUri, "video/*")
                        startActivity(vidIntent)

                    }, {
                        if (it.parent != null) {
                            (it.parent as ViewGroup).removeView(it)
                            ifHoriItemViewIsEmptyEnableAllAddButtons()
                        }

                    })

                    add_q_linearlayout.addView(btnFile)
                    mediaFiles = if (add_q_linearlayout.childCount != 0) {
                        hashMapOf(Media.VIDEO_TYPE to listOf(vidUrl))
                    } else {
                        null  //nothing

                    }
                }
            }
            FilePickerConst.REQUEST_CODE_PHOTO -> {
                if (intent != null) {
                    q_add.hint = "add a tag to the attached photo so that people can find it\n#math1b #drawingGraphs"
                    enableAddButtons(isPic = true)
                    val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    Log.d(TAG, "the data that we got: Photos $photosPaths")
                    photosPaths.forEach(
                            {
                                l("Hello From Timber the path is $it")
                                val imagePreviewInstance = BasicUtils.getImagePreviewInstance(this@AddQuestionActivity, it,
                                        {
                                            if (it.parent != null) {
                                                (it.parent as ViewGroup).removeView(it)
                                            }
                                            ifHoriItemViewIsEmptyEnableAllAddButtons()
                                        }
                                )
                                add_q_linearlayout.addView(imagePreviewInstance)
                            }
                    )
                    mediaFiles = hashMapOf(Media.PICTURE_TYPE to photosPaths)
                }
            }
            FilePickerConst.REQUEST_CODE_DOC -> {
                if (intent != null) {
                    q_add.hint = "add a tag to the attached document so that people can find it\n#math1b #drawingGraphs"
                    enableAddButtons(isFiles = true)
                    val photosPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)
                    Log.d(TAG, "the data that we got ${intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)}")
                    photosPaths.forEach {
                        Timber.i("Helllo the file has been selected $it")
                        val fileViewInstance = BasicUtils.getFileViewInstance(this,
                                Media(it.substringAfterLast("/"), 0, Media.DOCS_TYPE, it),
                                { _ -> }, //not require but looks great ;)
                                {
                                    if (it.parent != null) {
                                        (it.parent as ViewGroup).removeView(it)
                                    }
                                    ifHoriItemViewIsEmptyEnableAllAddButtons()

                                }
                        )
                        add_q_linearlayout.addView(fileViewInstance)
                    }
                    mediaFiles = hashMapOf(Media.DOCS_TYPE to photosPaths)
                }
            }

        }

    }

    private fun publishNewQuestion() {
        //get the question text
        //create files from the paths send them to presenter
        // presenter will upload to server
        val docsListPaths = mediaFiles?.get(Media.DOCS_TYPE)
        val videoListPaths = mediaFiles?.get(Media.VIDEO_TYPE)
        val picturesListPaths = mediaFiles?.get(Media.PICTURE_TYPE)
        Log.d(TAG, "The values are: " +  mediaFiles?.values?.toString())

        val questionBody = q_add.text.toString()
        val questionTitle = q_add_title.text.toString()

        when {
            questionTitle.isBlank() -> {
                snack("Please add a brief title about your question")
                return
            }
            questionTitle.isBlank() -> {
                snack("Please add a brief title about your question")
                return
            }
            questionBody.isBlank() -> {
                snack("You did not type the actual question")
                return
            }
            BasicUtils.textHasNoTags(questionBody) -> {  // the last && is not required just for clearity
                snack("please add at least one tag to this question so that people can find it")
                return
            }
            else -> {
                val (_, tags) = BasicUtils.getCleanTextAndTags(questionBody)
                val questionToPost = Question(questionTitle, questionBody, 0, tags.map { Tag(it, Date()) }, Question.TYPE_Q)

                when {

                    docsListPaths != null && docsListPaths.isNotEmpty() -> {
                        mAddQuestionPresenter.publishNewQuestion(questionToPost, docsListPaths)
                        return
                    }
                    videoListPaths != null && videoListPaths.isNotEmpty() -> {
                        mAddQuestionPresenter.publishNewQuestion(questionToPost, videoListPaths)
                        return
                    }
                    picturesListPaths != null && picturesListPaths.isNotEmpty() -> {
                        mAddQuestionPresenter.publishNewQuestion(questionToPost, picturesListPaths)
                        return
                    }
                    questionBody.isNotBlank() -> {
                        mAddQuestionPresenter.publishNewQuestion(questionToPost)
                        return
                    }
                    else -> {
                        snack("Please also add a question body")
                    }
                }
            }
        }

    }

    private fun enableAddButtons(isPic: Boolean = false, isVid: Boolean = false, isFiles: Boolean = false) {
        btn_add_picture.isEnabled = isPic
        btn_add_video.isEnabled = isVid
        btn_add_files.isEnabled = isFiles
    }

    private fun ifHoriItemViewIsEmptyEnableAllAddButtons() {
        if (add_q_linearlayout.childCount == 0) {
            enableAddButtons(true, true, true)
            q_add.hint = "You can just type your question here, like you do on google, no need to greet we are all here to get answers :)"
        }
    }

    //todo: leaking memory is here
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

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            /*ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)*/
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        }
    }

    companion object {
        private const val TAG = "__AddQuestionActivity"
        const val REQUEST_VIDEO_CAPTURE = 1
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, AddQuestionActivity::class.java)
            return intent
        }
    }
}
