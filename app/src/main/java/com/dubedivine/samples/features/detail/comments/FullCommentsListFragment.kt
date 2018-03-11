package com.dubedivine.samples.features.detail.comments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ViewUtils
import android.view.View
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Comment
import com.dubedivine.samples.util.ViewUtil

/**
 * Created by div on 2018/01/28.
 */
class FullCommentsListFragment(con: Context, comments: List<Comment>) : BottomSheetDialog(con) {

    init {
        val contentView = View.inflate(context, R.layout.fragment_full_comments_bottom_sheet, null)
        val commentsRecycler = contentView.findViewById<RecyclerView>(R.id.comments_recycler_list)
        commentsRecycler.layoutManager = LinearLayoutManager(con)
        commentsRecycler.adapter = CommentsAdapter(comments, false)
        contentView.setPadding(0,  0, 0, ViewUtil.dpToPx(20))
        setContentView(contentView)
    }

}