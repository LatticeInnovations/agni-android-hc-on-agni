package com.heartcare.agni.ui.prescription.filldetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.medication.MedicationRepository
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FillDetailsViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var medicationSelected by mutableStateOf<MedicationResponse?>(null)

    var medSelected by mutableStateOf("")

    var quantityPerDose by mutableStateOf("1.0")
    var frequency by mutableStateOf("1")
    var timing by mutableStateOf("")
    var duration by mutableStateOf("")
    var notes by mutableStateOf("")
    var medUnit by mutableStateOf("")
    var medDoseForm by mutableStateOf("")
    var medFhirId by mutableStateOf("")
    var isDurationInvalid by mutableStateOf(false)

    var selectedBrand by mutableStateOf("")

    val qtyRange = listOf(
        "0.5",
        "1.0",
        "1.5",
        "2.0",
        "2.5",
        "3.0",
        "3.5",
        "4.0",
        "4.5",
        "5.0"
    )

    val frequencyRange = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7"
    )

    internal fun quantityPrescribed(): String {
        return if (duration.isBlank() || isDurationInvalid) ""
        else (quantityPerDose.toDouble() * frequency.toInt() * duration.toInt()).toString()
    }

    internal fun reset() {
        medSelected = ""
        quantityPerDose = "1.0"
        frequency = "1"
        duration = ""
        notes = ""
        timing = ""
        medFhirId = ""
        medDoseForm = ""
        medUnit = ""
        isDurationInvalid = false
        medicationSelected = null
        selectedBrand = ""
    }

    internal fun getMedicationByMedFhirId(
        medFhirId: String,
        formulationsList: (List<MedicationResponse>) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            formulationsList(
                medicationRepository.getMedicationByMedFhirId(medFhirId)
            )
        }
    }
}