package com.heartcare.agni.data.local.repository.generic

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.patch.AppointmentPatchRequest
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
import com.heartcare.agni.utils.constants.Id.APPOINTMENT_ID
import com.heartcare.agni.utils.constants.Id.APP_UPDATED_DATE
import com.heartcare.agni.utils.constants.Id.PATIENT_ID
import com.heartcare.agni.utils.converters.responseconverter.FHIR.isFhirId
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.fromJson
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.mapToObject
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import java.util.Date

open class GenericRepositoryDatabaseTransactions(
    private val genericDao: GenericDao,
    private val patientDao: PatientDao,
    private val scheduleDao: ScheduleDao,
    private val appointmentDao: AppointmentDao,
    private val campaignScheduleDao: CampaignScheduleDao,
    private val campaignAppointmentDao: CampaignAppointmentDao
) {

    protected suspend fun insertPatientGenericEntity(
        patientResponse: PatientResponse,
        patientGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (patientGenericEntity != null) {
            genericDao.insertGenericEntity(
                patientGenericEntity.copy(payload = patientResponse.copy(appUpdatedDate = Date()).toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientResponse.id,
                    payload = patientResponse.copy(appUpdatedDate = Date()).toJson(),
                    type = GenericTypeEnum.PATIENT,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertPrescriptionGenericEntity(
        prescriptionResponse: PrescriptionResponse,
        prescriptionGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (prescriptionGenericEntity != null) {
            genericDao.insertGenericEntity(
                prescriptionGenericEntity.copy(payload = prescriptionResponse.copy(appUpdatedOn = Date()).toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = prescriptionResponse.prescriptionId!!,
                    payload = prescriptionResponse.copy(appUpdatedOn = Date()).toJson(),
                    type = GenericTypeEnum.PRESCRIPTION,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertPrescriptionPutGenericEntity(
        prescriptionFhirId: String,
        prescriptionResponse: PrescriptionResponse,
        prescriptionGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (prescriptionGenericEntity != null) {
            genericDao.insertGenericEntity(
                prescriptionGenericEntity.copy(
                    payload = prescriptionResponse.copy(appUpdatedOn = Date()).toJson()
                ))[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = prescriptionFhirId,
                    payload = prescriptionResponse.copy(appUpdatedOn = Date()).toJson(),
                    type = GenericTypeEnum.PRESCRIPTION,
                    syncType = SyncType.PUT
                )
            )[0]
        }
    }

    protected suspend fun insertInterventionPutGenericEntity(
        interventionFhirId: String,
        interventionResponse: InterventionResponse,
        interventionGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (interventionGenericEntity != null) {
            genericDao.insertGenericEntity(
                interventionGenericEntity.copy(
                    payload = interventionResponse.toJson()
                ))[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = interventionFhirId,
                    payload = interventionResponse.toJson(),
                    type = GenericTypeEnum.INTERVENTION,
                    syncType = SyncType.PUT
                )
            )[0]
        }
    }

    protected suspend fun insertExaminationPutGenericEntity(
        examinationFhirId: String,
        examinationResponse: ExaminationResponse,
        examinationGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (examinationGenericEntity != null) {
            genericDao.insertGenericEntity(
                examinationGenericEntity.copy(
                    payload = examinationResponse.toJson()
                ))[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = examinationFhirId,
                    payload = examinationResponse.toJson(),
                    type = GenericTypeEnum.EXAMINATION,
                    syncType = SyncType.PUT
                )
            )[0]
        }
    }

    protected suspend fun updateFormPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity: GenericEntity) {
        val existingMap =
            prescriptionGenericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(PrescriptionResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                prescriptionGenericEntity.copy(
                    payload = existingMap.copy(
                        patientFhirId = if (!existingMap.patientFhirId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientFhirId
                        )!! else existingMap.patientFhirId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun insertScheduleGenericEntity(
        scheduleGenericEntity: GenericEntity?,
        scheduleResponse: ScheduleResponse,
        uuid: String,
        type: GenericTypeEnum = GenericTypeEnum.SCHEDULE
    ): Long {
        return if (scheduleGenericEntity != null) {
            genericDao.insertGenericEntity(
                scheduleGenericEntity.copy(payload = scheduleResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = scheduleResponse.uuid,
                    payload = scheduleResponse.toJson(),
                    type = type,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertAppointmentGenericEntity(
        appointmentGenericEntity: GenericEntity?,
        appointmentResponse: AppointmentResponse,
        uuid: String,
        type: GenericTypeEnum = GenericTypeEnum.APPOINTMENT
    ): Long {
        return if (appointmentGenericEntity != null) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(payload = appointmentResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = appointmentResponse.uuid,
                    payload = appointmentResponse.toJson(),
                    type = type,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertCVDGenericEntity(
        cvdGenericEntity: GenericEntity?,
        cvdResponse: CVDResponse,
        uuid: String
    ): Long {
        val type = if (cvdResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_CVD else GenericTypeEnum.CVD
        return if (cvdGenericEntity != null) {
            genericDao.insertGenericEntity(
                cvdGenericEntity.copy(payload = cvdResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = cvdResponse.cvdUuid,
                    payload = cvdResponse.toJson(),
                    type = type,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertVitalGenericEntity(
        vitalResponse: VitalResponse,
        genericEntity: GenericEntity?,
        uuid: String
    ): Long {
        val type = if (vitalResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_VITAL else GenericTypeEnum.VITAL
        return if (genericEntity != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(payload = vitalResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid, patientId = vitalResponse.uuid,
                    payload = vitalResponse.toJson(),
                    type = type,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertSymDiagGenericEntity(
        local: DiagnosisData,
        genericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (genericEntity != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(payload = local.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid, patientId = local.diagnosisUuid,
                    payload = local.toJson(),
                    type = GenericTypeEnum.DIAGNOSIS,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertPriorDxGenericEntity(
        priorDxGenericEntity: GenericEntity?,
        priorDxResponse: PriorDxResponse,
        uuid: String
    ): Long {
        val type = if (priorDxResponse.campaignId != null) GenericTypeEnum.CAMPAIGN_PRIOR_DX else GenericTypeEnum.PRIOR_DX

        return if (priorDxGenericEntity != null) {
            genericDao.insertGenericEntity(
                priorDxGenericEntity.copy(payload = priorDxResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = priorDxResponse.priorDxUuid,
                    payload = priorDxResponse.toJson(),
                    type = type,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertHistoryMedicationGenericEntity(
        historyMedicationGenericEntity: GenericEntity?,
        historyMedicationResponse: HistoryMedicationResponse,
        uuid: String
    ): Long {
        return if (historyMedicationGenericEntity != null) {
            genericDao.insertGenericEntity(
                historyMedicationGenericEntity.copy(payload = historyMedicationResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = historyMedicationResponse.uuid,
                    payload = historyMedicationResponse.toJson(),
                    type = GenericTypeEnum.HISTORY_MEDICATION,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertFamilyHistoryGenericEntity(
        familyHistoryGenericEntity: GenericEntity?,
        familyHistoryResponse: FamilyHistoryResponse,
        uuid: String
    ): Long {
        return if (familyHistoryGenericEntity != null) {
            genericDao.insertGenericEntity(
                familyHistoryGenericEntity.copy(payload = familyHistoryResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = familyHistoryResponse.uuid,
                    payload = familyHistoryResponse.toJson(),
                    type = GenericTypeEnum.FAMILY_HISTORY,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertAllergyGenericEntity(
        allergyGenericEntity: GenericEntity?,
        allergyResponse: AllergyResponse,
        uuid: String
    ): Long {
        return if (allergyGenericEntity != null) {
            genericDao.insertGenericEntity(
                allergyGenericEntity.copy(payload = allergyResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = allergyResponse.uuid,
                    payload = allergyResponse.toJson(),
                    type = GenericTypeEnum.ALLERGY,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertRiskFactorGenericEntity(
        riskFactorGenericEntity: GenericEntity?,
        riskFactorResponse: RiskFactorResponse,
        uuid: String
    ): Long {
        return if (riskFactorGenericEntity != null) {
            genericDao.insertGenericEntity(
                riskFactorGenericEntity.copy(payload = riskFactorResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = riskFactorResponse.uuid,
                    payload = riskFactorResponse.toJson(),
                    type = GenericTypeEnum.RISK_FACTOR,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertTobaccoCessationGenericEntity(
        tobaccoCessationGenericEntity: GenericEntity?,
        tobaccoCessationResponse: TobaccoCessationResponse,
        uuid: String
    ): Long {
        return if (tobaccoCessationGenericEntity != null) {
            genericDao.insertGenericEntity(
                tobaccoCessationGenericEntity.copy(payload = tobaccoCessationResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = tobaccoCessationResponse.uuid,
                    payload = tobaccoCessationResponse.toJson(),
                    type = GenericTypeEnum.TOBACCO_CESSATION,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertInterventionGenericEntity(
        interventionGenericEntity: GenericEntity?,
        interventionResponse: InterventionResponse,
        uuid: String
    ): Long {
        return if (interventionGenericEntity != null) {
            genericDao.insertGenericEntity(
                interventionGenericEntity.copy(payload = interventionResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = interventionResponse.uuid!!,
                    payload = interventionResponse.toJson(),
                    type = GenericTypeEnum.INTERVENTION,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertExaminationGenericEntity(
        examinationGenericEntity: GenericEntity?,
        examinationResponse: ExaminationResponse,
        uuid: String
    ): Long {
        return if (examinationGenericEntity != null) {
            genericDao.insertGenericEntity(
                examinationGenericEntity.copy(payload = examinationResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = examinationResponse.uuid!!,
                    payload = examinationResponse.toJson(),
                    type = GenericTypeEnum.EXAMINATION,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun insertReferralGenericEntity(
        referralGenericEntity: GenericEntity?,
        referralResponse: ReferralResponse,
        uuid: String
    ): Long {
        return if (referralGenericEntity != null) {
            genericDao.insertGenericEntity(
                referralGenericEntity.copy(payload = referralResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = referralResponse.uuid,
                    payload = referralResponse.toJson(),
                    type = GenericTypeEnum.REFERRAL,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }

    protected suspend fun updateAppointmentFhirIdInGenericEntity(appointmentGenericEntity: GenericEntity) {
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(AppointmentResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(
                    payload = existingMap.copy(
                        patientFhirId = if (!existingMap.patientFhirId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientFhirId
                        )!! else existingMap.patientFhirId,
                        scheduleId = if (!existingMap.scheduleId.isFhirId()) getScheduleFhirIdById(
                            existingMap.scheduleId
                        )!! else existingMap.scheduleId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateAppointmentFhirIdInGenericEntityPatch(appointmentGenericEntity: GenericEntity) {
        val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(AppointmentPatchRequest::class.java)
        if (existingMap?.scheduleId != null && !(existingMap.scheduleId.value as String).isFhirId()) {
            genericDao.insertGenericEntity(
                appointmentGenericEntity.copy(
                    payload = existingMap.copy(
                        scheduleId = existingMap.scheduleId.copy(
                            value = getScheduleFhirIdById(existingMap.scheduleId.value)
                        )
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateCVDFhirIdInGenericEntity(cvdGenericEntity: GenericEntity) {
        val existingMap = cvdGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(CVDResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                cvdGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateDiagnosisFhirIdInGenericEntity(genericEntity: GenericEntity) {
        val existingMap =
            genericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(DiagnosisData::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId!!.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }


    protected suspend fun updateVitalFhirIdInGenericEntity(genericEntity: GenericEntity) {
        val existingMap =
            genericEntity.payload.fromJson<MutableMap<String, Any>>()
                .mapToObject(VitalResponse::class.java)
        if (existingMap != null) {
            genericDao.insertGenericEntity(
                genericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }


    protected suspend fun updatePriorDxFhirIdInGenericEntity(priorDxGenericEntity: GenericEntity) {
        val existingMap = priorDxGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(PriorDxResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                priorDxGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateHistoryMedicationFhirIdInGenericEntity(historyMedicationGenericEntity: GenericEntity) {
        val existingMap = historyMedicationGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(HistoryMedicationResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                historyMedicationGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateFamilyHistoryFhirIdInGenericEntity(familyHistoryGenericEntity: GenericEntity) {
        val existingMap = familyHistoryGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(FamilyHistoryResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                familyHistoryGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateAllergyFhirIdInGenericEntity(allergyGenericEntity: GenericEntity) {
        val existingMap = allergyGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(AllergyResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                allergyGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateRiskFactorFhirIdInGenericEntity(riskFactorGenericEntity: GenericEntity) {
        val existingMap = riskFactorGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(RiskFactorResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                riskFactorGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateTobaccoCessationFhirIdInGenericEntity(tobaccoCessationGenericEntity: GenericEntity) {
        val existingMap = tobaccoCessationGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(TobaccoCessationResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                tobaccoCessationGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateInterventionFhirIdInGenericEntity(interventionGenericEntity: GenericEntity) {
        val existingMap = interventionGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(InterventionResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                interventionGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateExaminationFhirIdInGenericEntity(examinationGenericEntity: GenericEntity) {
        val existingMap = examinationGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(ExaminationResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                examinationGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun updateReferralFhirIdInGenericEntity(referralGenericEntity: GenericEntity) {
        val existingMap = referralGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            .mapToObject(ReferralResponse::class.java)

        if (existingMap != null) {
            genericDao.insertGenericEntity(
                referralGenericEntity.copy(
                    payload = existingMap.copy(
                        patientId = if (!existingMap.patientId.isFhirId()) getPatientFhirIdById(
                            existingMap.patientId
                        )!! else existingMap.patientId,
                        appointmentId = if (!existingMap.appointmentId.isFhirId()) getAppointmentFhirIdById(
                            existingMap.appointmentId
                        )!! else existingMap.appointmentId
                    ).toJson()
                )
            )
        }
    }

    protected suspend fun insertOrUpdateAppointmentGenericEntityPatch(
        appointmentGenericEntity: GenericEntity?,
        map: Map<String, Any>,
        patientFhirId: String,
        appointmentFhirId: String,
        uuid: String
    ): Long {
        return if (appointmentGenericEntity != null) {
            val existingMap = appointmentGenericEntity.payload.fromJson<MutableMap<String, Any>>()
            map.entries.forEach { mapEntry ->
                existingMap[mapEntry.key] = mapEntry.value
            }
            existingMap[APP_UPDATED_DATE] = Date()
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = appointmentGenericEntity.id,
                    patientId = appointmentFhirId,
                    payload = existingMap.toJson(),
                    type = GenericTypeEnum.APPOINTMENT,
                    syncType = SyncType.PATCH
                )
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = appointmentFhirId,
                    payload = map.toMutableMap().let { mutableMap ->
                        mutableMap[APPOINTMENT_ID] = appointmentFhirId
                        mutableMap[PATIENT_ID] = patientFhirId
                        mutableMap[APP_UPDATED_DATE] = Date()
                        mutableMap
                    }.toJson(),
                    type = GenericTypeEnum.APPOINTMENT,
                    syncType = SyncType.PATCH
                )
            )[0]
        }
    }


    protected suspend fun insertPatientGenericEntityPatch(
        patientGenericPatchEntity: GenericEntity?,
        patientFhirId: String,
        patientResponse: PatientResponse,
        uuid: String
    ): Long {
        return if (patientGenericPatchEntity != null) {
            /** Insert Updated Map */
            genericDao.insertGenericEntity(
                patientGenericPatchEntity.copy(
                    payload = patientResponse.copy(appUpdatedDate = Date()).toJson()
                ))[0]
        } else {
            /** Insert Freshly Patch data */
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientFhirId,
                    payload = patientResponse.copy(appUpdatedDate = Date()).toJson(),
                    type = GenericTypeEnum.PATIENT,
                    syncType = SyncType.PATCH
                )
            )[0]
        }
    }

    private suspend fun getPatientFhirIdById(patientId: String): String? {
        return patientDao.getPatientDataById(patientId).firstOrNull()?.patientEntity?.fhirId
    }

    private suspend fun getScheduleFhirIdById(scheduleId: String): String? {
        return scheduleDao.getScheduleById(scheduleId).firstOrNull()?.scheduleFhirId
            ?: campaignScheduleDao.getScheduleById(scheduleId).firstOrNull()?.scheduleFhirId
    }

    private suspend fun getAppointmentFhirIdById(appointmentId: String): String? {
        return appointmentDao.getAppointmentById(appointmentId).firstOrNull()?.appointmentFhirId
            ?: campaignAppointmentDao.getAppointmentById(appointmentId).firstOrNull()?.appointmentFhirId
    }

    protected suspend fun insertPatientLastUpdatedGenericEntity(
        patientLastUpdatedResponse: PatientLastUpdatedResponse,
        patientLastUpdatedGenericEntity: GenericEntity?,
        uuid: String
    ): Long {
        return if (patientLastUpdatedGenericEntity != null) {
            genericDao.insertGenericEntity(
                patientLastUpdatedGenericEntity.copy(payload = patientLastUpdatedResponse.toJson())
            )[0]
        } else {
            genericDao.insertGenericEntity(
                GenericEntity(
                    id = uuid,
                    patientId = patientLastUpdatedResponse.uuid,
                    payload = patientLastUpdatedResponse.toJson(),
                    type = GenericTypeEnum.LAST_UPDATED,
                    syncType = SyncType.POST
                )
            )[0]
        }
    }


}