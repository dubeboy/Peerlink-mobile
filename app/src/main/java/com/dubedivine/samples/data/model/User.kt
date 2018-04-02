package com.dubedivine.samples.data.model

import java.io.Serializable


data class User(val nickname: String,
                val email: String,
                val photoUrl: String?,
                val degree: String,
                val modules: Set<String>,
                val id: String? = null,
                var tags: List<Tag>? = null) : Serializable {

    constructor(id: String) : this("", "", null, "", emptySet(), id, null)

}


