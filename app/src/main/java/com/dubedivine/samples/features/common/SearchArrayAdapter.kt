package com.dubedivine.samples.features.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

import java.util.ArrayList

import timber.log.Timber

/**
 * Created by divine on 2017/09/09.
 */
open class SearchArrayAdapter(context: Context,
                              resource: Int) : ArrayAdapter<String>(context, resource), Filterable {

    private val items = ArrayList<String>()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val fl = Filter.FilterResults()
                if (charSequence != null) {
                    Timber.i("performFiltering: the filterd data is %s", charSequence)
                    fl.values = items
                }
                return fl
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                if (charSequence != null && charSequence.isNotEmpty()) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
