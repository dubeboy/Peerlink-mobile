package com.dubedivine.samples.util

import com.dubedivine.samples.data.model.Question

/**
 * Created by divine on 2017/09/12.
 */
object BasicUtils {
    fun createTheStatusTextViewInfo(question: Question): String {
        if (question?.answers?.size != null) {
            return "answers ${(question.answers.size)}, answered by ${question.user.name}"
        } else {
            return "answers 0"
        }

    }
}