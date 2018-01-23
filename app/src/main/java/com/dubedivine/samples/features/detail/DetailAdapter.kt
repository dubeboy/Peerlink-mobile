package com.dubedivine.samples.features.detail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.dubedivine.samples.R
import com.dubedivine.samples.data.DataManager
import com.dubedivine.samples.data.model.Answer
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.FileView
import com.dubedivine.samples.util.BasicUtils
import javax.inject.Inject

/**
 * Created by divine on 2017/09/16.
 */

//should also inject the user ID here as well

//Ok so here there there is a bit of a problem
//I want everything to scroll
//so i add the question as the top element on the list

class DetailAdapter
@Inject constructor(private val mDataManager: DataManager)
    : RecyclerView.Adapter<DetailAdapter.DetailView>() {

    private var _mQuestion: Question? = null

    var mQuestion: Question
        get() = _mQuestion!!
        set(value) {
            _mQuestion = value
        }

    fun clear() {
        mQuestion.answers?.clear()
    }

    fun addAll(answers: List<Answer>) {
        mQuestion.answers?.addAll(answers)
//        notifyDataSetChanged()

    }

    fun addAnswer(answer: Answer) {
        mQuestion.answers?.add(answer)
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DetailView {
        val view = LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.item_question_detail_thread, parent, false)
        return DetailView(view, mDataManager)
    }

    override fun getItemCount(): Int = (mQuestion.answers?.size ?: 0) + 1

    override fun onBindViewHolder(holder: DetailView?, position: Int) {
        if (position == 0) {
            holder!!.bindQuestion(mQuestion) // look at this this is how you can index an Item
        } else { //position > 0
            holder!!.bindAnswer(mQuestion.answers!![position - 1])  // so that I don`t miss the 0th element
        }
    }

    // no need to be inner class from my perspective we don`t have to carry the ref of the parent class
    class DetailView(view: View, private val mDataManager: DataManager) : RecyclerView.ViewHolder(view) {

        private val btnVoteUp: Button = view.findViewById(R.id.btn_vote_up)
        private val btnVoteDown: Button = view.findViewById(R.id.btn_vote_down)
        private val btnCorrectAnswer: Button = view.findViewById(R.id.btn_correct_answer)
        private val tvQuestionTitle: TextView = view.findViewById(R.id.q_title)
        private val tvQuestionBody: TextView = view.findViewById(R.id.q_body)
        private val questionVidView: VideoView = view.findViewById(R.id.q_vid)
        private val questionImageView: ImageView = view.findViewById(R.id.q_image)
        private val tagsLinearHorizontalView: LinearLayout = view.findViewById(R.id.q_tags_linearlayout) // naming is a bit off...
        private val filesLinearHorizontalView: LinearLayout = view.findViewById(R.id.q_files_linearlayout)
        private val tvVotes: TextView = view.findViewById(R.id.tv_vote_count)

        private var q: Question?= null

        //it should have helped if both of our answers and question where of the same type hierarchy i think....
        // but that for some other day bro for now the painful method will work

        private fun bindCommonQuestion(q: Question) {
            tvQuestionTitle.text = q.title
            tvQuestionBody.text = q.body
            bindFileView(q.files)
            bindVotes(q.votes)
            bindVideo(q.video)
        }

        private fun bindCommonAnswer(a: Answer) {
            tvQuestionTitle.visibility = View.GONE
            tvQuestionBody.text = a.body
            bindFileView(a.files)
            bindVotes(a.votes)
            bindVideo(a.video)
        }

        private fun bindFileView(files: List<Media>?) {
            if (files != null) {
                filesLinearHorizontalView.visibility = View.VISIBLE
                files.forEach({
                    val fileView = FileView(itemView.context, it)
                    filesLinearHorizontalView.addView(fileView)
                })
            }
        }

        private fun bindVideo(video: Media?) {
            if (video?.location != null) {
                questionVidView.visibility = View.VISIBLE
                if (video.type == Media.VIDEO_TYPE) {
//                    questionVidView.setV
//                     load the video here please
                } else if (video.type == Media.PICTURE_TYPE) {
                    Glide.with(itemView.context)
                            .load(video.location)
                            .into(questionImageView)
                }
            }
        }

        private fun bindVotes(votes: Long) {
            tvVotes.text = votes.toString()
        }

        fun bindQuestion(q: Question) {
            bindCommonQuestion(q)
            this.q = q
            q.tags.forEach({
                val chip = BasicUtils.createTagsChip(itemView.context, it.name)
                tagsLinearHorizontalView.addView(chip)
            })

            if (q.answered == true) {
                btnCorrectAnswer.visibility = View.VISIBLE
            }

            //todo: once we have the user object then we can allow the changing of state of the answered object
            btnVoteUp.setOnClickListener({
                mDataManager.addVote(q.id!!, 1)
                tvVotes.text = "${(tvVotes.text.toString().toInt() +1)}"
                //should set the button to be disabled here
            })
            btnVoteDown.setOnClickListener({
                mDataManager.addVote(q.id!!, -1)
                tvVotes.text = "${(tvVotes.text.toString().toInt() -1)}"
            })
        }

        fun bindAnswer(ans: Answer) {

            bindCommonAnswer(ans)

            btnVoteUp.setOnClickListener({
                mDataManager.addVoteToAnswer(q!!.id!!, ans.id, 1)
                tvVotes.text = "${(tvVotes.text.toString().toInt() +1)}"
                //should set the button to be disabled here
            })
            btnVoteDown.setOnClickListener({
                mDataManager.addVoteToAnswer(q!!.id!!, ans.id, -1)
                tvVotes.text = "${(tvVotes.text.toString().toInt() -1)}"
            })
        }
    }
}