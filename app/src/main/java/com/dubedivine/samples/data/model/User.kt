package com.dubedivine.samples.data.model

class User(val name: String, val email: String, var tags: List<Tag>?) {
    val id: String? = null  // initialised by my faithful mongodb!!
}
