package com.heartcare.agni.data.server.repository.sync

import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicineTimeResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

interface SyncRepository {

    suspend fun getAndInsertListPatientData(offset: Int): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertPatientDataById(id: String): ResponseMapper<List<PatientResponse>>
    suspend fun getAndInsertFormPrescription(patientId: String?): ResponseMapper<List<PrescriptionResponse>>
    suspend fun getAndInsertMedication(offset: Int): ResponseMapper<List<MedicationResponse>>
    suspend fun getAndInsertInterventionMaster(offset: Int): ResponseMapper<List<InterventionMasterResponse>>
    suspend fun getAndInsertExaminationMaster(offset: Int): ResponseMapper<List<ExaminationMasterResponse>>
    suspend fun getMedicineTime(): ResponseMapper<List<MedicineTimeResponse>>
    suspend fun getAndInsertSchedule(offset: Int): ResponseMapper<List<ScheduleResponse>>
    suspend fun getAndInsertAppointment(offset: Int): ResponseMapper<List<AppointmentResponse>>
    suspend fun getAndInsertPatientLastUpdatedData(): ResponseMapper<List<PatientLastUpdatedResponse>>
    suspend fun getAndInsertCVD(offset: Int): ResponseMapper<List<CVDResponse>>
    suspend fun getAndInsertListVitalData(offset: Int): ResponseMapper<List<VitalResponse>>
    suspend fun getAndInsertListDiagnosisData(offset: Int): ResponseMapper<List<DiagnosisResponse>>
    suspend fun getAndInsertLevelsData(offset: Int): ResponseMapper<List<LevelResponse>>
    suspend fun getAndInsertPriorDxData(offset: Int): ResponseMapper<List<PriorDxResponse>>
    suspend fun getAndInsertHistoryMedicationData(offset: Int): ResponseMapper<List<HistoryMedicationResponse>>
    suspend fun getAndInsertFamilyHistoryData(offset: Int): ResponseMapper<List<FamilyHistoryResponse>>
    suspend fun getAndInsertAllergyData(offset: Int): ResponseMapper<List<AllergyResponse>>
    suspend fun getAndInsertRiskFactorData(offset: Int): ResponseMapper<List<RiskFactorResponse>>
    suspend fun getAndInsertTobaccoCessationData(offset: Int): ResponseMapper<List<TobaccoCessationResponse>>
    suspend fun getAndInsertInterventionsData(offset: Int): ResponseMapper<List<InterventionResponse>>
    suspend fun getAndInsertExaminationData(offset: Int): ResponseMapper<List<ExaminationResponse>>

    //POST
    suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendFormPrescriptionPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendSchedulePostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAppointmentPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPatientLastUpdatePostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendCVDPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendVitalPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendDiagnosisPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPriorDxPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendHistoryMedicationPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendFamilyHistoryPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAllergyPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendRiskFactorPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendTobaccoCessationPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendInterventionPostData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendExaminationPostData(): ResponseMapper<List<CreateResponse>>

    //PATCH
    suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendAppointmentPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendCVDPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendVitalPatchData(): ResponseMapper<List<CreateResponse>>
    suspend fun sendPrescriptionPutData(): ResponseMapper<List<CreateResponse>>
    suspend fun sentInterventionPutData(): ResponseMapper<List<CreateResponse>>
    suspend fun sentExaminationPutData(): ResponseMapper<List<CreateResponse>>
}