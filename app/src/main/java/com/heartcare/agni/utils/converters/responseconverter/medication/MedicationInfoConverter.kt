package com.heartcare.agni.utils.converters.responseconverter.medication

import android.content.Context
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.MedFrequencyEnum
import java.util.Locale

object MedicationInfoConverter {
    internal fun getMedInfo(
        frequency: Int,
        medUnit: String,
        timing: String?,
        note: String?,
        qtyPerDose: Double,
        duration: Int,
        qtyPrescribed: Double,
        context: Context
    ): String {
        return context.getString(
            R.string.med_info,
            String.format(Locale.getDefault(), "%.1f", qtyPerDose),
            medUnit,
            getMedFreqValue(frequency, context),
            if (timing?.isNotEmpty() == true) context.getString(R.string.timing, timing) else "",
            duration,
            String.format(Locale.getDefault(), "%.1f", qtyPrescribed),
            if (note?.isNotEmpty() == true) context.getString(R.string.notes, note) else ""
        )
    }

    private fun getMedFreqValue(freq: Int, context: Context): String {
        return when (freq <= 4) {
            true -> MedFrequencyEnum.fromInt(freq).value
            else -> context.getString(R.string.freq_dose_per_day, freq)
        }
    }
}