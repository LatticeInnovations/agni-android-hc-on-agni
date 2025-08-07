package com.heartcare.agni.data.local.enums

enum class SaltAmountEnum(val code: Int, val display: String) {
    FAR_TOO_LITTLE(1, "Far too little"),
    TOO_LITTLE(2, "Too little"),
    JUST_RIGHT(3, "Just the right amount"),
    TOO_MUCH(4, "Too much"),
    FAR_TOO_MUCH(5, "Far too much"),
    DO_NOT_KNOW(6, "Don't know");

    companion object {
        fun listOfSaltAmount() = entries.map { it.display }
        fun saltAmountCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}