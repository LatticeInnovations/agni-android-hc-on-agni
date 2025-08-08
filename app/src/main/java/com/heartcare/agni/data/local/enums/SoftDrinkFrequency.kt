package com.heartcare.agni.data.local.enums

enum class SoftDrinkFrequency(val code: Int, val display: String) {
    DID_NOT_DRINK(1, "I did not drink carbonated soft drinks during the past 30 days"),
    LESS_THAN_ONCE(2, "Less than once a day"),
    ONCE_PER_DAY(3, "Once a day"),
    TWICE_PER_DAY(4, "2 times per day"),
    THREE_TIMES_PER_DAY(5, "3 times per day"),
    FOUR_TIMES_PER_DAY(6, "4 times per day"),
    FIVE_OR_MORE_TIMES_PER_DAY(7, "5 or more times per day");

    companion object {
        fun listOfSoftDrinkFrequency() = entries.map { it.display }
        fun softDrinkFrequencyCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
        fun softDrinkFrequencyDisplayFromCode(code: Int) = entries.firstOrNull { it.code == code }?.display
    }
}