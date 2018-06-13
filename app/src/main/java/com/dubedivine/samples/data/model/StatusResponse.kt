package com.dubedivine.samples.data.model

/**
 * Created by divine on 2017/10/01.
 */
data class  StatusResponse<T>(var status: Boolean? = null, var message: String? = null, var entity: T? = null)