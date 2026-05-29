package com.heartcare.agni.utils.converters.server.responsemapper

data class ApiContinueResponse<T>(val body: T, val total: Int) : ResponseMapper<T>()