package com.dubedivine.samples.features.searchResults

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.FileView
import com.dubedivine.samples.features.searchResults.SearchAdapter.SearchViewHolder
import com.dubedivine.samples.util.BasicUtils
import com.dubedivine.samples.util.setDefaultDrawableIcon
import com.dubedivine.samples.util.setRandomColor
import com.robertlevonyan.views.chip.Chip
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by divine on 2017/09/10.
 */
class SearchAdapter @Inject
constructor(private val context: Activity) : RecyclerView.Adapter<SearchViewHolder>() {

    private var clickListener: ClickListener? = null
    private var mQuestion: ArrayList<Question>? = null

    init {
        mQuestion = arrayListOf()
    }

    //binding each element to the view boss!!!
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(mQuestion!!.get(position))
    }

    override fun getItemCount(): Int = mQuestion!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_question, parent, false)
        return SearchViewHolder(view)
    }

    interface ClickListener {
        fun onQuestionClick(question: Question)
    }


    open inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.question_status) @JvmField var questionStatus: TextView? = null //like: 10 answers, answered by Divine
        @BindView(R.id.question_title) @JvmField var questionTitle: TextView? = null
        @BindView(R.id.question_body) @JvmField var questionBody: TextView? = null
        @BindView(R.id.question_files_hori_scrollview) @JvmField var questionFilesHoriScrollView: HorizontalScrollView? = null
        @BindView(R.id.question_tags_layout) @JvmField var questionTagsLayout: LinearLayout? = null

        fun bind(question: Question) {
            questionStatus?.text = BasicUtils.createTheStatusTextViewInfo(question)
            questionBody?.text = question.body
            questionTitle?.text = question.title
            if (question.files != null && question.files.size > 0) {
                questionFilesHoriScrollView?.visibility = View.VISIBLE
                question.files.forEach({
                    val fileView = FileView(itemView.context, it)
                    questionFilesHoriScrollView?.addView(fileView)
                })

            }
            if (question.tags != null && question.tags.size > 0) { // a question should have atleast one tag yoh
                questionTagsLayout?.visibility = View.VISIBLE
                question.tags.forEach({
                    val params = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT )
                    val chip: Chip = Chip(context)
                    chip.layoutParams = params
                    chip.chipText = it.name
                    chip.setRandomColor()
                    chip.setDefaultDrawableIcon()
                    questionTagsLayout?.addView(chip)
                })

            }
        }
    }

    fun  setClickListener(clickListener: ClickListener ) {
        this.clickListener = clickListener
    }

    fun addQuestions(questions: ArrayList<Question>) {
        mQuestion!!.addAll(questions) //should never be null so... i am like lets do it kotlin!!
    }

    companion object {
//        val intent =
    }
}

