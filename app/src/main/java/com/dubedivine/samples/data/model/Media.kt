package com.dubedivine.samples.data.model


import java.io.Serializable
import java.util.*

/**
 * Created by divine on 2017/08/13.
 */
class Media(val name: String, val size: Long, val type: Char  // P-> picture, V-> video, f -> docs
            , val location: String) : Serializable {

    val limit: Int

        get() = 5120

    companion object {
        //enums are very expensive so we use chars instead!!
        var VIDEO_TYPE = 'V'
        var PICTURE_TYPE = 'P'
        var DOCS_TYPE = 'D'
    }
}
