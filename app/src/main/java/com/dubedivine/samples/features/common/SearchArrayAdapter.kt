package com.dubedivine.samples.features.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.dubedivine.samples.R
import com.dubedivine.samples.data.model.Question
import com.dubedivine.samples.util.BasicUtils

import timber.log.Timber
import javax.inject.Inject

/**
 * Created by divine on 2017/09/09.
 */

//todo: should inject this from the data base in the future
open class SearchArrayAdapter @Inject
constructor(context: Context,
            private val questions: List<Question>) : ArrayAdapter<Question>(context, 0, questions), Filterable {

    private var _onItemClick: OnItemClickListener? = null
    var onItemClick: OnItemClickListener?  //backing field vs setters??
         get() = _onItemClick
         set(listener) {
            _onItemClick = listener
        }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val fl = Filter.FilterResults()
                if (charSequence != null) {
                    Timber.i("performFiltering: the filtered data is %s", charSequence)
                    fl.values = questions
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        val question: Question = getItem(position)
        if (convertView == null) {   //manually instantiating the view
            convertView = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false)
        }

        val qTitle: TextView? = convertView?.findViewById(R.id.question_title)
        val qBody: TextView? = convertView?.findViewById(R.id.question_body)
        val qStatus: TextView? = convertView?.findViewById(R.id.question_status)

        qTitle?.text = question.title
        qBody?.text = question.body
        qStatus?.text = BasicUtils.createTheStatusTextViewInfo(question)

        convertView?.setOnClickListener({
            if(onItemClick == null) {
                Timber.e("setOnClickListener: the click listener is null!!")
            } else {
                onItemClick?.onItemClick(question)  // we are sending this question back to the activity because we want to put it on top just like google!
            }
        })

        return convertView!! // it can be null bro!!
    }

    interface OnItemClickListener {
        fun onItemClick(question: Question)
    }
}
