package com.dubedivine.samples.features.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import butterknife.ButterKnife
import com.dubedivine.samples.R

class AddQuestionActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        // we need a presenter because we need to fetch the tags of the question
    }

    override val layout: Int
        get() = R.layout.activity_add_question
}
