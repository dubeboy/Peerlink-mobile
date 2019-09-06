package com.dubedivine.samples.data.model


/**
 * Created by divine on 2017/09/23.
 */

// class for responses that just send confirmation back
@Deprecated(message = "use StatusResponse",
        replaceWith = ReplaceWith("StatusResponse", "com.dubedivine.samples.data.model.StatusResponse"))
class Status {
    val status: Boolean = false
}
