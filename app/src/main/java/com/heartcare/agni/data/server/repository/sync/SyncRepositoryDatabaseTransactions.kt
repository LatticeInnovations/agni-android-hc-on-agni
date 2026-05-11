package com.heartcare.agni.data.server.repository.sync

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.DiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.local.roomdb.dao.FamilyHistoryDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.HealthFacilityDao
import com.heartcare.agni.data.local.roomdb.dao.HistoryMedicationDao
import com.heartcare.agni.data.local.roomdb.dao.InterventionDao
import com.heartcare.agni.data.local.roomdb.dao.LevelsDao
import com.heartcare.agni.data.local.roomdb.dao.MedicationDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.PatientLastUpdatedDao
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.dao.PriorDxDao
import com.heartcare.agni.data.local.roomdb.dao.ReferralDao
import com.heartcare.agni.data.local.roomdb.dao.RiskFactorDao
import com.heartcare.agni.data.local.roomdb.dao.RiskPredictionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.local.roomdb.dao.TobaccoCessationDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignAppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.NationalIdDao
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.IdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisResponse
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
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
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.data.server.model.nationalId.NationalIdResponse
import com.heartcare.agni.data.server.model.report.ReportTokenResponse
import com.heartcare.agni.utils.constants.ErrorConstants
import com.heartcare.agni.utils.constants.ErrorConstants.APPOINTMENT_ERROR
import com.heartcare.agni.utils.constants.ErrorConstants.DUPLICATE_RECORD
import com.heartcare.agni.utils.converters.responseconverter.toAllergyEntity
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.toCVDEntity
import com.heartcare.agni.utils.converters.responseconverter.toCampaignScheduleEntity
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisEntity
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toExaminationEntity
import com.heartcare.agni.utils.converters.responseconverter.toExaminationMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toFamilyHistoryEntity
import com.heartcare.agni.utils.converters.responseconverter.toHealthFacilityEntity
import com.heartcare.agni.utils.converters.responseconverter.toHistoryMedicationEntity
import com.heartcare.agni.utils.converters.responseconverter.toInterventionEntity
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toLevelEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfId
import com.heartcare.agni.utils.converters.responseconverter.toListOfIdentifierEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfMedicationEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfMedicineDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toPatientEntity
import com.heartcare.agni.utils.converters.responseconverter.toPatientLastUpdatedEntity
import com.heartcare.agni.utils.converters.responseconverter.toPrescriptionEntity
import com.heartcare.agni.utils.converters.responseconverter.toPriorDxEntity
import com.heartcare.agni.utils.converters.responseconverter.toReferralEntity
import com.heartcare.agni.utils.converters.responseconverter.toRiskFactorEntity
import com.heartcare.agni.utils.converters.responseconverter.toScheduleEntity
import com.heartcare.agni.utils.converters.responseconverter.toTobaccoCessationEntity
import com.heartcare.agni.utils.converters.responseconverter.toScreeningSiteMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toCampaignAppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.toNationalIdEntity
import com.heartcare.agni.utils.converters.responseconverter.toReportTokenEntity
import com.heartcare.agni.utils.converters.responseconverter.toVitalEntity
import java.util.UUID

open class SyncRepositoryDatabaseTransactions(
    private val patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val medicationDao: MedicationDao,
    private val prescriptionDao: PrescriptionDao,
    private val scheduleDao: ScheduleDao,
    private val appointmentDao: AppointmentDao,
    private val patientLastUpdatedDao: PatientLastUpdatedDao,
    private val cvdDao: CVDDao,
    private val vitalDao: VitalDao,
    private val diagnosisDao: DiagnosisDao,
    private val levelsDao: LevelsDao,
    private val riskPredictionDao: RiskPredictionDao,
    private val priorDxDao: PriorDxDao,
    private val historyMedicationDao: HistoryMedicationDao,
    private val familyHistoryDao: FamilyHistoryDao,
    private val allergyDao: AllergyDao,
    private val riskFactorDao: RiskFactorDao,
    private val tobaccoCessationDao: TobaccoCessationDao,
    private val interventionDao: InterventionDao,
    private val examinationDao: ExaminationDao,
    private val healthFacilityDao: HealthFacilityDao,
    private val referralDao: ReferralDao,
    private val screeningSiteDao: ScreeningSiteDao,
    private val campaignScheduleDao: CampaignScheduleDao,
    private val campaignAppointmentDao: CampaignAppointmentDao,
    private val nationalIdDao: NationalIdDao
) {


    protected suspend fun insertPatient(body: List<PatientResponse>) {
        //Insert Patient Data
        patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()
        val identifierList = mutableListOf<IdentifierEntity>()

        body.map { patientResponse ->
            listOfGenericEntity.add(
                GenericEntity(
                    id = UUID.randomUUID().toString(),
                    patientId = patientResponse.id,
                    payload = patientResponse.fhirId!!,
                    type = GenericTypeEnum.FHIR_IDS_PRESCRIPTION,
                    syncType = SyncType.POST
                )
            )
            patientResponse.toListOfIdentifierEntity().let { listOfIdentifiers ->
                identifierList.addAll(listOfIdentifiers)
            }
        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

        //Insert Identifier Data
        patientDao.insertIdentifiers(*identifierList.toTypedArray())
    }

    protected suspend fun insertFormPrescriptions(body: List<PrescriptionResponse>) {
        prescriptionDao.insertPrescription(*body.map { prescriptionResponse ->
            prescriptionResponse.toPrescriptionEntity(
                patientDao
            )
        }.toTypedArray())
        val medicineDirections = mutableListOf<PrescriptionDirectionsEntity>()
        body.forEach { prescriptionResponse ->
            medicineDirections.addAll(
                prescriptionResponse.toListOfPrescriptionDirectionsEntity(
                    medicationDao
                )
            )
        }
        prescriptionDao.insertPrescriptionMedicines(
            *medicineDirections.toTypedArray()
        )
    }

    protected suspend fun insertMedication(body: List<MedicationResponse>) {
        medicationDao.insertMedication(
            *body.toListOfMedicationEntity().toTypedArray()
        )
    }

    protected suspend fun insertInterventionMasterList(body: List<InterventionMasterResponse>) {
        interventionDao.insertInterventionMaster(
            *body.map { it.toInterventionMasterEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertExaminationMasterList(body: List<ExaminationMasterResponse>) {
        examinationDao.insertExaminationMaster(
            *body.map { it.toExaminationMasterEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertDiagnosisMasterList(body: List<DiagnosisMasterResponse>) {
        diagnosisDao.insertDiagnosisMasterEntity(
            *body.map { it.toDiagnosisMasterEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertScreeningSiteMasterList(body: List<ScreeningSiteMasterResponse>) {
        screeningSiteDao.insertScreeningSiteMaster(
            *body.map { it.toScreeningSiteMasterEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertMedicationTiming(body: List<MedicineTimeResponse>) {
        medicationDao.insertMedicineDosageInstructions(
            *body.toListOfMedicineDirectionsEntity().toTypedArray()
        )
    }

    protected suspend fun insertSchedule(
        body: List<ScheduleResponse>,
        recordType: RecordType? = RecordType.FACILITY
    ) {
        if (recordType == RecordType.SCREENING_SITE) {
            campaignScheduleDao.insertScheduleEntity(*body.map { scheduleResponse ->
                scheduleResponse.toCampaignScheduleEntity()
            }.toTypedArray())
        } else {
            scheduleDao.insertScheduleEntity(*body.map { scheduleResponse ->
                scheduleResponse.toScheduleEntity()
            }.toTypedArray())
        }
    }

    protected suspend fun insertAppointment(
        body: List<AppointmentResponse>,
        recordType: RecordType? = RecordType.FACILITY
    ) {
        if (recordType == RecordType.SCREENING_SITE) {
            campaignAppointmentDao.insertAppointmentEntity(*body.map { appointmentResponse ->
                appointmentResponse.toCampaignAppointmentEntity(patientDao, campaignScheduleDao)
            }.toTypedArray())
        } else {
            appointmentDao.insertAppointmentEntity(*body.map { appointmentResponse ->
                appointmentResponse.toAppointmentEntity(patientDao, scheduleDao)
            }.toTypedArray())
        }
    }

    protected suspend fun insertCVD(body: List<CVDResponse>) {
        cvdDao.insertCVDRecord(*body.map { cvdResponse ->
            cvdResponse.toCVDEntity(patientDao, appointmentDao, campaignAppointmentDao, riskPredictionDao)
        }.toTypedArray())
    }

    protected suspend fun insertVital(body: List<VitalResponse>) {
        //Insert Vital Data
        vitalDao.insertVital(*body.map { it.toVitalEntity(patientDao, appointmentDao, campaignAppointmentDao) }
            .toTypedArray())
    }

    protected suspend fun insertSymDiag(body: List<DiagnosisResponse>) {
        //Insert Vital Data
        diagnosisDao.insertDiagnosis(*body.map {
            it.toDiagnosisEntity(
                patientDao,
                appointmentDao,
                campaignAppointmentDao
            )
        }.toTypedArray())
    }

    protected suspend fun insertPatientFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            patientDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertPrescriptionAndMedicationRequestFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                prescriptionDao.updatePrescriptionFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.prescription!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateMedReqFhirId(
                        prescriptionResponse.medReqUuid,
                        prescriptionResponse.medReqFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertMedicationRequestFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                createResponse.prescription!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateMedReqFhirId(
                        prescriptionResponse.medReqUuid,
                        prescriptionResponse.medReqFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertScheduleFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>,
        genericTypeEnum: GenericTypeEnum
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null, ErrorConstants.SCHEDULE_EXISTS -> {
                    if (genericTypeEnum== GenericTypeEnum.CAMPAIGN_SCHEDULE) {
                        campaignScheduleDao.updateScheduleFhirId(
                            createResponse.id!!, createResponse.fhirId!!
                        )
                    }else{
                        scheduleDao.updateScheduleFhirId(
                            createResponse.id!!, createResponse.fhirId!!
                        )
                    }
                }

                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertAppointmentFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>,
        genericTypeEnum: GenericTypeEnum

    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                if (genericTypeEnum== GenericTypeEnum.CAMPAIGN_APPOINTMENT) {
                    campaignAppointmentDao.updateAppointmentFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }else{
                    appointmentDao.updateAppointmentFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }

            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertPatientLastUpdated(body: List<PatientLastUpdatedResponse>) {
        //Insert Patient Last Updated Data
        patientLastUpdatedDao.insertPatientLastUpdatedData(
            patientLastUpdatedEntity = body
                .filter { patientDao.getPatientDataById(it.uuid).isNotEmpty() }
                .map { it.toPatientLastUpdatedEntity() }
                .toTypedArray()
        )
    }

    protected suspend fun insertCVDFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    cvdDao.updateCVDFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                APPOINTMENT_ERROR, DUPLICATE_RECORD -> {
                    cvdDao.deleteCVD(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertVitalFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            vitalDao.updateVitalFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertSymDiagFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            diagnosisDao.updateDiagnosisFhirId(
                createResponse.id!!, createResponse.fhirId!!
            )
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    private suspend fun deleteGenericEntityByListOfIds(idsToDelete: List<String>): Int {
        return genericDao.deleteSyncPayload(idsToDelete)
    }

    protected suspend fun deleteGenericEntityData(listOfGenericEntities: List<GenericEntity>): Int {
        return genericDao.deleteSyncPayload(listOfGenericEntities.toListOfId())
    }

    protected suspend fun insertPriorDxFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    priorDxDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    priorDxDao.deletePriorDx(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertHistoryMedicationFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    historyMedicationDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    historyMedicationDao.deleteHistoryMedication(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertFamilyHistoryFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    familyHistoryDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    familyHistoryDao.deleteFamilyHistory(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertAllergyFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    allergyDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    allergyDao.deleteAllergy(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertRiskFactorsFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    riskFactorDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    riskFactorDao.deleteRiskFactor(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertTobaccoCessationFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    tobaccoCessationDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    tobaccoCessationDao.deleteTobaccoCessation(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertInterventionFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    interventionDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    interventionDao.deleteIntervention(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertExaminationFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    examinationDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    examinationDao.deleteExamination(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertReferralFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    referralDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    referralDao.deleteReferral(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertLevels(body: List<LevelResponse>) {
        levelsDao.insertLevelEntity(
            *body.map { it.toLevelEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertHealthFacility(body: List<HealthFacilityResponse>) {
        healthFacilityDao.insertHealthFacility(
            *body.map { it.toHealthFacilityEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertPriorDx(body: List<PriorDxResponse>) {
        priorDxDao.insertPriorDxRecord(
            *body.map { it.toPriorDxEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertHistoryMedication(body: List<HistoryMedicationResponse>) {
        historyMedicationDao.insertHistoryMedicationRecord(
            *body.map { it.toHistoryMedicationEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertFamilyHistory(body: List<FamilyHistoryResponse>) {
        familyHistoryDao.insertFamilyHistoryRecord(
            *body.map { it.toFamilyHistoryEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertAllergy(body: List<AllergyResponse>) {
        allergyDao.insertAllergyRecord(
            *body.map { it.toAllergyEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertRiskFactors(body: List<RiskFactorResponse>) {
        riskFactorDao.insertRiskFactorRecord(
            *body.map { it.toRiskFactorEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertTobaccoCessation(body: List<TobaccoCessationResponse>) {
        tobaccoCessationDao.insertTobaccoCessationRecord(
            *body.map { it.toTobaccoCessationEntity(patientDao, appointmentDao,campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertIntervention(body: List<InterventionResponse>) {
        interventionDao.insertIntervention(
            *body.map { it.toInterventionEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertExamination(body: List<ExaminationResponse>) {
        examinationDao.insertExamination(
            *body.map { it.toExaminationEntity(patientDao, appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertReferral(body: List<ReferralResponse>) {
        referralDao.insertReferralRecord(
            *body.map { it.toReferralEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertReportToken(body: List<ReportTokenResponse>) {
        appointmentDao.insertReportToken(
            *body.map { it.toReportTokenEntity(appointmentDao, campaignAppointmentDao) }.toTypedArray()
        )
    }

    protected fun insertNationalId(body: List<NationalIdResponse>) {
        nationalIdDao.insertNationalIdRecord(
            *body.map { it.toNationalIdEntity() }.toTypedArray()
        )
    }

    protected fun deleteAllNationalId() {
        nationalIdDao.deleteAllNationalIdRecord()
    }
}