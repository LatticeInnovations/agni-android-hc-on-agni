package com.heartcare.agni.data.local.enums

enum class KnowEnum(val display: String) {
    KNOW("I know"),
    DO_NOT_KNOW("I don't know");

    companion object {
        fun knowOptions() = entries.map { it.display }
    }
}