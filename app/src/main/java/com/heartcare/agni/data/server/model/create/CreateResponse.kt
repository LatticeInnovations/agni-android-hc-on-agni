package com.heartcare.agni.data.server.model.create

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateResponse(
    val status: String,
    val fhirId: String?,
    val id: String?,
    @SerializedName("err")
    val error: String?,
    val prescription: List<MedReqIdResponse>?,
    val prescriptionFiles: List<DocumentIdResponse>?,
    val medicineDispensedList: List<MedDispenseIdResponse>?,
    val files: List<*>?,
)
