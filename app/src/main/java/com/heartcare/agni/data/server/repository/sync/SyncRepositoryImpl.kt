package com.heartcare.agni.data.server.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.DiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.local.roomdb.dao.FamilyHistoryDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.HistoryMedicationDao
import com.heartcare.agni.data.local.roomdb.dao.InterventionDao
import com.heartcare.agni.data.local.roomdb.dao.LevelsDao
import com.heartcare.agni.data.local.roomdb.dao.MedicationDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.PatientLastUpdatedDao
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.dao.PriorDxDao
import com.heartcare.agni.data.local.roomdb.dao.RiskFactorDao
import com.heartcare.agni.data.local.roomdb.dao.RiskPredictionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.TobaccoCessationDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.DiagnosisApiService
import com.heartcare.agni.data.server.api.ExaminationApiService
import com.heartcare.agni.data.server.api.HistoryAndTestsApiService
import com.heartcare.agni.data.server.api.InterventionApiService
import com.heartcare.agni.data.server.api.LevelsApiService
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.api.PrescriptionApiService
import com.heartcare.agni.data.server.api.ScheduleAndAppointmentApiService
import com.heartcare.agni.data.server.api.VitalApiService
import com.heartcare.agni.data.server.constants.ConstantValues.COUNT_VALUE
import com.heartcare.agni.data.server.constants.ConstantValues.DEFAULT_MAX_COUNT_VALUE
import com.heartcare.agni.data.server.constants.EndPoints
import com.heartcare.agni.data.server.constants.EndPoints.MEDICATION_REQUEST
import com.heartcare.agni.data.server.constants.EndPoints.PATIENT
import com.heartcare.agni.data.server.constants.EndPoints.VITAL
import com.heartcare.agni.data.server.constants.QueryParameters.COUNT
import com.heartcare.agni.data.server.constants.QueryParameters.GREATER_THAN_BUILDER
import com.heartcare.agni.data.server.constants.QueryParameters.ID
import com.heartcare.agni.data.server.constants.QueryParameters.LAST_UPDATED
import com.heartcare.agni.data.server.constants.QueryParameters.OFFSET
import com.heartcare.agni.data.server.constants.QueryParameters.PATIENT_ID
import com.heartcare.agni.data.server.constants.QueryParameters.SORT
import com.heartcare.agni.data.server.constants.QueryParameters.TYPE
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisResponse
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
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.constants.ErrorConstants.SOMETHING_WENT_WRONG
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.fromJson
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.mapToObject
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeStampDate
import com.heartcare.agni.utils.converters.responseconverter.toListOfId
import com.heartcare.agni.utils.converters.responseconverter.toNoBracketAndNoSpaceString
import com.heartcare.agni.utils.converters.server.responsemapper.ApiContinueResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEmptyResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiErrorResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val patientApiService: PatientApiService,
    private val prescriptionApiService: PrescriptionApiService,
    private val scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService,
    private val cvdApiService: CVDApiService,
    private val vitalApiService: VitalApiService,
    private val diagnosisApiService: DiagnosisApiService,
    private val levelsApiService: LevelsApiService,
    private val historyAndTestsApiService: HistoryAndTestsApiService,
    private val interventionApiService: InterventionApiService,
    private val examinationApiService: ExaminationApiService,
    patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val preferenceRepository: PreferenceRepository,
    medicationDao: MedicationDao,
    prescriptionDao: PrescriptionDao,
    scheduleDao: ScheduleDao,
    appointmentDao: AppointmentDao,
    patientLastUpdatedDao: PatientLastUpdatedDao,
    cvdDao: CVDDao,
    vitalDao: VitalDao,
    diagnosisDao: DiagnosisDao,
    levelsDao: LevelsDao,
    riskPredictionDao: RiskPredictionDao,
    priorDxDao: PriorDxDao,
    historyMedicationDao: HistoryMedicationDao,
    familyHistoryDao: FamilyHistoryDao,
    allergyDao: AllergyDao,
    riskFactorDao: RiskFactorDao,
    tobaccoCessationDao: TobaccoCessationDao,
    interventionDao: InterventionDao,
    examinationDao: ExaminationDao
) : SyncRepository, SyncRepositoryDatabaseTransactions(
    patientDao,
    genericDao,
    medicationDao,
    prescriptionDao,
    scheduleDao,
    appointmentDao,
    patientLastUpdatedDao,
    cvdDao,
    vitalDao,
    diagnosisDao,
    levelsDao,
    riskPredictionDao,
    priorDxDao,
    historyMedicationDao,
    familyHistoryDao,
    allergyDao,
    riskFactorDao,
    tobaccoCessationDao,
    interventionDao,
    examinationDao
) {

    override suspend fun getAndInsertListPatientData(
        offset: Int
    ): ResponseMapper<List<PatientResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncPatient() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncPatient().toTimeStampDate()
        )

        try {
            ApiResponseConverter.convert(
                patientApiService.getListData(
                    PATIENT, map
                ), true
            ).run {
                return when (this) {
                    is ApiContinueResponse -> {
                        //Insert Patient
                        insertPatient(body)
                        //Call for next batch data
                        getAndInsertListPatientData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        //Set Last Update Time
                        preferenceRepository.setLastSyncPatient(Date().time)
                        //Insert Patient
                        insertPatient(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            return ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertPatientDataById(
        id: String
    ): ResponseMapper<List<PatientResponse>> {
        try {
            ApiResponseConverter.convert(
                patientApiService.getListData(
                    PATIENT, mapOf(Pair(ID, id))
                )
            ).run {
                return when (this) {
                    is ApiEndResponse -> {
                        //Insert Patient Data
                        insertPatient(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            return ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertFormPrescription(patientId: String?): ResponseMapper<List<PrescriptionResponse>> {
        return try {
            if (patientId == null) {
                genericDao.getSameTypeGenericEntityPayload(
                    GenericTypeEnum.FHIR_IDS_PRESCRIPTION, SyncType.POST, COUNT_VALUE
                ).let { listOfGenericEntity ->
                    if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                    else {
                        val map = mutableMapOf<String, String>()
                        map[PATIENT_ID] =
                            listOfGenericEntity.map { it.payload }.toNoBracketAndNoSpaceString()
                        map[COUNT] = DEFAULT_MAX_COUNT_VALUE.toString()
                        ApiResponseConverter.convert(prescriptionApiService.getPastPrescription(map))
                            .run {
                                when (this) {
                                    is ApiEndResponse -> {
                                        insertFormPrescriptions(body)
                                        genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                                        getAndInsertFormPrescription(null)
                                    }

                                    else -> {
                                        this
                                    }
                                }

                            }
                    }
                }
            } else {
                ApiResponseConverter.convert(
                    prescriptionApiService.getPastPrescription(
                        mapOf(
                            Pair(PATIENT_ID, patientId)
                        )
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertFormPrescriptions(body)
                            this
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertMedication(offset: Int): ResponseMapper<List<MedicationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastMedicationSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicationSyncDate().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                prescriptionApiService.getAllMedications(map), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertMedication(body)
                        getAndInsertMedication(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastMedicationSyncDate(Date().time)
                        insertMedication(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertInterventionMaster(offset: Int): ResponseMapper<List<InterventionMasterResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastInterventionMasterSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastInterventionMasterSyncDate().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                interventionApiService.getInterventionMasterList(map), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertInterventionMasterList(body)
                        getAndInsertInterventionMaster(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastInterventionMasterSyncDate(Date().time)
                        insertInterventionMasterList(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertExaminationMaster(offset: Int): ResponseMapper<List<ExaminationMasterResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastExaminationMasterSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastExaminationMasterSyncDate().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                examinationApiService.getExaminationMasterList(map), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertExaminationMasterList(body)
                        getAndInsertExaminationMaster(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastExaminationMasterSyncDate(Date().time)
                        insertExaminationMasterList(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertDiagnosisMaster(): ResponseMapper<List<DiagnosisMasterResponse>> {
        val map = mutableMapOf<String, String>()
        if (preferenceRepository.getLastDiagnosisMasterSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastDiagnosisMasterSyncDate().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                diagnosisApiService.getDiagnosisMaster(map)
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        preferenceRepository.setLastDiagnosisMasterSyncDate(Date().time)
                        insertDiagnosisMasterList(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getMedicineTime(): ResponseMapper<List<MedicineTimeResponse>> {
        val map = mutableMapOf<String, String>()
        if (preferenceRepository.getLastMedicineDosageInstructionSyncDate() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastMedicineDosageInstructionSyncDate().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                prescriptionApiService.getMedicineTime(
                    map
                )
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        preferenceRepository.setLastMedicineDosageInstructionSyncDate(Date().time)
                        insertMedicationTiming(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertSchedule(offset: Int): ResponseMapper<List<ScheduleResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncSchedule() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncSchedule().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.getScheduleList(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Schedule Data
                        insertSchedule(body)
                        //Call for next batch data
                        getAndInsertSchedule(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncSchedule(Date().time)
                        insertSchedule(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertAppointment(offset: Int): ResponseMapper<List<AppointmentResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncAppointment() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncAppointment().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.getAppointmentList(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Appointment Data
                        insertAppointment(body)
                        //Call for next batch data
                        getAndInsertAppointment(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncAppointment(Date().time)
                        insertAppointment(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertPatientLastUpdatedData(): ResponseMapper<List<PatientLastUpdatedResponse>> {
        return try {
            ApiResponseConverter.convert(
                patientApiService.getPatientLastUpdatedData()
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        //Insert Patient Last Updated Data
                        insertPatientLastUpdated(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCVD(offset: Int): ResponseMapper<List<CVDResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCVD() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncCVD().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                cvdApiService.getCVD(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert CVD Data
                        insertCVD(body)
                        //Call for next batch data
                        getAndInsertCVD(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCVD(Date().time)
                        insertCVD(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertListVitalData(offset: Int): ResponseMapper<List<VitalResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncVital() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncVital().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                vitalApiService.getListData(
                    VITAL, map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Patient
                        insertVital(body)
                        //Call for next batch data
                        getAndInsertListVitalData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        //Set Last Update Time
                        preferenceRepository.setLastSyncVital(Date().time)
                        //Insert Patient
                        insertVital(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertListDiagnosisData(offset: Int): ResponseMapper<List<DiagnosisResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncDiagnosis() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncDiagnosis().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                diagnosisApiService.getListData(
                    EndPoints.DIAGNOSIS, map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Patient
                        insertSymDiag(body)
                        //Call for next batch data
                        getAndInsertListDiagnosisData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        //Set Last Update Time
                        preferenceRepository.setLastSyncDiagnosis(Date().time)
                        //Insert Patient
                        insertSymDiag(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            return ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendPersonPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    patientApiService.createData(
                        PATIENT,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(PatientResponse::class.java)!!
                        })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertPatientFhirId(
                                listOfGenericEntity,
                                body
                            ).let { deletedRows -> if (deletedRows > 0) sendPersonPostData() else this }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendFormPrescriptionPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PRESCRIPTION,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        prescriptionApiService.postPrescriptionRelatedData(
                            MEDICATION_REQUEST,
                            listOfGenericEntity.map { prescriptionGenericEntity ->
                                prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(PrescriptionResponse::class.java)!!
                            }
                        )
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                insertPrescriptionAndMedicationRequestFhirId(
                                    listOfGenericEntity,
                                    body
                                ).let { deletedRows ->
                                    if (deletedRows > 0) sendFormPrescriptionPostData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendSchedulePostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.SCHEDULE, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    scheduleAndAppointmentApiService.postScheduleData(listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(ScheduleResponse::class.java)!!
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertScheduleFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendSchedulePostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendAppointmentPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    scheduleAndAppointmentApiService.createAppointment(listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(AppointmentResponse::class.java)!!
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertAppointmentFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendAppointmentPostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }


    override suspend fun sendPatientLastUpdatePostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.LAST_UPDATED,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    patientApiService.postPatientLastUpdates(
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(PatientLastUpdatedResponse::class.java)!!
                        })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            val deletedRows = deleteGenericEntityData(listOfGenericEntity)
                            if (deletedRows > 0) sendPatientLastUpdatePostData() else this
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendCVDPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.CVD, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    cvdApiService.createCVD(listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(CVDResponse::class.java)!!
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertCVDFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendCVDPostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendVitalPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.VITAL, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    vitalApiService.createData(VITAL, listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(VitalResponse::class.java)!!
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertVitalFhirId(
                                listOfGenericEntity, body
                            ).let { deletedRows -> if (deletedRows > 0) sendVitalPostData() else this }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendDiagnosisPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.DIAGNOSIS, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    diagnosisApiService.createData(
                        EndPoints.DIAGNOSIS,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(DiagnosisData::class.java)!!
                        })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertSymDiagFhirId(
                                listOfGenericEntity, body
                            ).let { deletedRows -> if (deletedRows > 0) sendDiagnosisPostData() else this }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendPriorDxPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PRIOR_DX,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postPriorDx(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(PriorDxResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertPriorDxFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendPriorDxPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendHistoryMedicationPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.HISTORY_MEDICATION,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postHistoryMedication(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(HistoryMedicationResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertHistoryMedicationFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendHistoryMedicationPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendFamilyHistoryPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.FAMILY_HISTORY,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postFamilyHistory(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(FamilyHistoryResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertFamilyHistoryFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendFamilyHistoryPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendAllergyPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.ALLERGY,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postAllergy(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(AllergyResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertAllergyFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendAllergyPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendRiskFactorPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.RISK_FACTOR,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postRiskFactor(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(RiskFactorResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertRiskFactorsFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendRiskFactorPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendTobaccoCessationPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.TOBACCO_CESSATION,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postTobaccoCessation(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(TobaccoCessationResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertTobaccoCessationFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendTobaccoCessationPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendInterventionPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.INTERVENTION,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        interventionApiService.postIntervention(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(InterventionResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertInterventionFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendInterventionPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendExaminationPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.EXAMINATION,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        examinationApiService.postExamination(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(ExaminationResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertExaminationFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendExaminationPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendPersonPatchData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PATIENT, syncType = SyncType.PATCH
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        patientApiService.patchPatient(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(PatientResponse::class.java)!!
                            })
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                deleteGenericEntityData(listOfGenericEntity).let {
                                    if (it > 0) sendPersonPatchData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendAppointmentPatchData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT, syncType = SyncType.PATCH
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        scheduleAndAppointmentApiService.patchListOfChanges(
                            listOfGenericEntity.map { it.payload.fromJson() })
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                deleteGenericEntityData(listOfGenericEntity).let {
                                    if (it > 0) sendAppointmentPatchData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendPrescriptionPutData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.PRESCRIPTION, syncType = SyncType.PUT
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        prescriptionApiService.sendPrescriptionPut(
                            listOfGenericEntity.map { prescriptionGenericEntity ->
                                prescriptionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(PrescriptionResponse::class.java)!!
                            }
                        )
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                insertMedicationRequestFhirId(
                                    listOfGenericEntity,
                                    body
                                ).let {
                                    if (it > 0) sendPrescriptionPutData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sentInterventionPutData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.INTERVENTION, syncType = SyncType.PUT
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        interventionApiService.putIntervention(
                            listOfGenericEntity.map { interventionGenericEntity ->
                                interventionGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(InterventionResponse::class.java)!!
                            }
                        )
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                deleteGenericEntityData(listOfGenericEntity).let {
                                    if (it > 0) sentInterventionPutData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sentExaminationPutData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.EXAMINATION, syncType = SyncType.PUT
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        examinationApiService.putExamination(
                            listOfGenericEntity.map { examinationGenericEntity ->
                                examinationGenericEntity.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(ExaminationResponse::class.java)!!
                            }
                        )
                    ).run {
                        when (this) {
                            is ApiEndResponse -> {
                                deleteGenericEntityData(listOfGenericEntity).let {
                                    if (it > 0) sentExaminationPutData() else this
                                }
                            }

                            else -> this
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertLevelsData(
        offset: Int
    ): ResponseMapper<List<LevelResponse>> {
        val map = mutableMapOf<String, String>()
        map[TYPE] = "village,province,area-council,health-facility,island"
        map[COUNT] = "$COUNT_VALUE"
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastSyncLevelRecord() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncLevelRecord().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                levelsApiService.getLevelsData(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertLevels(body)
                        //Call for next batch data
                        getAndInsertLevelsData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncLevelRecord(Date().time)
                        insertLevels(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertPriorDxData(
        offset: Int
    ): ResponseMapper<List<PriorDxResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncPriorDx() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncPriorDx().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getPriorDx(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertPriorDx(body)
                        //Call for next batch data
                        getAndInsertPriorDxData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncPriorDx(Date().time)
                        insertPriorDx(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertHistoryMedicationData(
        offset: Int
    ): ResponseMapper<List<HistoryMedicationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncHistoryMedication() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncHistoryMedication().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getHistoryMedication(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertHistoryMedication(body)
                        //Call for next batch data
                        getAndInsertHistoryMedicationData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncHistoryMedication(Date().time)
                        insertHistoryMedication(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertFamilyHistoryData(offset: Int): ResponseMapper<List<FamilyHistoryResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncFamilyHistory() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncFamilyHistory().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getFamilyHistory(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertFamilyHistory(body)
                        //Call for next batch data
                        getAndInsertFamilyHistoryData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncFamilyHistory(Date().time)
                        insertFamilyHistory(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertAllergyData(offset: Int): ResponseMapper<List<AllergyResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncAllergy() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncAllergy().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getAllergy(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertAllergy(body)
                        //Call for next batch data
                        getAndInsertAllergyData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncAllergy(Date().time)
                        insertAllergy(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertRiskFactorData(offset: Int): ResponseMapper<List<RiskFactorResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncRiskFactors() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncRiskFactors().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getRiskFactors(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertRiskFactors(body)
                        //Call for next batch data
                        getAndInsertRiskFactorData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncRiskFactors(Date().time)
                        insertRiskFactors(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertTobaccoCessationData(offset: Int): ResponseMapper<List<TobaccoCessationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncTobaccoCessation() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncTobaccoCessation().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getTobaccoCessation(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertTobaccoCessation(body)
                        //Call for next batch data
                        getAndInsertTobaccoCessationData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncTobaccoCessation(Date().time)
                        insertTobaccoCessation(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertInterventionsData(
        offset: Int
    ): ResponseMapper<List<InterventionResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncIntervention() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncIntervention().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                interventionApiService.getInterventions(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertIntervention(body)
                        //Call for next batch data
                        getAndInsertInterventionsData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncIntervention(Date().time)
                        insertIntervention(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertExaminationData(
        offset: Int
    ): ResponseMapper<List<ExaminationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncExamination() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncExamination().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                examinationApiService.getExaminations(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertExamination(body)
                        //Call for next batch data
                        getAndInsertExaminationData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncExamination(Date().time)
                        insertExamination(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e.localizedMessage)
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
}