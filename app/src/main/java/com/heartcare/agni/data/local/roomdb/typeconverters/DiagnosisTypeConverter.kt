package com.heartcare.agni.data.local.roomdb.typeconverters

import androidx.room.TypeConverter
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DiagnosisTypeConverter {

    @TypeConverter
    fun fromListToJson(list: List<DiagnosisItem>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json: String?): List<DiagnosisItem>? {
        if (json == null) return null
        val type = object : TypeToken<List<DiagnosisItem>>() {}.type
        return Gson().fromJson(json, type)
    }
}