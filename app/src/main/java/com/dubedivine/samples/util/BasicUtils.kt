package com.dubedivine.samples.util

import android.content.Context
import android.widget.RelativeLayout
import com.dubedivine.samples.data.model.Question
import com.robertlevonyan.views.chip.Chip
import timber.log.Timber

/**
 * Created by divine on 2017/09/12.
 */
object BasicUtils {

    val _3dp = ViewUtil.dpToPx(3)  // what do u think is it cool ? instead of ThreeDp or i could do this val `3dp` = "3dp"
            val `3dp` = "3dp"
    fun createTheStatusTextViewInfo(question: Question): String {
        return if (question.answers?.size != null) {
            Timber.d("the answer is: ${question.answers}")
            "answers ${(question.answers?.size)}, answered by ${question.user?.name}"
        } else {
            "answers 0"
        }
    }

    fun createTagsChip(context: Context, chipText: String): Chip {
        val params = ViewUtil.getLayoutParamsForView()
        params.setMargins(_3dp,0, _3dp, 0)
        val chip = Chip(context)
//        chip.setMargin(_3dp,0, _3dp, 0)
        chip.layoutParams = params
        chip.chipText = "#$chipText" //apend the hash tag yoh!!
        chip.setRandomColor()
        chip.setDefaultDrawableIcon()
        return chip
    }


}