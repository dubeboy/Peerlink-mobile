package com.dubedivine.samples.data.model


import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/08/13.
 */
data class Comment(val body: String, val votes: Long) : Serializable {
    val createdAt: Date? = null
    val id: String? = null
}
