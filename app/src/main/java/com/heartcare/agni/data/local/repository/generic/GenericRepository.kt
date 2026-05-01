package com.heartcare.agni.data.local.repository.generic

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.builders.UUIDBuilder

/**
 *
 * Here we are passing UUID in Parameters due to Unit Testing Scenario.
 * if we generate UUID in repo Unit tests were failing.
 * Do not pass uuid from anywhere else it will automatically generate here.
 *
 */
interface GenericRepository {

    suspend fun insertPatient(
        patientResponse: PatientResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertPrescription(
        prescriptionResponse: PrescriptionResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun updatePrescriptionFhirId(type: GenericTypeEnum)

    suspend fun insertSchedule(
        scheduleResponse: ScheduleResponse,
        type: GenericTypeEnum = GenericTypeEnum.SCHEDULE,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertAppointment(
        appointmentResponse: AppointmentResponse,
        type: GenericTypeEnum = GenericTypeEnum.APPOINTMENT,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertCVDRecord(
        cvdResponse: CVDResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertSymDiag(
        local: DiagnosisData, uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertPriorDxRecord(
        priorDxResponse: PriorDxResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertHistoryMedicationRecord(
        historyMedicationResponse: HistoryMedicationResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertFamilyHistoryRecord(
        familyHistoryResponse: FamilyHistoryResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertAllergyRecord(
        allergyResponse: AllergyResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertRiskFactorRecord(
        riskFactorResponse: RiskFactorResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertTobaccoCessationRecord(
        tobaccoCessationResponse: TobaccoCessationResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertInterventionRecord(
        interventionResponse: InterventionResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertExaminationRecord(
        examinationResponse: ExaminationResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertReferralRecord(
        referralResponse: ReferralResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun updateAppointmentFhirIds(genericTypeEnum: GenericTypeEnum)
    suspend fun updateAppointmentFhirIdInPatch()

    suspend fun updateCVDFhirIds(genericTypeEnum: GenericTypeEnum)
    suspend fun updateVitalFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateDiagnosisFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updatePriorDxFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateHistoryMedicationFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateFamilyHistoryFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateAllergyFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateRiskFactorsFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateTobaccoCessationFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateInterventionFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateExaminationFhirId(genericTypeEnum: GenericTypeEnum)
    suspend fun updateReferralFhirId()

    suspend fun insertOrUpdatePatientPatchEntity(
        patientFhirId: String,
        patientResponse: PatientResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateAppointmentPatch(
        appointmentFhirId: String,
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertVital(
        vitalResponse: VitalResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertPatientLastUpdated(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun removeGenericRecord(id: String): Int
    suspend fun insertDeleteRequest(fhirId: String, typeEnum: GenericTypeEnum, syncType: SyncType): Long

    suspend fun insertOrUpdatePrescriptionPut(
        prescriptionFhirId: String,
        prescriptionResponse: PrescriptionResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateInterventionPut(
        interventionFhirId: String,
        interventionResponse: InterventionResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long

    suspend fun insertOrUpdateExaminationPut(
        examinationFhirId: String,
        examinationResponse: ExaminationResponse,
        uuid: String = UUIDBuilder.generateUUID()
    ): Long
}