package com.heartcare.agni.data.local.repository.generic

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignAppointmentDao
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
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
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import javax.inject.Inject

/**
 *
 * Here we are passing UUID in Parameters due to Unit Testing Scenario.
 * if we generate UUID in repo Unit tests were failing.
 * Do not pass uuid from anywhere else it will automatically generate here.
 *
 */
class GenericRepositoryImpl @Inject constructor(
    private val genericDao: GenericDao,
    patientDao: PatientDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao,
    campaignScheduleDao: CampaignScheduleDao,
    campaignAppointmentDao: CampaignAppointmentDao
) : GenericRepository,
    GenericRepositoryDatabaseTransactions(
        genericDao,
        patientDao,
        scheduleDao,
        appointmentDao,
        campaignScheduleDao,
        campaignAppointmentDao
    ) {

    override suspend fun insertPatient(patientResponse: PatientResponse, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = patientResponse.id,
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.POST
        ).let { patientGenericEntity ->
            insertPatientGenericEntity(patientResponse, patientGenericEntity, uuid)
        }
    }

    override suspend fun insertPrescription(
        prescriptionResponse: PrescriptionResponse,
        uuid: String
    ): Long {
        val type = if (prescriptionResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_PRESCRIPTION else GenericTypeEnum.PRESCRIPTION
        return genericDao.getGenericEntityById(
            patientId = prescriptionResponse.prescriptionId!!,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { prescriptionGenericEntity ->
            insertPrescriptionGenericEntity(prescriptionResponse, prescriptionGenericEntity, uuid, type)
        }
    }

    override suspend fun updatePrescriptionFhirId(type: GenericTypeEnum) {
        genericDao.getNotSyncedData(type)
            .forEach { prescriptionGenericEntity ->
                updateFormPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
            }
    }

    override suspend fun insertSchedule(scheduleResponse: ScheduleResponse, type: GenericTypeEnum, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = scheduleResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { scheduleGenericEntity ->
            insertScheduleGenericEntity(scheduleGenericEntity, scheduleResponse, uuid, type)
        }
    }

    override suspend fun updateAppointmentFhirIds(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { appointmentGenericEntity ->
                updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity)
            }
    }

    override suspend fun updateAppointmentFhirIdInPatch() {
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT, SyncType.PATCH)
            .forEach { appointmentGenericEntity ->
                updateAppointmentFhirIdInGenericEntityPatch(appointmentGenericEntity)
            }
    }

    override suspend fun updateCVDFhirIds(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { cvdGenericEntity ->
                updateCVDFhirIdInGenericEntity(cvdGenericEntity)
            }
    }
    override suspend fun updateVitalFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { vitalGenericEntity ->
                updateVitalFhirIdInGenericEntity(vitalGenericEntity)
            }
    }
    override suspend fun updateDiagnosisFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { diagnosisGenericEntity ->
                updateDiagnosisFhirIdInGenericEntity(diagnosisGenericEntity)
            }
    }

    override suspend fun updatePriorDxFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { priorDxGenericEntity ->
                updatePriorDxFhirIdInGenericEntity(priorDxGenericEntity)
            }
    }

    override suspend fun updateHistoryMedicationFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { historyMedicationGenericEntity ->
                updateHistoryMedicationFhirIdInGenericEntity(historyMedicationGenericEntity)
            }
    }

    override suspend fun updateFamilyHistoryFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { familyHistoryGenericEntity ->
                updateFamilyHistoryFhirIdInGenericEntity(familyHistoryGenericEntity)
            }
    }

    override suspend fun updateAllergyFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { allergyGenericEntity ->
                updateAllergyFhirIdInGenericEntity(allergyGenericEntity)
            }
    }

    override suspend fun updateRiskFactorsFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { riskFactoGenericEntity ->
                updateRiskFactorFhirIdInGenericEntity(riskFactoGenericEntity)
            }
    }

    override suspend fun updateTobaccoCessationFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { tobaccoCessationGenericEntity ->
                updateTobaccoCessationFhirIdInGenericEntity(tobaccoCessationGenericEntity)
            }
    }

    override suspend fun updateInterventionFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { interventionGenericEntity ->
                updateInterventionFhirIdInGenericEntity(interventionGenericEntity)
            }
    }

    override suspend fun updateExaminationFhirId(genericTypeEnum: GenericTypeEnum) {
        genericDao.getNotSyncedData(genericTypeEnum)
            .forEach { examinationGenericEntity ->
                updateExaminationFhirIdInGenericEntity(examinationGenericEntity)
            }
    }

    override suspend fun updateReferralFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.REFERRAL)
            .forEach { referralGenericEntity ->
                updateReferralFhirIdInGenericEntity(referralGenericEntity)
            }
    }

    override suspend fun insertAppointment(
        appointmentResponse: AppointmentResponse,
        type: GenericTypeEnum,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = appointmentResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { appointmentGenericEntity ->
            insertAppointmentGenericEntity(appointmentGenericEntity, appointmentResponse, uuid, type)
        }
    }

    override suspend fun insertCVDRecord(
        cvdResponse: CVDResponse,
        uuid: String
    ): Long {
        val type = if (cvdResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_CVD else GenericTypeEnum.CVD
        return genericDao.getGenericEntityById(
            patientId = cvdResponse.cvdUuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { cvdGenericEntity ->
            insertCVDGenericEntity(cvdGenericEntity, cvdResponse, uuid)
        }
    }

    override suspend fun insertSymDiag(local: DiagnosisData, uuid: String): Long {
        val type = if (local.campaignId != null) GenericTypeEnum.CAMPAIGN_DIAGNOSIS else GenericTypeEnum.DIAGNOSIS
        return genericDao.getGenericEntityById(
            patientId = local.diagnosisUuid,
            genericTypeEnum =type,
            syncType = SyncType.POST
        ).let {
            insertSymDiagGenericEntity(local, it, uuid,type)
        }
    }

    override suspend fun insertPriorDxRecord(priorDxResponse: PriorDxResponse, uuid: String): Long {
        val type = if (priorDxResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_PRIOR_DX else GenericTypeEnum.PRIOR_DX
        return genericDao.getGenericEntityById(
            patientId = priorDxResponse.priorDxUuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { priorDxGenericEntity ->
            insertPriorDxGenericEntity(priorDxGenericEntity, priorDxResponse, uuid)
        }
    }

    override suspend fun insertHistoryMedicationRecord(
        historyMedicationResponse: HistoryMedicationResponse,
        uuid: String
    ): Long {
        val type = if (historyMedicationResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_HISTORY_MEDICATION else GenericTypeEnum.HISTORY_MEDICATION
        return genericDao.getGenericEntityById(
            patientId = historyMedicationResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { historyMedicationGenericEntity ->
            insertHistoryMedicationGenericEntity(historyMedicationGenericEntity, historyMedicationResponse, uuid, type)
        }
    }

    override suspend fun insertFamilyHistoryRecord(
        familyHistoryResponse: FamilyHistoryResponse,
        uuid: String
    ): Long {
        val type = if (familyHistoryResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_FAMILY_HISTORY else GenericTypeEnum.FAMILY_HISTORY

        return genericDao.getGenericEntityById(
            patientId = familyHistoryResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { familyHistoryGenericEntity ->
            insertFamilyHistoryGenericEntity(familyHistoryGenericEntity, familyHistoryResponse, uuid, type)
        }
    }

    override suspend fun insertAllergyRecord(
        allergyResponse: AllergyResponse,
        uuid: String
    ): Long {
        val type = if (allergyResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_ALLERGY else GenericTypeEnum.ALLERGY

        return genericDao.getGenericEntityById(
            patientId = allergyResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { allergyGenericEntity ->
            insertAllergyGenericEntity(allergyGenericEntity, allergyResponse, uuid, type)
        }
    }

    override suspend fun insertRiskFactorRecord(
        riskFactorResponse: RiskFactorResponse,
        uuid: String
    ): Long {
        val type = if (riskFactorResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_RISK_FACTORS else GenericTypeEnum.RISK_FACTOR

        return genericDao.getGenericEntityById(
            patientId = riskFactorResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { riskFactorGenericEntity ->
            insertRiskFactorGenericEntity(riskFactorGenericEntity, riskFactorResponse, uuid, type)
        }
    }

    override suspend fun insertTobaccoCessationRecord(
        tobaccoCessationResponse: TobaccoCessationResponse,
        uuid: String
    ): Long {
        val type = if (tobaccoCessationResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_TOBACCO_CESSATION else GenericTypeEnum.TOBACCO_CESSATION


        return genericDao.getGenericEntityById(
            patientId = tobaccoCessationResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { tobaccoCessationGenericEntity ->
            insertTobaccoCessationGenericEntity(tobaccoCessationGenericEntity, tobaccoCessationResponse, uuid, type)
        }
    }

    override suspend fun insertInterventionRecord(
        interventionResponse: InterventionResponse,
        uuid: String
    ): Long {
        val type = if (interventionResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_INTERVENTION else GenericTypeEnum.INTERVENTION
        return genericDao.getGenericEntityById(
            patientId = interventionResponse.uuid!!,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { interventionGenericEntity ->
            insertInterventionGenericEntity(interventionGenericEntity, interventionResponse, uuid, type)
        }
    }

    override suspend fun insertExaminationRecord(
        examinationResponse: ExaminationResponse,
        uuid: String
    ): Long {
        val type = if (examinationResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_EXAMINATION else GenericTypeEnum.EXAMINATION
        return genericDao.getGenericEntityById(
            patientId = examinationResponse.uuid!!,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let { examinationGenericEntity ->
            insertExaminationGenericEntity(examinationGenericEntity, examinationResponse, uuid, type)
        }
    }

    override suspend fun insertReferralRecord(
        referralResponse: ReferralResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = referralResponse.uuid,
            genericTypeEnum = GenericTypeEnum.REFERRAL,
            syncType = SyncType.POST
        ).let { referralGenericEntity ->
            insertReferralGenericEntity(referralGenericEntity, referralResponse, uuid)
        }
    }

    override suspend fun insertVital(
        vitalResponse: VitalResponse,
        uuid: String
    ): Long {
        val type = if (vitalResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_VITAL else GenericTypeEnum.VITAL
        return genericDao.getGenericEntityById(
            patientId = vitalResponse.uuid,
            genericTypeEnum = type,
            syncType = SyncType.POST
        ).let {
            insertVitalGenericEntity(vitalResponse, it, uuid)
        }
    }


    override suspend fun insertOrUpdatePatientPatchEntity(
        patientFhirId: String,
        patientResponse: PatientResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientFhirId,
            genericTypeEnum = GenericTypeEnum.PATIENT,
            syncType = SyncType.PATCH
        ).let { patientPatchGenericEntity ->
            insertPatientGenericEntityPatch(patientPatchGenericEntity, patientFhirId, patientResponse, uuid)
        }
    }

    override suspend fun insertOrUpdateAppointmentPatch(
        appointmentFhirId: String,
        patientFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            appointmentFhirId,
            GenericTypeEnum.APPOINTMENT,
            SyncType.PATCH
        ).let { appointmentGenericEntity ->
            insertOrUpdateAppointmentGenericEntityPatch(
                appointmentGenericEntity,
                map,
                patientFhirId,
                appointmentFhirId,
                uuid
            )
        }
    }

    override suspend fun insertPatientLastUpdated(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = patientLastUpdatedResponse.uuid,
            genericTypeEnum = GenericTypeEnum.LAST_UPDATED,
            syncType = SyncType.POST
        ).let { patientLastUpdatedGenericEntity ->
            insertPatientLastUpdatedGenericEntity(
                patientLastUpdatedResponse,
                patientLastUpdatedGenericEntity,
                uuid
            )
        }
    }
    
    override suspend fun removeGenericRecord(id: String): Int {
        return genericDao.removeGenericRecord(id)
    }

    override suspend fun insertDeleteRequest(
        fhirId: String,
        typeEnum: GenericTypeEnum,
        syncType: SyncType
    ): Long {
        return genericDao.insertGenericEntity(
            GenericEntity(
                id = UUIDBuilder.generateUUID(),
                patientId = fhirId,
                payload = fhirId.toJson(),
                type = typeEnum,
                syncType = syncType
            )
        )[0]
    }

    override suspend fun insertOrUpdatePrescriptionPut(
        prescriptionFhirId: String,
        prescriptionResponse: PrescriptionResponse,
        uuid: String
    ): Long {
        val type = if (prescriptionResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_PRESCRIPTION else GenericTypeEnum.PRESCRIPTION

        return genericDao.getGenericEntityById(
            patientId = prescriptionFhirId,
            genericTypeEnum = type,
            syncType = SyncType.PUT
        ).let { prescriptionGenericEntity ->
            insertPrescriptionPutGenericEntity(prescriptionFhirId, prescriptionResponse, prescriptionGenericEntity, uuid,type)
        }
    }

    override suspend fun insertOrUpdateInterventionPut(
        interventionFhirId: String,
        interventionResponse: InterventionResponse,
        uuid: String
    ): Long {
        val type = if (interventionResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_INTERVENTION else GenericTypeEnum.INTERVENTION
        return genericDao.getGenericEntityById(
            patientId = interventionFhirId,
            genericTypeEnum = type,
            syncType = SyncType.PUT
        ).let { interventionGenericEntity ->
            insertInterventionPutGenericEntity(interventionFhirId, interventionResponse, interventionGenericEntity, uuid, type)
        }
    }

    override suspend fun insertOrUpdateExaminationPut(
        examinationFhirId: String,
        examinationResponse: ExaminationResponse,
        uuid: String
    ): Long {
        val type = if (examinationResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_EXAMINATION else GenericTypeEnum.EXAMINATION
        return genericDao.getGenericEntityById(
            patientId = examinationFhirId,
            genericTypeEnum = type,
            syncType = SyncType.PUT
        ).let { examinationGenericEntity ->
            insertExaminationPutGenericEntity(examinationFhirId, examinationResponse, examinationGenericEntity, uuid, type)
        }
    }
}