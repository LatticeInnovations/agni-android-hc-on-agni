package com.heartcare.agni.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import com.heartcare.agni.FhirApp.Companion.gson
import com.heartcare.agni.data.local.roomdb.entities.campaign.StaffEntity
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

    @TypeConverter
    fun fromStaffList(list: List<StaffEntity>?): String = list?.let { gson.toJson(it) } ?: ""

    @TypeConverter
    fun toStaffList(data: String?): List<StaffEntity> = if (data.isNullOrEmpty()) emptyList() else {
        val type = object : com.google.gson.reflect.TypeToken<List<StaffEntity>>() {}.type
        gson.fromJson(data, type)
    }
}