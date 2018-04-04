package com.dubedivine.samples.features.main.fragment.subscribe

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Tag
import com.dubedivine.samples.features.base.BaseFragment
import com.dubedivine.samples.features.main.MainPresenter
import com.dubedivine.samples.util.toast
import kotlinx.android.synthetic.main.content_swipe_refresh.*
import kotlinx.android.synthetic.main.fragment_tags_subscribed.*
import javax.inject.Inject

/**
 * Created by d on 3/24/18.
 */
class TagsSubscribedFragment : BaseFragment() {

    @Inject lateinit var tagsSubscribedAdapter: TagsSubscribedAdapter
    @Inject lateinit var mainPresenter: MainPresenter

    override val layout: Int
        get() = R.layout.fragment_tags_subscribed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_to_refresh.isRefreshing = true


        setUpSwipeRecyclerView {
            mainPresenter.fragmentGetTagsSubscribed(this::showTags)
        }

        recycler_data.layoutManager = LinearLayoutManager(activity)
        recycler_data.adapter = tagsSubscribedAdapter
        mainPresenter.fragmentGetTagsSubscribed(this::showTags) // pass the function handle
    }

    private fun showTags(status: Boolean, message: String, tags: List<Tag>?) {
        if (status) {
            tagsSubscribedAdapter.tags = tags!!
        } else {
            tv_error.visibility = View.VISIBLE
            toast(message)
        }
        swipe_to_refresh.isRefreshing = false
    }
}

