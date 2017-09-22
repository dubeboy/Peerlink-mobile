package com.dubedivine.samples.data.model


import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/08/13.
 */
class Answer : Serializable {
    var body: String? = null
        private set
    var votes: Long = 0
        private set
    var isChosen: Boolean = false
        private set
    val createAt = Date()
    var comments: List<Comment>? = null
    var video: Media? = null
    val files: List<Media>? = null
    val id = System.currentTimeMillis()

    constructor() {}

    constructor(body: String, votes: Long, isChosen: Boolean) {
        this.body = body
        this.votes = votes
        this.isChosen = isChosen
    }

    constructor(body: String, votes: Long, isChosen: Boolean, comments: List<Comment>, video: Media) {
        this.body = body
        this.votes = votes
        this.isChosen = isChosen
        this.comments = comments
        this.video = video
    }
}
