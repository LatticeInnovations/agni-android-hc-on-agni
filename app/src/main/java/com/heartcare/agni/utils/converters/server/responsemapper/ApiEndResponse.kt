package com.heartcare.agni.utils.converters.server.responsemapper

import okhttp3.Headers

data class ApiEndResponse<T>(
    val body: T,
    val headers: Headers? = null,
    val total: Int
) : ResponseMapper<T>()
