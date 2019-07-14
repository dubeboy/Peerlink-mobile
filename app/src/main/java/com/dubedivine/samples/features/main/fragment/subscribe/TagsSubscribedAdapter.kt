package com.dubedivine.samples.features.main.fragment.subscribe

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Tag
import javax.inject.Inject

/**
 * Created by d on 3/24/18.
 */
class TagsSubscribedAdapter @Inject
constructor() : RecyclerView.Adapter<TagsSubscribedAdapter.TagsSubscribedAdapterViewHolder>() {

    private var mTags: List<Tag> = emptyList()

    var tags: List<Tag>
        get() {
            return mTags
        }
        set(value) {
            mTags = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsSubscribedAdapterViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_tag, parent, false)
        return TagsSubscribedAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: TagsSubscribedAdapterViewHolder, position: Int) {
        holder.bind(mTags[position])
    }

    class TagsSubscribedAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTag: TextView = view.findViewById(R.id.tag_name)

        fun bind(tag: Tag) {
            tvTag.text = "#${tag.name}"
            tvTag.setOnClickListener({
                Log.d(TAG, "you clicked me ${tag.name}")
            })
        }
    }


    companion object {
        const val TAG = "TAGS_SUBS"
    }
}