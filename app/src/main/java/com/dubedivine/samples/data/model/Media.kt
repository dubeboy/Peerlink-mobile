package com.dubedivine.samples.data.model


import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/08/13.
 */
// P-> picture, V-> video, f -> docs
class Media(val name: String,
            val size: Long,
            val type: Char,
            var location: String) : Serializable {

    val limit: Int
        get() = 5120

    companion object {
        //enums are very expensive so we use chars instead!!
        const val VIDEO_TYPE = 'V'
        const val PICTURE_TYPE = 'P'
        const val DOCS_TYPE = 'D'
    }
}
