package com.dubedivine.samples.data.model


data class User(val nickname: String,
                 val email: String,
                 val photoUrl: String?,
                 val degree: String,
                val modules: Set<String>,
                 val id: String? = null,
                 var tags: List<Tag>? = null)
