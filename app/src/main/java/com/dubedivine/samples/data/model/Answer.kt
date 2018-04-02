package com.dubedivine.samples.data.model


import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by divine on 2017/08/13.
 */
class Answer : Serializable {
    var body: String? = null
        private set  // todo: this is ugly y not make it a val
    var votes: Long = 0
        private set
    var isChosen: Boolean = false
        private set
    val createAt = Date()
    var comments: ArrayList<Comment> = ArrayList()
    var video: Media? = null
    val files: List<Media>? = null
    val id: String? = null // UUID
    var user:User? = null

    constructor() {}

    constructor(body: String, votes: Long, isChosen: Boolean, user:User) {
        this.body = body
        this.votes = votes
        this.isChosen = isChosen
        this.user = user
    }

    constructor(body: String, votes: Long, isChosen: Boolean, comments: ArrayList<Comment>, video: Media, user:User) {
        this.body = body
        this.votes = votes
        this.isChosen = isChosen
        this.comments = comments
        this.video = video
        this.user = user
    }
}
