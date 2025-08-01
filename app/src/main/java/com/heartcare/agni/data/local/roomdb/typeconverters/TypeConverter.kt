package com.heartcare.agni.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    internal fun dateToLong(date: Date?): Long? = date?.time

    @TypeConverter
    internal fun longToDate(long: Long?): Date? = long?.let { Date(it) }

    @TypeConverter
    fun fromStringList(list: List<String>?): String = list?.joinToString(separator = ",") ?: ""

    @TypeConverter
    fun toStringList(data: String?): List<String> = data?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}