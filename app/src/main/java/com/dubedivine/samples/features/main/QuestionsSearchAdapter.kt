package com.dubedivine.samples.features.main

import android.app.Activity
import android.widget.ArrayAdapter
import com.dubedivine.samples.features.common.SearchArrayAdapter
import javax.inject.Inject

/**
 * Created by divine on 2017/09/08.
 */
class QuestionsSearchAdapter @Inject
constructor(context: Activity) :
        SearchArrayAdapter(context, android.R.layout.simple_dropdown_item_1line )