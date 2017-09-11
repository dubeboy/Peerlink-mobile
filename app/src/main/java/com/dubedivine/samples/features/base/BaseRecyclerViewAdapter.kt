package com.dubedivine.samples.features.base

import android.support.v7.widget.RecyclerView
import android.view.View
import com.dubedivine.samples.features.searchResults.SearchAdapter

/**
 * Created by divine on 2017/09/10.
 */

//todo: to be done soon!!! so that no repeated code
abstract class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

abstract class BaseRecyclerViewAdapter
constructor(view: View) : RecyclerView.Adapter<MyViewHolder>() {

}