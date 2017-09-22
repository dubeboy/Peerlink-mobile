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
    fun createTheStatusTextViewInfo(question: Question): String {
        return if (question.answers?.size != null) {
            Timber.d("the answer is: ${question.answers}")
            "answers ${(question.answers?.size)}, answered by ${question.user?.name}"
        } else {
            "answers 0"
        }
    }

    fun createChipFromCode(context: Context, chipText: String): Chip {
        val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        val chip = Chip(context)
        chip.layoutParams = params
        chip.chipText = chipText
        chip.setRandomColor()
        chip.setDefaultDrawableIcon()
        return chip
    }
}