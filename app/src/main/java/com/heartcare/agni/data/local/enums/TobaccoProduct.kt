package com.heartcare.agni.data.local.enums

enum class TobaccoProduct(val code: Int, val display: String) {
    CIGARETTES(1, "Cigarettes"),
    CIGARS(2, "Cigars"),
    PIPES(3, "Pipes"),
    BIDIS_KRETEKS(4, "Bidis and Kreteks (clove cigarettes)"),
    HOOKAH(5, "Hookah"),
    CHEWING_TOBACCO(6, "Chewing tobacco"),
    SNUFF(7, "Snuff (moist and dry)"),
    DISSOLVABLES(8, "Dissolvables (lozenges, orbs, sticks, strips)"),
    ELECTRONIC_CIGARETTES(9, "Electronic cigarettes"),
    OTHER(10, "Others");

    companion object {
        fun listOfTobaccoProducts() = entries.map { it.display }
    }
}