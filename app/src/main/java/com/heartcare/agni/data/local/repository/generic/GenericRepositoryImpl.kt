package com.heartcare.agni.data.local.repository.generic

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.dispense.request.MedicineDispenseRequest
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.photo.PrescriptionPhotoPatch
import com.heartcare.agni.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import timber.log.Timber
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
    prescriptionDao: PrescriptionDao
) : GenericRepository,
    GenericRepositoryDatabaseTransactions(genericDao, patientDao, scheduleDao, appointmentDao, prescriptionDao) {

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
        return genericDao.getGenericEntityById(
            patientId = prescriptionResponse.prescriptionId!!,
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
            syncType = SyncType.POST
        ).let { prescriptionGenericEntity ->
            insertPrescriptionGenericEntity(prescriptionResponse, prescriptionGenericEntity, uuid)
        }
    }

    override suspend fun insertPhotoPrescription(
        prescriptionPhotoResponse: PrescriptionPhotoResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = prescriptionPhotoResponse.prescriptionId,
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
            syncType = SyncType.POST
        ).let { prescriptionGenericEntity ->
            insertPrescriptionPhotoGenericEntity(
                prescriptionPhotoResponse,
                prescriptionGenericEntity,
                uuid
            )
        }
    }

    override suspend fun updatePrescriptionFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION)
            .forEach { prescriptionGenericEntity ->
                updateFormPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
            }
        genericDao.getNotSyncedData(GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE)
            .forEach { prescriptionGenericEntity ->
                updatePhotoPrescriptionFhirIdInGenericEntity(prescriptionGenericEntity)
            }
    }

    override suspend fun insertSchedule(scheduleResponse: ScheduleResponse, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = scheduleResponse.uuid,
            genericTypeEnum = GenericTypeEnum.SCHEDULE,
            syncType = SyncType.POST
        ).let { scheduleGenericEntity ->
            insertScheduleGenericEntity(scheduleGenericEntity, scheduleResponse, uuid)
        }
    }

    override suspend fun updateAppointmentFhirIds() {
        genericDao.getNotSyncedData(GenericTypeEnum.APPOINTMENT)
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

    override suspend fun updateCVDFhirIds() {
        genericDao.getNotSyncedData(GenericTypeEnum.CVD)
            .forEach { cvdGenericEntity ->
                updateCVDFhirIdInGenericEntity(cvdGenericEntity)
            }
    }
    override suspend fun updateVitalFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.VITAL)
            .forEach { vitalGenericEntity ->
                updateVitalFhirIdInGenericEntity(vitalGenericEntity)
            }
    }
    override suspend fun updateSymDiagFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.SYMPTOMS_DIAGNOSIS)
            .forEach { symDiagGenericEntity ->
                updateSymDiagFhirIdInGenericEntity(symDiagGenericEntity)
            }
    }
    override suspend fun updateLabTestFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.LAB_TEST)
            .forEach { genericEntity ->
                updateLabTestFhirIdInGenericEntity(genericEntity)
            }
    }

    override suspend fun updateMedRecordFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.MEDICAL_RECORD)
            .forEach { genericEntity ->
                updateMedicalRecordFhirIdInGenericEntity(genericEntity)
            }
    }

    override suspend fun updateDispenseFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.DISPENSE)
            .forEach { dispenseGenericEntity ->
                updateDispenseFhirIdInGenericEntity(dispenseGenericEntity)
            }
    }

    override suspend fun updateImmunizationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.IMMUNIZATION)
            .forEach { immunizationGenericEntity ->
                updateImmunizationFhirIdInGenericEntity(immunizationGenericEntity)
            }
    }

    override suspend fun updatePriorDxFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.PRIOR_DX)
            .forEach { priorDxGenericEntity ->
                updatePriorDxFhirIdInGenericEntity(priorDxGenericEntity)
            }
    }

    override suspend fun updateHistoryMedicationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.HISTORY_MEDICATION)
            .forEach { historyMedicationGenericEntity ->
                updateHistoryMedicationFhirIdInGenericEntity(historyMedicationGenericEntity)
            }
    }

    override suspend fun updateFamilyHistoryFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.FAMILY_HISTORY)
            .forEach { familyHistoryGenericEntity ->
                updateFamilyHistoryFhirIdInGenericEntity(familyHistoryGenericEntity)
            }
    }

    override suspend fun updateAllergyFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.ALLERGY)
            .forEach { allergyGenericEntity ->
                updateAllergyFhirIdInGenericEntity(allergyGenericEntity)
            }
    }

    override suspend fun updateRiskFactorsFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.RISK_FACTOR)
            .forEach { riskFactoGenericEntity ->
                updateRiskFactorFhirIdInGenericEntity(riskFactoGenericEntity)
            }
    }

    override suspend fun updateTobaccoCessationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.TOBACCO_CESSATION)
            .forEach { tobaccoCessationGenericEntity ->
                updateTobaccoCessationFhirIdInGenericEntity(tobaccoCessationGenericEntity)
            }
    }

    override suspend fun updateInterventionFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.INTERVENTION)
            .forEach { interventionGenericEntity ->
                updateInterventionFhirIdInGenericEntity(interventionGenericEntity)
            }
    }

    override suspend fun updateExaminationFhirId() {
        genericDao.getNotSyncedData(GenericTypeEnum.EXAMINATION)
            .forEach { examinationGenericEntity ->
                updateExaminationFhirIdInGenericEntity(examinationGenericEntity)
            }
    }

    override suspend fun insertAppointment(
        appointmentResponse: AppointmentResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = appointmentResponse.uuid,
            genericTypeEnum = GenericTypeEnum.APPOINTMENT,
            syncType = SyncType.POST
        ).let { appointmentGenericEntity ->
            insertAppointmentGenericEntity(appointmentGenericEntity, appointmentResponse, uuid)
        }
    }

    override suspend fun insertCVDRecord(
        cvdResponse: CVDResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = cvdResponse.cvdUuid,
            genericTypeEnum = GenericTypeEnum.CVD,
            syncType = SyncType.POST
        ).let { cvdGenericEntity ->
            insertCVDGenericEntity(cvdGenericEntity, cvdResponse, uuid)
        }
    }

    override suspend fun insertSymDiag(local: SymptomsAndDiagnosisData, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = local.symDiagUuid,
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS,
            syncType = SyncType.POST
        ).let {
            insertSymDiagGenericEntity(local, it, uuid)
        }
    }

    override suspend fun insertDispense(
        medicineDispenseRequest: MedicineDispenseRequest,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = medicineDispenseRequest.dispenseId,
            genericTypeEnum = GenericTypeEnum.DISPENSE,
            syncType = SyncType.POST
        ).let { dispenseGenericEntity ->
            insertDispenseGenericEntity(medicineDispenseRequest, dispenseGenericEntity, uuid)
        }
    }

    override suspend fun insertPriorDxRecord(priorDxResponse: PriorDxResponse, uuid: String): Long {
        return genericDao.getGenericEntityById(
            patientId = priorDxResponse.priorDxUuid,
            genericTypeEnum = GenericTypeEnum.PRIOR_DX,
            syncType = SyncType.POST
        ).let { priorDxGenericEntity ->
            insertPriorDxGenericEntity(priorDxGenericEntity, priorDxResponse, uuid)
        }
    }

    override suspend fun insertHistoryMedicationRecord(
        historyMedicationResponse: HistoryMedicationResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = historyMedicationResponse.uuid,
            genericTypeEnum = GenericTypeEnum.HISTORY_MEDICATION,
            syncType = SyncType.POST
        ).let { historyMedicationGenericEntity ->
            insertHistoryMedicationGenericEntity(historyMedicationGenericEntity, historyMedicationResponse, uuid)
        }
    }

    override suspend fun insertFamilyHistoryRecord(
        familyHistoryResponse: FamilyHistoryResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = familyHistoryResponse.uuid,
            genericTypeEnum = GenericTypeEnum.FAMILY_HISTORY,
            syncType = SyncType.POST
        ).let { familyHistoryGenericEntity ->
            insertFamilyHistoryGenericEntity(familyHistoryGenericEntity, familyHistoryResponse, uuid)
        }
    }

    override suspend fun insertAllergyRecord(
        allergyResponse: AllergyResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = allergyResponse.uuid,
            genericTypeEnum = GenericTypeEnum.ALLERGY,
            syncType = SyncType.POST
        ).let { allergyGenericEntity ->
            insertAllergyGenericEntity(allergyGenericEntity, allergyResponse, uuid)
        }
    }

    override suspend fun insertRiskFactorRecord(
        riskFactorResponse: RiskFactorResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = riskFactorResponse.uuid,
            genericTypeEnum = GenericTypeEnum.RISK_FACTOR,
            syncType = SyncType.POST
        ).let { riskFactorGenericEntity ->
            insertRiskFactorGenericEntity(riskFactorGenericEntity, riskFactorResponse, uuid)
        }
    }

    override suspend fun insertTobaccoCessationRecord(
        tobaccoCessationResponse: TobaccoCessationResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = tobaccoCessationResponse.uuid,
            genericTypeEnum = GenericTypeEnum.TOBACCO_CESSATION,
            syncType = SyncType.POST
        ).let { tobaccoCessationGenericEntity ->
            insertTobaccoCessationGenericEntity(tobaccoCessationGenericEntity, tobaccoCessationResponse, uuid)
        }
    }

    override suspend fun insertInterventionRecord(
        interventionResponse: InterventionResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = interventionResponse.uuid!!,
            genericTypeEnum = GenericTypeEnum.INTERVENTION,
            syncType = SyncType.POST
        ).let { interventionGenericEntity ->
            insertInterventionGenericEntity(interventionGenericEntity, interventionResponse, uuid)
        }
    }

    override suspend fun insertExaminationRecord(
        examinationResponse: ExaminationResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = examinationResponse.uuid!!,
            genericTypeEnum = GenericTypeEnum.EXAMINATION,
            syncType = SyncType.POST
        ).let { examinationGenericEntity ->
            insertExaminationGenericEntity(examinationGenericEntity, examinationResponse, uuid)
        }
    }

    override suspend fun insertVital(
        vitalResponse: VitalResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = vitalResponse.uuid,
            genericTypeEnum = GenericTypeEnum.VITAL,
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

    override suspend fun insertOrUpdatePhotoPrescriptionPatch(
        prescriptionFhirId: String,
        prescriptionPhotoPatch: PrescriptionPhotoPatch,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            prescriptionFhirId,
            GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
            SyncType.PATCH
        ).let { prescriptionGenericEntity ->
            insertOrUpdatePhotoPrescriptionGenericEntityPatch(
                prescriptionFhirId = prescriptionFhirId,
                prescriptionGenericEntity = prescriptionGenericEntity,
                prescriptionPhotoPatch = prescriptionPhotoPatch,
                uuid = uuid
            )
        }
    }

    override suspend fun insertOrUpdateCVDPatch(
        cvdFhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.CVD,
            SyncType.PATCH
        ).let { genericEntities ->
            insertOrUpdateCVDGenericEntityPatch(genericEntities, cvdFhirId, map, uuid)
        }
    }
    override suspend fun insertOrUpdateSymDiagPatchEntity(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = fhirId,
            genericTypeEnum = GenericTypeEnum.SYMPTOMS_DIAGNOSIS,
            syncType = SyncType.PATCH
        ).let { genericEntity ->
            insertSymDiagGenericEntityPatch(genericEntity, fhirId, map, uuid)
        }
    }

    override suspend fun insertOrUpdateVitalPatchEntity(
        vitalFhirId: String, map: Map<String, Any>, uuid: String
    ): Long {
        return genericDao.getSameTypeGenericEntityPayload(
            genericTypeEnum = GenericTypeEnum.VITAL,
            syncType = SyncType.PATCH
        ).let { genericEntity ->
            insertVitalGenericEntityPatch(genericEntity, vitalFhirId, map, uuid)
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

    override suspend fun insertPhotoLabTestAndMedRecord(
        map: Map<String, Any>,
        patientId: String,
        uuid: String,
        labTestId:String,
        typeEnum: GenericTypeEnum
    ): Long {
        Timber.d("LAB: $labTestId")
        return genericDao.getGenericEntityById(
            patientId = labTestId,
            genericTypeEnum = typeEnum,
            syncType = SyncType.POST
        ).let { genericEntity ->
            insertLabTestPhotoGenericEntity(
                map = map,
                labTestId = labTestId,
                photoGenericEntity = genericEntity,
                uuid = uuid, typeEnum = typeEnum
            )
        }
    }
    
    override suspend fun insertOrUpdatePhotoLabTestAndMedPatch(
        fhirId: String,
        map: Map<String, Any>,
        uuid: String,
        typeEnum: GenericTypeEnum
    ): Long {
        return genericDao.getGenericEntityById(
            fhirId,
            typeEnum,
            SyncType.PATCH
        ).let { prescriptionGenericEntity ->
            insertOrUpdatePhotoLabTestGenericEntityPatch(
                prescriptionFhirId = fhirId,
                prescriptionGenericEntity = prescriptionGenericEntity,
                map = map,
                uuid = uuid,
                typeEnum = typeEnum
            )
        }

    }

    override suspend fun insertImmunization(
        immunizationResponse: ImmunizationResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = immunizationResponse.immunizationUuid,
            genericTypeEnum = GenericTypeEnum.IMMUNIZATION,
            syncType = SyncType.POST
        ).let { immunizationGenericEntity ->
            insertImmunizationGenericEntity(immunizationResponse, immunizationGenericEntity, uuid)
        }
    }

    override suspend fun insertOrUpdatePrescriptionPut(
        prescriptionFhirId: String,
        prescriptionResponse: PrescriptionResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = prescriptionFhirId,
            genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
            syncType = SyncType.PUT
        ).let { prescriptionGenericEntity ->
            insertPrescriptionPutGenericEntity(prescriptionFhirId, prescriptionResponse, prescriptionGenericEntity, uuid)
        }
    }

    override suspend fun insertOrUpdateInterventionPut(
        interventionFhirId: String,
        interventionResponse: InterventionResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = interventionFhirId,
            genericTypeEnum = GenericTypeEnum.INTERVENTION,
            syncType = SyncType.PUT
        ).let { interventionGenericEntity ->
            insertInterventionPutGenericEntity(interventionFhirId, interventionResponse, interventionGenericEntity, uuid)
        }
    }

    override suspend fun insertOrUpdateExaminationPut(
        examinationFhirId: String,
        examinationResponse: ExaminationResponse,
        uuid: String
    ): Long {
        return genericDao.getGenericEntityById(
            patientId = examinationFhirId,
            genericTypeEnum = GenericTypeEnum.EXAMINATION,
            syncType = SyncType.PUT
        ).let { examinationGenericEntity ->
            insertExaminationPutGenericEntity(examinationFhirId, examinationResponse, examinationGenericEntity, uuid)
        }
    }
}