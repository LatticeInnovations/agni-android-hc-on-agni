package com.heartcare.agni.utils.regex

object NameRegex {
    val nameRegex = Regex("^(?!.* {2})[A-Za-z ]+$")
}