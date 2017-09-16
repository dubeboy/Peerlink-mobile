package com.dubedivine.samples.features.detail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.dubedivine.samples.R
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.FileView
import com.dubedivine.samples.util.BasicUtils
import com.robertlevonyan.views.chip.Chip
import javax.inject.Inject

/**
 * Created by divine on 2017/09/16.
 */

//should also inject the user ID here as well
class DetailAdapter @Inject constructor (private val mDataManager: DataManager,
                                         private val _mQuestions: ArrayList<Question>) : RecyclerView.Adapter<DetailAdapter.DetailView>() {

    var mQuestions: ArrayList<Question>
        get() = _mQuestions
        set(value) {
            _mQuestions.clear()
            _mQuestions.addAll(value)
        }

    fun clear() {
        mQuestions.clear()
    }

    fun addAll(questions: List<Question>) {
        mQuestions.addAll(questions)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DetailView {
        val view = LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.item_question_detail_thread, parent, false)
        return DetailView(view, mDataManager)
    }

    override fun getItemCount(): Int = mQuestions.size

    override fun onBindViewHolder(holder: DetailView?, position: Int) {
        holder!!.bind(mQuestions[position]) // look at this this is how you can index an Item
    }

    // no need to be inner class from my perspective we dont have to carry the ref of the parent class
    class DetailView(view: View, private val mDataManager: DataManager) : RecyclerView.ViewHolder(view) {

        val btnVoteUp: Button = view.findViewById(R.id.btn_vote_up)
        val btnVoteDown: Button = view.findViewById(R.id.btn_vote_down)
        val btnCorrectAnswer: Button = view.findViewById(R.id.btn_correct_answer)
        val tvQuestionTitle: TextView = view.findViewById(R.id.q_title)
        val tvQuestionBody: TextView = view.findViewById(R.id.q_body)
        val questionVidView: VideoView = view.findViewById(R.id.q_vid)
        val questionImageView: ImageView = view.findViewById(R.id.q_image)
        val tagsHorizontalView: HorizontalScrollView = view.findViewById(R.id.hori_tags_list)
        val filesHorizontalView: HorizontalScrollView = view.findViewById(R.id.hori_files_list)


        fun bind(q: Question) {
            tvQuestionTitle.text = q.title
            tvQuestionBody.text = q.body
            if (q.video?.location != null) {
                questionVidView.visibility = View.VISIBLE
                if(q.video.type == Media.VIDEO_TYPE) {
//                    questionVidView.setV
//                     load the video here please
                } else if(q.video.type == Media.PICTURE_TYPE) {
                    Glide.with(itemView.context)
                            .load(q.video.location)
                            .into(questionImageView)
                }
            }
            if (q.files != null) {
                filesHorizontalView.visibility = View.VISIBLE
                q.files!!.forEach({
                   val fileView =  FileView(itemView.context, it)
                    filesHorizontalView.addView(fileView)
                })
            }

            q.tags.forEach({
                val chip = BasicUtils.createChipFromCode(itemView.context,  it.name)
                tagsHorizontalView.addView(chip)
            })

            if (q.answered == true) {
                btnCorrectAnswer.visibility = View.VISIBLE
            }

            //todo: once we have the user object then we can allow the changing of state of the answered object
            btnVoteUp.setOnClickListener({
                mDataManager.addVote(q.id, 1)
                //should set the button to be disabled here
            })
            btnVoteDown.setOnClickListener({
                mDataManager.addVote(q.id,-1)
            })
        }


    }
}