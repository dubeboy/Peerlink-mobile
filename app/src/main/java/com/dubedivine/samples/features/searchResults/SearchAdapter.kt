package com.dubedivine.samples.features.searchResults

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.FileView
import com.dubedivine.samples.features.searchResults.SearchAdapter.SearchViewHolder
import com.dubedivine.samples.util.BasicUtils
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by divine on 2017/09/10.
 */
class SearchAdapter @Inject
constructor(private val context: Activity) : RecyclerView.Adapter<SearchViewHolder>() {

    private var clickListener: ClickListener? = null // should use property style
    private val mQuestion: ArrayList<Question> = arrayListOf()

    //binding each element to the view boss!!!
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(mQuestion[position])
    }

    override fun getItemCount(): Int = mQuestion.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_question, parent, false)
        return SearchViewHolder(view, context, clickListener)
    }

    interface ClickListener {
        fun onQuestionClick(question: Question)
    }

    class SearchViewHolder(view: View,
                           private val context: Activity,
                           private val clickListener: ClickListener?) : RecyclerView.ViewHolder(view) {

        private val questionStatus: TextView = view.findViewById(R.id.question_status)  //like: 10 answers, answered by Divine
        private val questionTitle: TextView = view.findViewById(R.id.question_title)
        private val questionBody: TextView = view.findViewById(R.id.question_body)
        private val tvNumberOfMediaFiles: TextView = view.findViewById(R.id.tv_number_of_media_files)
        private val questionTagsLayout: LinearLayout = view.findViewById(R.id.question_tags_layout)

        fun bind(question: Question) {
            Timber.i("is it null?:  $questionTitle")
            questionStatus.text = BasicUtils.createTheStatusTextViewInfo(question)
            questionBody.text = question.body
            questionTitle.text = question.title
            if (question.video != null) {
                tvNumberOfMediaFiles.visibility = View.VISIBLE
                tvNumberOfMediaFiles.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ondemand_video_24dp, 0, 0, 0)
                tvNumberOfMediaFiles.text = "1 Video"
            } else {
                if (question.files != null && question.files!!.size > 0) {
                    tvNumberOfMediaFiles.visibility = View.VISIBLE
                    when (question.files!![0].type) {
                        Media.PICTURE_TYPE -> {
                            tvNumberOfMediaFiles.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_black_24dp, 0, 0, 0)
                            val x = question.files!!.size
                            tvNumberOfMediaFiles.text = "Picture".pluralise(x)
                            return
                        }
                        Media.DOCS_TYPE -> {
                            tvNumberOfMediaFiles.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_24dp, 0, 0, 0)
                            val x = question.files!!.size
                            tvNumberOfMediaFiles.text = "Document".pluralise(x)
                            return
                        }
                    }

                }
            }
            // not required
            if (question.tags.isNotEmpty()) { // a question should have atleast one tag yoh
                // todo: clean the layout first
                questionTagsLayout.visibility = View.VISIBLE
                if (questionTagsLayout.childCount == 0) {
                    question.tags.forEach(action = {
                        questionTagsLayout.addView(BasicUtils.createTagsChip(itemView.context, it.name))
                    })
                }
            }
            itemView.setOnClickListener({
                clickListener?.onQuestionClick(question)
            })
        }

        private fun String.pluralise(x: Int): String {
            if (x != 1) {
              return """$x ${this}s"""
            }
            return """$x ${this}"""
        }
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    fun addQuestions(questions: List<Question>) {
        mQuestion.addAll(questions) //should never be null so... i am like lets do it kotlin!!
        notifyDataSetChanged()
    }

    fun setTopQuestion(question: Question) { // todo: sending the whole question inefficient
        Timber.i("here is the Questions List from DB $mQuestion")
        Timber.i("and the passed in question is $question")
        var indexOfQ = -1
        mQuestion.forEachIndexed { i, item ->
            if (item.title == question.title) {
                indexOfQ = i
                return@forEachIndexed
            }
        }
        if (indexOfQ != -1)
            Collections.swap(mQuestion, indexOfQ, 0)
    }

    fun clear() {
        mQuestion.clear()
    }
}

