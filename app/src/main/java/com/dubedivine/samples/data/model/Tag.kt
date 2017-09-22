package com.dubedivine.samples.data.model

import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/08/13.
 */


class Tag(// name is the ID means
        val name: String,
        val createAt: Date) : Serializable {
   ;
    private var questions: MutableList<Question>? = null


    fun getQuestions(): List<Question>? {
        return questions
    }

    fun setQuestions(questions: MutableList<Question>) {
        this.questions = questions
    }

    fun addQuestion(q: Question) {
        if (questions != null) {
            questions!!.add(q)
        } else {
            questions = ArrayList()
            questions!!.add(q)
        }
    }
}
