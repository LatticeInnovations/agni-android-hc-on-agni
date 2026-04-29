package com.heartcare.agni.data.local.enums

enum class LevelsEnum(val levelType: String, val display: String) {
    PROVINCE("province", "Province"),
    AREA_COUNCIL("area-council", "Area Council/Municipalities"),
    ISLAND("island", "Island/Wards"),
    VILLAGE("village", "Village");

    companion object {
        fun getLevelsDisplay() = entries.map { it.display }

        fun getCodeFromDisplay(display: String) = entries.find { it.display == display }!!.levelType
    }
}