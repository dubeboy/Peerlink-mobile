package com.dubedivine.samples.features.addQuestion

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BaseActivity
import com.dubedivine.samples.util.ViewUtil
import com.dubedivine.samples.util.snack
import kotlinx.android.synthetic.main.activity_add_question.*
import kotlinx.android.synthetic.main.content_fab_add.*
import timber.log.Timber
import javax.inject.Inject


// todo: this class breaks the constency rull one its not using timber!!
// and it not using ButterKnife myabe there shold be a revolution
class AddQuestionActivity : BaseActivity(), AddQuestionMvpView {

    @Inject lateinit var mAddQuestionPresenter: AddQuestionPresenter

    lateinit var adapter: ArrayAdapter<String>
    lateinit var tagsSuggestionSpinnerView: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mAddQuestionPresenter.attachView(this)
        //fab_add set the drawable here of a tick

        Log.d(TAG, "guys you are awesome")
        Timber.i(TAG, "Timber is not so awesome")

        adapter = ArrayAdapter(this@AddQuestionActivity, android.R.layout.simple_spinner_item)
        tagsSuggestionSpinnerView = Spinner(this)

        setUpTagsSuggestionSpinner(adapter)
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
                        }
                        if (isTypingHashTag) {
                            Log.d(TAG, "this is the has tag dwag $text")
                            //get the tags suggestion here
                            if (numChars >= tagStartIndex) {
                                val tag = text.subSequence(tagStartIndex - 1, text.lastIndex + 1)
                                Log.d(TAG, "the actual tag is: $tag")
                                mAddQuestionPresenter.getTagSuggestion(tag)
                            }
                        } else {
                            Log.d(TAG, "this is the normal text: $text")
                            // stop the suggestion or disconnect the adapter what you think!
                        }
                    }
                }
            }
        })
    }

    override val layout: Int
        get() = R.layout.activity_add_question

    fun setUpTagsSuggestionSpinner(adapter: ArrayAdapter<String>) {
        tagsSuggestionSpinnerView.layoutParams = ViewUtil.getLayoutParamsForView()
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tagsSuggestionSpinnerView.adapter = adapter
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

    override fun showTagsSuggestion(tags: List<Tag>, typedWord: CharSequence) {
        for (tag in tags) {
            adapter.add(tag.name)
        }
        val popUpWindow = PopupWindow(tagsSuggestionSpinnerView, getDisplay().x - 50, getDisplay().y - 500, true)
        //  popUpWindow.setAnimationStyle(R.anim.animation)
        popUpWindow.isFocusable = true
        popUpWindow.isOutsideTouchable = true;
        //  popUpWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.pop))
        val (x, y) = getTheXAndYForWord(q_add, typedWord)
        popUpWindow.showAtLocation(q_add, Gravity.TOP, x, y)
    }


    override fun showProgress(show: Boolean) {
        snack("showing progress $show")
    }

    override fun showError(error: Throwable) {
        snack("showing error")
        error.printStackTrace()
    }

    companion object {
        val TAG = "__AddQuestionActivity"
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, AddQuestionActivity::class.java)
            return intent
        }
    }
}
