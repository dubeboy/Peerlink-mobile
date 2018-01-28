package com.dubedivine.samples.features.detail.comments

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Comment

/**
 * Created by div on 2018/01/28.
 */
class CommentsAdapter constructor(private val comment: List<Comment>,
                                  private val showTwoMaxLinesOnEditText: Boolean) :
                                                    RecyclerView.Adapter<CommentsAdapter.DetailView>() {

    private fun clear() {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return DetailView(view)
    }

    override fun getItemCount(): Int = if (comment.size > 5) 4 else comment.size

    override fun onBindViewHolder(holder: DetailView, position: Int) {
            holder.bind(comment[position], showTwoMaxLinesOnEditText)
    }

    class DetailView(view: View) : RecyclerView.ViewHolder(view) {

        private val commentsTv: TextView = view.findViewById(R.id.comments_tv)

        fun bind(comment: Comment, showTwoMaxLinesOnEditText: Boolean) {
            if (showTwoMaxLinesOnEditText) commentsTv.maxLines = 2
            commentsTv.text = comment.body
        }
    }
}