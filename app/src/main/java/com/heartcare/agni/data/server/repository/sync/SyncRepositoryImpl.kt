package com.heartcare.agni.data.server.repository.sync

import com.google.gson.internal.LinkedTreeMap
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLogger
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignAppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignScheduleDao
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
import com.heartcare.agni.data.server.api.CampaignApiService
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.DiagnosisApiService
import com.heartcare.agni.data.server.api.ExaminationApiService
import com.heartcare.agni.data.server.api.HistoryAndTestsApiService
import com.heartcare.agni.data.server.api.InterventionApiService
import com.heartcare.agni.data.server.api.LevelsApiService
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.api.PrescriptionApiService
import com.heartcare.agni.data.server.api.ReferralApiService
import com.heartcare.agni.data.server.api.ScheduleAndAppointmentApiService
import com.heartcare.agni.data.server.api.VitalApiService
import com.heartcare.agni.data.server.constants.ConstantValues.COUNT_VALUE
import com.heartcare.agni.data.server.constants.ConstantValues.DEFAULT_MAX_COUNT_VALUE
import com.heartcare.agni.data.server.constants.EndPoints
import com.heartcare.agni.data.server.constants.EndPoints.CAMPAIGN_VITAL
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
import com.heartcare.agni.utils.constants.ErrorConstants.ERROR_FETCHING_USER_DETAILS
import com.heartcare.agni.utils.constants.ErrorConstants.SOMETHING_WENT_WRONG
import com.heartcare.agni.utils.constants.FirebaseKeyConstants.USER_ID
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
    private val referralApiService: ReferralApiService,
    private val campaignApiService: CampaignApiService,
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
    examinationDao: ExaminationDao,
    healthFacilityDao: HealthFacilityDao,
    referralDao: ReferralDao,
    screeningSiteDao: ScreeningSiteDao,
    campaignScheduleDao: CampaignScheduleDao,
    campaignAppointmentDao: CampaignAppointmentDao,
    private val crashlyticsLogger: CrashlyticsLogger
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
    examinationDao,
    healthFacilityDao,
    referralDao,
    screeningSiteDao,
    campaignScheduleDao,
    campaignAppointmentDao
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
                        //Insert Patient
                        insertPatient(body)
                        //Set Last Update Time
                        preferenceRepository.setLastSyncPatient(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertListPatientData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertPatientDataById function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertFormPrescription function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertMedication(body)
                        preferenceRepository.setLastMedicationSyncDate(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertMedication function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertInterventionMasterList(body)
                        preferenceRepository.setLastInterventionMasterSyncDate(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertInterventionMaster function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertExaminationMasterList(body)
                        preferenceRepository.setLastExaminationMasterSyncDate(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertExaminationMaster function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertDiagnosisMasterList(body)
                        preferenceRepository.setLastDiagnosisMasterSyncDate(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertDiagnosisMaster function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertScreeningSiteMaster(): ResponseMapper<List<ScreeningSiteMasterResponse>> {
        val map = mutableMapOf<String, String>()
        if (preferenceRepository.getLastSyncScreeningSiteMaster() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncScreeningSiteMaster().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                campaignApiService.getScreeningSites(map)
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        insertScreeningSiteMasterList(body)
                        preferenceRepository.setLastSyncScreeningSiteMaster(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e, "getAndInsertScreeningSiteMaster function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertMedicationTiming(body)
                        preferenceRepository.setLastMedicineDosageInstructionSyncDate(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getMedicineTime function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.SCHEDULE,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Schedule Data
                        insertSchedule(body, RecordType.FACILITY)
                        //Call for next batch data
                        getAndInsertSchedule(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertSchedule(body, RecordType.FACILITY)
                        preferenceRepository.setLastSyncSchedule(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertSchedule function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCampaignSchedule(offset: Int): ResponseMapper<List<ScheduleResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignSchedule() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignSchedule().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.getScheduleList(
                    EndPoints.CAMPAIGN_SCHEDULE,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertSchedule(body, RecordType.SCREENING_SITE)
                        getAndInsertCampaignSchedule(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertSchedule(body, RecordType.SCREENING_SITE)
                        preferenceRepository.setLastSyncCampaignSchedule(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignSchedule function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.APPOINTMENT,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Appointment Data
                        insertAppointment(body, RecordType.FACILITY)
                        //Call for next batch data
                        getAndInsertAppointment(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertAppointment(body, RecordType.FACILITY)
                        preferenceRepository.setLastSyncAppointment(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertAppointment function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCampaignAppointment(offset: Int): ResponseMapper<List<AppointmentResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignAppointment() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignAppointment().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                scheduleAndAppointmentApiService.getAppointmentList(
                    EndPoints.CAMPAIGN_APPOINTMENT,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertAppointment(body, RecordType.SCREENING_SITE)
                        getAndInsertCampaignAppointment(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertAppointment(body, RecordType.SCREENING_SITE)
                        preferenceRepository.setLastSyncCampaignAppointment(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignAppointment function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertPatientLastUpdatedData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.CVD, map
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
                        insertCVD(body)
                        preferenceRepository.setLastSyncCVD(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCVD function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
    override suspend fun getAndInsertCampaignCVD(offset: Int): ResponseMapper<List<CVDResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignCVD() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncCampaignCVD().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                cvdApiService.getCVD(EndPoints.CAMPAIGN_CVD, map), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertCVD(body)
                        getAndInsertCampaignCVD(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertCVD(body)
                        preferenceRepository.setLastSyncCampaignCVD(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
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
                        insertVital(body)
                        //Set Last Update Time
                        preferenceRepository.setLastSyncVital(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertListVitalData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
    override suspend fun getAndInsertCampaignVitalData(offset: Int): ResponseMapper<List<VitalResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignVital() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignVital().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                vitalApiService.getListData(
                    CAMPAIGN_VITAL, map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        //Insert Patient
                        insertVital(body)
                        //Call for next batch data
                        getAndInsertCampaignVitalData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertVital(body)
                        //Set Last Update Time
                        preferenceRepository.setLastSyncCampaignVital(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertListVitalData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                        insertSymDiag(body)
                        //Set Last Update Time
                        preferenceRepository.setLastSyncDiagnosis(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertListDiagnosisData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            return ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
    override suspend fun getAndInsertCampaignDiagnosisData(offset: Int): ResponseMapper<List<DiagnosisResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignDiagnosis() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignDiagnosis().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                diagnosisApiService.getListData(
                    EndPoints.CAMPAIGN_DIAGNOSIS, map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertSymDiag(body)
                        //Call for next batch data
                        getAndInsertCampaignDiagnosisData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignDiagnosis(Date().time)
                        insertSymDiag(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignDiagnosisData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendPersonPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendFormPrescriptionPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    scheduleAndAppointmentApiService.postScheduleData(
                        EndPoints.SCHEDULE,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(ScheduleResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertScheduleFhirId(
                                listOfGenericEntity, body,
                                GenericTypeEnum.SCHEDULE
                            ).let { deletedRows ->
                                if (deletedRows > 0) sendSchedulePostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendSchedulePostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendCampaignSchedulePostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.CAMPAIGN_SCHEDULE, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    scheduleAndAppointmentApiService.postScheduleData(
                        EndPoints.CAMPAIGN_SCHEDULE,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(ScheduleResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertScheduleFhirId(
                                listOfGenericEntity, body,
                                GenericTypeEnum.CAMPAIGN_SCHEDULE
                            ).let { deletedRows ->
                                if (deletedRows > 0) sendCampaignSchedulePostData() else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendCampaignSchedulePostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendAppointmentPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum =genericTypeEnum, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    scheduleAndAppointmentApiService.createAppointment(
                        endPoint,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(AppointmentResponse::class.java)!!
                        }
                    )
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertAppointmentFhirId(
                                listOfGenericEntity, body,
                                genericTypeEnum
                            ).let { deletedRows ->
                                if (deletedRows > 0) sendAppointmentPostData(genericTypeEnum, endPoint) else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendAppointmentPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendPatientLastUpdatePostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendCVDPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    cvdApiService.createCVD(endPoint, listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(CVDResponse::class.java)!!.copy(campaignId = null)
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertCVDFhirId(listOfGenericEntity, body).let { deletedRows ->
                                if (deletedRows > 0) sendCVDPostData(genericTypeEnum,endPoint) else this
                            }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendCVDPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendVitalPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    vitalApiService.createData(endPoint, listOfGenericEntity.map {
                        it.payload.fromJson<LinkedTreeMap<*, *>>()
                            .mapToObject(VitalResponse::class.java)!!.copy(campaignId = null)
                    })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertVitalFhirId(
                                listOfGenericEntity, body
                            ).let { deletedRows -> if (deletedRows > 0) sendVitalPostData(genericTypeEnum,endPoint) else this }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendVitalPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendDiagnosisPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum, syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else ApiResponseConverter.convert(
                    diagnosisApiService.createData(
                        endPoint,
                        listOfGenericEntity.map {
                            it.payload.fromJson<LinkedTreeMap<*, *>>()
                                .mapToObject(DiagnosisData::class.java)!!.copy(campaignId = null)
                        })
                ).run {
                    when (this) {
                        is ApiEndResponse -> {
                            insertSymDiagFhirId(
                                listOfGenericEntity, body
                            ).let { deletedRows -> if (deletedRows > 0) sendDiagnosisPostData(genericTypeEnum,endPoint) else this }
                        }

                        else -> this
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendDiagnosisPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendPriorDxPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postPriorDx(
                             endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(PriorDxResponse::class.java)!!.copy(campaignId = null)
                                    .copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertPriorDxFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendPriorDxPostData(genericTypeEnum,endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendPriorDxPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendHistoryMedicationPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postHistoryMedication(
                            endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(HistoryMedicationResponse::class.java)!!.copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertHistoryMedicationFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendHistoryMedicationPostData(genericTypeEnum, endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendHistoryMedicationPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendFamilyHistoryPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postFamilyHistory(
                            endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(FamilyHistoryResponse::class.java)!!.copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertFamilyHistoryFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendFamilyHistoryPostData(genericTypeEnum,endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendFamilyHistoryPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendAllergyPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postAllergy(
                            endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(AllergyResponse::class.java)!!.copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertAllergyFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendAllergyPostData(genericTypeEnum,endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendAllergyPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }


    override suspend fun sendRiskFactorPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postRiskFactor(
                            endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(RiskFactorResponse::class.java)!!.copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertRiskFactorsFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendRiskFactorPostData(genericTypeEnum,endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendRiskFactorPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }


    override suspend fun sendTobaccoCessationPostData(genericTypeEnum: GenericTypeEnum, endPoint: String): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = genericTypeEnum,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        historyAndTestsApiService.postTobaccoCessation(
                            endPoint,
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(TobaccoCessationResponse::class.java)!!.copy(campaignId = null)
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertTobaccoCessationFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendTobaccoCessationPostData(genericTypeEnum,endPoint)
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendTobaccoCessationPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendInterventionPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendExaminationPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun sendReferralPostData(): ResponseMapper<List<CreateResponse>> {
        return try {
            genericDao.getSameTypeGenericEntityPayload(
                genericTypeEnum = GenericTypeEnum.REFERRAL,
                syncType = SyncType.POST
            ).let { listOfGenericEntity ->
                if (listOfGenericEntity.isEmpty()) ApiEmptyResponse()
                else {
                    ApiResponseConverter.convert(
                        referralApiService.postReferral(
                            listOfGenericEntity.map {
                                it.payload.fromJson<LinkedTreeMap<*, *>>()
                                    .mapToObject(ReferralResponse::class.java)!!
                            }
                        )
                    ).apply {
                        if (this is ApiEndResponse) {
                            insertReferralFhirIds(body, listOfGenericEntity)
                                .apply {
                                    if (this > 0) sendReferralPostData()
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendReferralPostData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendPersonPatchData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendAppointmentPatchData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sendPrescriptionPutData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sentInterventionPutData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "sentExaminationPutData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertLevelsData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertHealthFacilityData(offset: Int): ResponseMapper<List<HealthFacilityResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = "$COUNT_VALUE"
        map[OFFSET] = offset.toString()
        if (preferenceRepository.getLastSyncHealthFacility() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncHealthFacility().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                referralApiService.getHealthFacility(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertHealthFacility(body)
                        //Call for next batch data
                        getAndInsertHealthFacilityData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertHealthFacility(body)
                        preferenceRepository.setLastSyncHealthFacility(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertHealthFacilityData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.PRIOR_DX,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertPriorDxData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }


    override suspend fun getAndInsertCampaignPriorDxData(offset: Int): ResponseMapper<List<PriorDxResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignPriorDx() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignPriorDx().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getPriorDx(EndPoints.CAMPAIGN_PRIOR_DX,map), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertPriorDx(body)
                        getAndInsertCampaignPriorDxData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignPriorDx(Date().time)
                        insertPriorDx(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertPriorDxData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.HISTORY_MEDICATION,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertHistoryMedicationData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }


    override suspend fun getAndInsertCampaignHistoryMedicationData(
        offset: Int
    ): ResponseMapper<List<HistoryMedicationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignHistoryMedication() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignHistoryMedication().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getHistoryMedication(
                    EndPoints.CAMPAIGN_HISTORY_MEDICATION,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertHistoryMedication(body)
                        //Call for next batch data
                        getAndInsertCampaignHistoryMedicationData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignHistoryMedication(Date().time)
                        insertHistoryMedication(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignHistoryMedicationData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.FAMILY_HISTORY,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertFamilyHistoryData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCampaignFamilyHistoryData(
        offset: Int
    ): ResponseMapper<List<FamilyHistoryResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignFamilyHistory() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignFamilyHistory().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getFamilyHistory(
                    EndPoints.CAMPAIGN_FAMILY_HISTORY,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertFamilyHistory(body)
                        //Call for next batch data
                        getAndInsertCampaignFamilyHistoryData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignFamilyHistory(Date().time)
                        insertFamilyHistory(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignFamilyHistoryData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.ALLERGY,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertAllergyData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCampaignAllergyData(offset: Int): ResponseMapper<List<AllergyResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignAllergy() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignAllergy().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getAllergy(
                    EndPoints.CAMPAIGN_ALLERGY,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertAllergy(body)
                        //Call for next batch data
                        getAndInsertCampaignAllergyData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignAllergy(Date().time)
                        insertAllergy(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignAllergyData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.RISK_FACTOR,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertRiskFactorData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertCampaignRiskFactorData(offset: Int): ResponseMapper<List<RiskFactorResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignRiskFactors() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignRiskFactors().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getRiskFactors(
                    EndPoints.CAMPAIGN_RISK_FACTORS,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertRiskFactors(body)
                        //Call for next batch data
                        getAndInsertCampaignRiskFactorData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignRiskFactors(Date().time)
                        insertRiskFactors(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignRiskFactorData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
                    EndPoints.TOBACCO_CESSATION,
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertTobaccoCessationData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
    override suspend fun getAndInsertCampaignTobaccoCessationData(offset: Int): ResponseMapper<List<TobaccoCessationResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncCampaignTobaccoCessation() != 0L) map[LAST_UPDATED] =
            String.format(
                GREATER_THAN_BUILDER,
                preferenceRepository.getLastSyncCampaignTobaccoCessation().toTimeStampDate()
            )

        return try {
            ApiResponseConverter.convert(
                historyAndTestsApiService.getTobaccoCessation(
                    EndPoints.CAMPAIGN_TOBACCO_CESSATION,
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertTobaccoCessation(body)
                        //Call for next batch data
                        getAndInsertCampaignTobaccoCessationData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        preferenceRepository.setLastSyncCampaignTobaccoCessation(Date().time)
                        insertTobaccoCessation(body)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertCampaignTobaccoCessationData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertInterventionsData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
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
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertExaminationData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun getAndInsertReferralData(
        offset: Int
    ): ResponseMapper<List<ReferralResponse>> {
        val map = mutableMapOf<String, String>()
        map[COUNT] = COUNT_VALUE.toString()
        map[OFFSET] = offset.toString()
        map[SORT] = "-$ID"
        if (preferenceRepository.getLastSyncReferral() != 0L) map[LAST_UPDATED] = String.format(
            GREATER_THAN_BUILDER, preferenceRepository.getLastSyncReferral().toTimeStampDate()
        )

        return try {
            ApiResponseConverter.convert(
                referralApiService.getReferrals(
                    map
                ), true
            ).run {
                when (this) {
                    is ApiContinueResponse -> {
                        insertReferral(body)
                        //Call for next batch data
                        getAndInsertReferralData(offset + COUNT_VALUE)
                    }

                    is ApiEndResponse -> {
                        insertReferral(body)
                        preferenceRepository.setLastSyncReferral(Date().time)
                        this
                    }

                    else -> this
                }
            }
        } catch (e: Exception) {
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(
                e,
                "getAndInsertReferralData function failed.",
                mapOf(
                    Pair(
                        USER_ID,
                        preferenceRepository.getUserDetails()?.userId ?: ERROR_FETCHING_USER_DETAILS
                    )
                )
            )
            ApiErrorResponse(
                statusCode = 0,
                errorMessage = e.localizedMessage ?: SOMETHING_WENT_WRONG
            )
        }
    }
}