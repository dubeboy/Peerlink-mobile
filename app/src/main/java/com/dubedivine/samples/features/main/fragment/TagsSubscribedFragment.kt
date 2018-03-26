package com.dubedivine.samples.features.main.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dubedivine.samples.R
import com.dubedivine.samples.features.base.BaseFragment
import kotlinx.android.synthetic.main.content_swipe_refresh.*
import javax.inject.Inject

/**
 * Created by d on 3/24/18.
 */
class TagsSubscribedFragment : BaseFragment() {

    @Inject lateinit var tagsSubscribedAdapter: TagsSubscribedAdapter

    override val layout: Int
        get() = R.layout.fragment_tags_subscribed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSwipeRecyclerView {}
        recycler_data.layoutManager = LinearLayoutManager(activity)
        recycler_data.adapter = tagsSubscribedAdapter
    }


}

