package com.dubedivine.samples.features.main

import android.app.Activity
import android.widget.ArrayAdapter
import javax.inject.Inject

/**
 * Created by divine on 2017/09/08.
 */
class QuestionsSearchAdapter @Inject
constructor(context: Activity) :
        ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line )