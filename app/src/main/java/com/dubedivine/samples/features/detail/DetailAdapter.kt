package com.dubedivine.samples.features.detail

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Answer
import com.dubedivine.samples.data.model.Comment
import com.dubedivine.samples.data.model.Media
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.features.common.FileView
import com.dubedivine.samples.features.detail.DetailActivity.Companion.TAG
import com.dubedivine.samples.features.detail.comments.CommentsAdapter
import com.dubedivine.samples.features.detail.comments.FullCommentsListFragment
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
@Inject constructor(private val mDetailPresenter: DetailPresenter) :
        RecyclerView.Adapter<DetailAdapter.DetailView>() {

    private var _mQuestion: Question? = null

    var mQuestion: Question
        get() = _mQuestion!!
        set(value) {
            _mQuestion = value
        }

    fun addAnswer(answer: Answer) {
        mQuestion.answers?.add(answer)
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DetailView {
        val view = LayoutInflater
                .from(parent!!.context)
                .inflate(R.layout.item_question_detail_thread, parent, false)
        return DetailView(view, mDetailPresenter)
    }

    override fun getItemCount(): Int = (mQuestion.answers?.size ?: 0) + 1

    override fun onBindViewHolder(holder: DetailView, position: Int) {
        if (position == 0) {
            holder.bindQuestion(mQuestion) // look at this this is how you can index an Item
        } else { //position > 0
            holder.bindAnswer(mQuestion.id!!, mQuestion.answers!![position - 1])  // so that I don`t miss the 0th element
        }
    }

    fun clear() {
        mQuestion.answers?.clear()
    }

    fun addAll(answers: List<Answer>) {
        mQuestion.answers?.addAll(answers)
//        notifyDataSetChanged()

    }

    inner class DetailView(private val view: View, private val mDetailPresenter: DetailPresenter) : RecyclerView.ViewHolder(view) {
        private val btnVoteUp: ImageButton = view.findViewById(R.id.btn_vote_up)
        private val btnVoteDown: ImageButton = view.findViewById(R.id.btn_vote_down)
        private val btnCorrectAnswer: Button = view.findViewById(R.id.btn_correct_answer)
        private val tvQuestionTitle: TextView = view.findViewById(R.id.q_title)
        private val tvQuestionBody: TextView = view.findViewById(R.id.q_body)
        private val questionVidView: VideoView = view.findViewById(R.id.q_vid)
        private val questionImageView: ImageView = view.findViewById(R.id.q_image)
        private val tagsLinearHorizontalView: LinearLayout = view.findViewById(R.id.q_tags_linearlayout) // naming is a bit off...
        private val filesLinearHorizontalView: LinearLayout = view.findViewById(R.id.q_files_linearlayout)
        private val tvVotes: TextView = view.findViewById(R.id.tv_vote_count)
        private val recyclerComments: RecyclerView = view.findViewById(R.id.comments_recycler_list)
        private val btnSubmitComment: ImageButton = view.findViewById(R.id.btn_comment_question)
        private val btnMoreComments: ImageButton = view.findViewById(R.id.btn_more_comments)
        private val etCommentBody: EditText = view.findViewById(R.id.et_comment_input)
        private var q: Question? = null

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
                mDetailPresenter.addVote(q.id!!, true, this)
                tvVotes.text = "${(tvVotes.text.toString().toInt() + 1)}"
                //should set the button to be disabled here
            })
            btnVoteDown.setOnClickListener({
                mDetailPresenter.addVote(q.id!!, false, this)
                tvVotes.text = "${(tvVotes.text.toString().toInt() - 1)}"
            })

            btnSubmitComment.setOnClickListener({
                handleCommentSubmissionForQuestion(q.id!!, etCommentBody.text.toString(), this)
            })

            if (q.comments != null && q.comments!!.isNotEmpty())
                attachCommentsAdapter(q.comments!!)
            else Log.d(TAG, "Could not attach comments recycler Q: ${q.comments}")
        }

        fun bindAnswer(qId: String, ans: Answer) {

            bindCommonAnswer(ans)

            btnVoteUp.setOnClickListener({
                //should be done via the presenter
                mDetailPresenter.addVoteToAnswer(qId, ans.id, true, this)
                tvVotes.text = "${(tvVotes.text.toString().toInt() + 1)}"
                //should set the button to be disabled here
                btnVoteUp.isEnabled = false
            })

            btnVoteDown.setOnClickListener({
                //should be done via the presenter
                mDetailPresenter.addVoteToAnswer(qId, ans.id, false, this)
                tvVotes.text = "${(tvVotes.text.toString().toInt() - 1)}"
                btnVoteDown.isEnabled = false
            })

            btnSubmitComment.setOnClickListener({
                handleCommentSubmissionForAnswer(qId,
                        ans.id!!,
                        etCommentBody.text.toString(),
                        this) //todo: leaks
            })

            if (ans.comments != null && ans.comments!!.isNotEmpty())
                attachCommentsAdapter(ans.comments!!)
            else
                Log.i(TAG, "Could not attach comments recycler A: ${ans.comments}")
        }

        private fun attachCommentsAdapter(comments: List<Comment>) {
            Log.d(TAG, "Attaching comments recycler view and the comments are $comments ")
            recyclerComments.visibility = View.VISIBLE
            val commentsAdapter = CommentsAdapter(comments, true)
            recyclerComments.layoutManager = LinearLayoutManager(view.context)
            recyclerComments.adapter = commentsAdapter

            fun showCommentsDetails() {
                val commentsListFragment = FullCommentsListFragment(view.context, comments)
                commentsListFragment.show()
            }

            recyclerComments.setOnClickListener({
                showCommentsDetails()
            })

            if (comments.size > 5) {
                btnMoreComments.visibility = View.VISIBLE
                btnMoreComments.setOnClickListener({
                    showCommentsDetails()
                })
            }


        }

        private fun handleCommentSubmissionForQuestion(questionId: String, body: String, detailView: DetailView) {
            mDetailPresenter.postCommentQuestion(questionId, body, detailView)
        }

        private fun handleCommentSubmissionForAnswer(questionId: String, answerId: String, body: String, detailView: DetailView) {
            mDetailPresenter.postCommentForAnswer(questionId, answerId, body, detailView)
        }

        fun downVoteAnswerAndDisableButton(answerId: String) {

        }

        fun upVoteAnswerAndDisableButton(answerId: String) {

        }

        fun downVoteQuestionAndDisableButton() {
            q!!.votes = q!!.votes - 1
            btnVoteUp.isEnabled = true
            notifyDataSetChanged()
        }

        fun upVoteQuestionAndDisableButton() {
            q!!.votes = q!!.votes + 1
            btnVoteDown.isEnabled = true
            notifyDataSetChanged()
        }

        //lets see how much memory this leaks

    }

    fun addCommentForQuestion(comment: Comment) {
        mQuestion.comments!!.add(comment)
        notifyDataSetChanged()
    }


    // why don`t we just pass the index of the answer instead of this O(n) operation it got to be O(1)
    fun addCommentForAnswer(answerId: String, comment1: Comment) {
        for (ans in mQuestion.answers!!) {
            if (ans.id == answerId) {
                ans.comments.add(comment1)
                notifyDataSetChanged()
            }
        }
    }
}