package com.heartcare.agni.data.local.enums

enum class FatType(val code: Int, val display: String) {
    VEGETABLE_OIL(1, "Vegetable Oil"),
    LARD_OR_SUET(2, "Lard or suet"),
    BUTTER_OR_GHEE(3, "Butter or ghee"),
    MARGARINE(4, "Margarine"),
    OTHERS(5, "Others"),
    NONE_USED(6, "None used"),
    DO_NOT_KNOW(7, "Don’t know");

    companion object {
        fun listOfFatType() = entries.map { it.display }
        fun fatTypeCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}