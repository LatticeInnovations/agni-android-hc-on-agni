package com.heartcare.agni.data.local.enums

enum class SymbolEnum (val symbol: String) {
    NEGATIVE("-"),
    POSITIVE_MINUS("+-"),
    POSITIVE("+"),
    POSITIVE_DOUBLE("++"),
    POSITIVE_TRIPLE("+++"),
    POSITIVE_QUADRUPLE("++++");

    companion object {
        fun getSymbolList() = entries.map { it.symbol }
    }
}