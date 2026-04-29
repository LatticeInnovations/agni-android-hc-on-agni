package com.heartcare.agni.service.sync

import android.content.Context
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.server.constants.EndPoints
import com.heartcare.agni.data.server.repository.sync.SyncRepository
import com.heartcare.agni.utils.constants.ErrorConstants
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEmptyResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiErrorResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import com.heartcare.agni.utils.network.CheckNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SyncService(
    private val context: Context,
    private val syncRepository: SyncRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository
) {

    private lateinit var patientDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var scheduleDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var campaignScheduleDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var appointmentPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var prescriptionPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var interventionPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var examinationPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var interventionMasterDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var examinationMasterDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var healthFacilityDownloadJob: Deferred<ResponseMapper<Any>?>

    /**
     *
     *
     * Launcher
     *
     *
     * */

    internal suspend fun syncLauncher(logout: (Boolean, String) -> Unit) {
        if (CheckNetwork.isInternetAvailable(context)) {
            coroutineScope {
                awaitAll(
                    async { uploadPatientAndScheduleJob(logout) },
                    async { patchPatient(logout) },
                    async { patchPrescription(logout) },
                    async { patchIntervention(logout) },
                    async { patchExamination(logout) },
                    async { uploadPatientLastUpdatedData(logout) },
                    async { downloadLevelsRecord(logout) },
                    async { downloadDiagnosisMasterList(logout) },
                    async { downloadMedicationTiming(logout) },
                    async { downloadMedication(logout) },
                    async { downloadInterventionMasterList(logout) },
                    async { downloadExaminationMasterList(logout) },
                    async { downloadScreeningSiteMasterList(logout) }
                )
            }
        }
    }

    /**
     *
     * Upload patient and schedule
     * Asynchronously
     *
     * */

    private suspend fun uploadPatientAndScheduleJob(logout: (Boolean, String) -> Unit): Boolean {
        return coroutineScope {
            awaitAll(
                async {
                    uploadPatient(logout)
                },
                async {
                    uploadSchedule(logout)
                },
                async {
                    uploadCampaignSchedule(logout)
                }
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse
            }.apply {
                if (this) {
                    downloadScheduleJob(logout)
                }
            }
        }
    }

    /**
     *
     * Download Schedule after Uploading Appointment and Patch Appointments
     * Asynchronously
     *
     * */

    private suspend fun downloadScheduleJob(logout: (Boolean, String) -> Unit) {
        return coroutineScope {
            awaitAll(
                async {
                    updateFhirIdsInAppointment(logout)
                }, async {
                    updateFhirIdsInCampaignAppointment(logout)
                },
                appointmentPatchJob
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse
            }.apply {
                if (this) {
                    scheduleDownloadJob = async {
                        downloadSchedule(logout)
                    }
                    campaignScheduleDownloadJob = async {
                        downloadCampaignSchedule(logout)
                    }
                    downloadAppointmentJob(logout)
                }
            }
        }
    }

    /**
     *
     * Download Appointment after Downloading Schedule and Patients
     * Asynchronously
     *
     * */

    private suspend fun downloadAppointmentJob(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            awaitAll(
                patientDownloadJob,
                scheduleDownloadJob,
                campaignScheduleDownloadJob
            ).all { responseMapper -> responseMapper is ApiEndResponse }.apply {
                if (this) {
                    downloadAppointment(logout)
                    downloadCampaignAppointment(logout)
                }
            }
        }
    }

    /**
     *
     *
     * Upload Syncing
     *
     *
     * */

    /** Upload Patient */
    private suspend fun uploadPatient(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPersonPostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {
                CoroutineScope(Dispatchers.IO).apply {
                    patientDownloadJob = async {
                        downloadPatient(logout)
                    }
                }
            }
        }
    }

    /** Upload Schedule */
    private suspend fun uploadSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendSchedulePostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {
                appointmentPatchJob = CoroutineScope(Dispatchers.IO).async {
                    updateScheduleFhirIdInAppointmentPatch(logout)
                }
            }
        }
    }

    /** Upload Campaign Schedule */
    private suspend fun uploadCampaignSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendCampaignSchedulePostData(), logout)
    }

    /** Upload Appointment */
    private suspend fun uploadAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        val response = checkAuthenticationStatus(
            syncRepository.sendAppointmentPostData(
                genericTypeEnum = GenericTypeEnum.APPOINTMENT,
                EndPoints.APPOINTMENT
            ),
            logout
        )
        coroutineScope {
            if (response is ApiEmptyResponse) {
                // Run all update jobs concurrently
                val jobs = listOf(
                    async { updateFhirIdInCVD(logout) },
                    async { updateFhirIdInVital(logout) },
                    async { updateFhirIdInPrescription(logout) },
                    async { updateFhirIdInDiagnosis(logout) },
                    async { updateFhirIdInPriorDx(logout) },
                    async { updateFhirIdInHistoryMedication(logout) },
                    async { updateFhirIdInFamilyHistory(logout) },
                    async { updateFhirIdInAllergy(logout) },
                    async { updateFhirIdInRiskFactors(logout) },
                    async { updateFhirIdInTobaccoCessation(logout) },
                    async { updateFhirIdInIntervention(logout) },
                    async { updateFhirIdInExamination(logout) },
                    async { updateFhirIdInReferral(logout) }
                )

                // Wait for all of them to complete
                jobs.awaitAll()
            }
        }
        return response
    }

    /** Upload Campaign Appointment */
    private suspend fun uploadCampaignAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        val response = checkAuthenticationStatus(
            syncRepository.sendAppointmentPostData(
                GenericTypeEnum.CAMPAIGN_APPOINTMENT, EndPoints.CAMPAIGN_APPOINTMENT
            ), logout
        )
        coroutineScope {
            if (response is ApiEmptyResponse) {
                // Run all update jobs concurrently
                val jobs = listOf(
                    async { updateFhirIdInCampaignCVD(logout) },
                    async { updateFhirIdInCampaignVital(logout) },
                    async { updateFhirIdInCampaignPriorDx(logout) },
                    async { updateFhirIdInCampaignHistoryMedication(logout) },
                    async { updateFhirIdInCampaignFamilyHistory(logout) },
                    async { updateFhirIdInCampaignAllergy(logout) },
                    async { updateFhirIdInCampaignRiskFactors(logout) },
                    async { updateFhirIdInCampaignTobaccoCessation(logout) },
                    async { updateFhirIdInCampaignDiagnosis(logout) }
                )

                // Wait for all of them to complete
                jobs.awaitAll()
            }
        }
        return response
    }

    /** Upload Form Prescription*/
    private suspend fun uploadFormPrescriptionData(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendFormPrescriptionPostData(), logout)
    }

    /** Upload Patient Last Updated Data */
    private suspend fun uploadPatientLastUpdatedData(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPatientLastUpdatePostData(), logout)
    }

    /** Upload CVD */
    private suspend fun uploadCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendCVDPostData(GenericTypeEnum.CVD,EndPoints.CVD), logout)
    }

    /** Upload Campaign CVD */
    private suspend fun uploadCampaignCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendCVDPostData(GenericTypeEnum.CAMPAIGN_CVD,EndPoints.CAMPAIGN_CVD), logout)
    }

    /** Upload Diagnosis */
    private suspend fun uploadDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendDiagnosisPostData(GenericTypeEnum.DIAGNOSIS,EndPoints.DIAGNOSIS), logout)
    }
    private suspend fun uploadCampaignDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendDiagnosisPostData(GenericTypeEnum.CAMPAIGN_DIAGNOSIS,EndPoints.CAMPAIGN_DIAGNOSIS), logout)

    }

    /** Upload Vital */
    private suspend fun uploadVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendVitalPostData(GenericTypeEnum.VITAL,EndPoints.VITAL), logout)
    }

    /** Upload Campaign Vital */
    private suspend fun uploadCampaignVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendVitalPostData(GenericTypeEnum.CAMPAIGN_VITAL,EndPoints.CAMPAIGN_VITAL), logout)
    }

    /** Upload Prior Dx */
    private suspend fun uploadPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPriorDxPostData(GenericTypeEnum.PRIOR_DX,EndPoints.PRIOR_DX), logout)
    }

    /** Upload Campaign Prior Dx */
    private suspend fun uploadCampaignPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPriorDxPostData(GenericTypeEnum.CAMPAIGN_PRIOR_DX,EndPoints.CAMPAIGN_PRIOR_DX), logout)
    }

    /** Upload History Medication */
    private suspend fun uploadHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendHistoryMedicationPostData(GenericTypeEnum.HISTORY_MEDICATION,EndPoints.HISTORY_MEDICATION), logout)
    }

    /** Upload Campaign History Medication */
    private suspend fun uploadCampaignHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.sendHistoryMedicationPostData(GenericTypeEnum.CAMPAIGN_HISTORY_MEDICATION,EndPoints.CAMPAIGN_HISTORY_MEDICATION),
            logout
        )
    }
    /** Upload Family History */
    private suspend fun uploadFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendFamilyHistoryPostData(GenericTypeEnum.FAMILY_HISTORY,EndPoints.FAMILY_HISTORY), logout)
    }
    /** Upload Campaign Family History */
    private suspend fun uploadCampaignFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendFamilyHistoryPostData(GenericTypeEnum.CAMPAIGN_FAMILY_HISTORY,EndPoints.CAMPAIGN_FAMILY_HISTORY), logout)
    }

    /** Upload Allergy */
    private suspend fun uploadAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAllergyPostData(GenericTypeEnum.ALLERGY,EndPoints.ALLERGY), logout)
    }
    /** Upload Campaign Allergy */
    private suspend fun uploadCampaignAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAllergyPostData(GenericTypeEnum.CAMPAIGN_ALLERGY,EndPoints.CAMPAIGN_ALLERGY), logout)
    }

    /** Upload Risk Factors */
    private suspend fun uploadRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendRiskFactorPostData(GenericTypeEnum.RISK_FACTOR,EndPoints.RISK_FACTOR), logout)
    }

    /** Upload Campaign Risk Factors */
    private suspend fun uploadCampaignRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendRiskFactorPostData(GenericTypeEnum.CAMPAIGN_RISK_FACTORS,EndPoints.CAMPAIGN_RISK_FACTORS), logout)
    }

    /** Upload Tobacco Cessation */
    private suspend fun uploadTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendTobaccoCessationPostData(GenericTypeEnum.TOBACCO_CESSATION,EndPoints.TOBACCO_CESSATION), logout)
    }

    /** Upload Campaign Tobacco Cessation */
    private suspend fun uploadCampaignTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.sendTobaccoCessationPostData(GenericTypeEnum.CAMPAIGN_TOBACCO_CESSATION,EndPoints.CAMPAIGN_TOBACCO_CESSATION),
            logout
        )
    }

    /** Upload Intervention */
    private suspend fun uploadIntervention(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendInterventionPostData(), logout)
    }

    /** Upload Examination */
    private suspend fun uploadExamination(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendExaminationPostData(), logout)
    }

    /** Upload Referral */
    private suspend fun uploadReferral(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendReferralPostData(), logout)
    }

    /**
     *
     *
     * Patch Syncing
     *
     *
     * */

    /** Patch Patient */
    private suspend fun patchPatient(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPersonPatchData(), logout)
    }

    /** Patch Appointment */
    private suspend fun patchAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAppointmentPatchData(), logout)
    }

    /** Patch Prescription */
    internal suspend fun patchPrescription(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            prescriptionPatchJob = async {
                checkAuthenticationStatus(syncRepository.sendPrescriptionPutData(), logout)
            }
        }
        return prescriptionPatchJob.await()
    }

    /** Patch Intervention */
    internal suspend fun patchIntervention(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            interventionPatchJob = async {
                checkAuthenticationStatus(syncRepository.sentInterventionPutData(), logout)
            }
        }
        return interventionPatchJob.await()
    }

    /** Patch Examination */
    internal suspend fun patchExamination(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            examinationPatchJob = async {
                checkAuthenticationStatus(syncRepository.sentExaminationPutData(), logout)
            }
        }
        return examinationPatchJob.await()
    }

    /**
     *
     *
     * Download Syncing
     *
     *
     * */

    /** Download Patient */
    private suspend fun downloadPatient(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListPatientData(0),
            logout
        )
    }

    /** Download Schedule */
    private suspend fun downloadSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertSchedule(0), logout)
    }

    /** Download Appointment*/
    private suspend fun downloadAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? =
        coroutineScope {
            val response = checkAuthenticationStatus(
                syncRepository.getAndInsertAppointment(0),
                logout
            )

            if (response is ApiEmptyResponse || response is ApiEndResponse) {
                val jobs = listOf(
                    async { downloadPatientLastUpdated(logout) },
                    async { downloadCVD(logout) },
                    async { downloadVitals(logout) },
                    async { downloadPriorDx(logout) },
                    async { downloadHistoryMedication(logout) },
                    async { downloadFamilyHistory(logout) },
                    async { downloadAllergy(logout) },
                    async { downloadRiskFactors(logout) },
                    async { downloadTobaccoCessation(logout) },
                    async { downloadDiagnosis(logout) },
                    async {
                        prescriptionPatchJob.await()
                        downloadFormPrescription(null, logout)
                    },
                    async {
                        interventionMasterDownloadJob.await()
                        interventionPatchJob.await()
                        downloadIntervention(logout)
                    },
                    async {
                        examinationMasterDownloadJob.await()
                        examinationPatchJob.await()
                        downloadExamination(logout)
                    },
                    async { downloadReferral(logout) }
                )
                jobs.awaitAll()
            }

            response
        }

    /** Download Campaign Appointment */
    private suspend fun downloadCampaignAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? =
        coroutineScope {
            val response =
                checkAuthenticationStatus(syncRepository.getAndInsertCampaignAppointment(0), logout)

            if (response is ApiEmptyResponse || response is ApiEndResponse) {
                val jobs = listOf(
                    async { downloadPatientLastUpdated(logout) },
                    async { downloadCampaignCVD(logout) },
                    async { downloadCampaignVitals(logout) },
                    async { downloadCampaignPriorDx(logout) },
                    async { downloadCampaignHistoryMedication(logout) },
                    async { downloadCampaignFamilyHistory(logout) },
                    async { downloadCampaignAllergy(logout) },
                    async { downloadCampaignRiskFactors(logout) },
                    async { downloadCampaignTobaccoCessation(logout) },
                    async { downloadCampaignDiagnosis(logout) }
                )
                jobs.awaitAll()
            }

            response
        }

    /** Download Form Prescription*/
    internal suspend fun downloadFormPrescription(
        patientId: String?,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertFormPrescription(patientId),
            logout
        )
    }

    /** Download Medication */
    internal suspend fun downloadMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertMedication(0), logout)
    }

    /** Download Intervention Master */
    internal suspend fun downloadInterventionMasterList(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            interventionMasterDownloadJob = async {
                checkAuthenticationStatus(syncRepository.getAndInsertInterventionMaster(0), logout)
            }
        }
        return interventionMasterDownloadJob.await()
    }

    /** Download Test and Examinations Master */
    internal suspend fun downloadExaminationMasterList(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            examinationMasterDownloadJob = async {
                checkAuthenticationStatus(syncRepository.getAndInsertExaminationMaster(0), logout)
            }
        }
        return examinationMasterDownloadJob.await()
    }

    /** Download Diagnosis Master */
    internal suspend fun downloadDiagnosisMasterList(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertDiagnosisMaster(), logout)
    }

    /** Download Screening Site Master */
    internal suspend fun downloadScreeningSiteMasterList(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertScreeningSiteMaster(), logout)
    }

    /** Download Medication Timing */
    private suspend fun downloadMedicationTiming(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return if (preferenceRepository.getLastMedicineDosageInstructionSyncDate() == 0L) {
            checkAuthenticationStatus(syncRepository.getMedicineTime(), logout)
        } else null
    }

    /** Download Patient Last Updated */
    private suspend fun downloadPatientLastUpdated(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertPatientLastUpdatedData(),
            logout
        )
    }

    /** Download CVD*/
    private suspend fun downloadCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCVD(0), logout)
    }

    /** Download Campaign CVD */
    private suspend fun downloadCampaignCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCampaignCVD(0), logout)
    }

    /** Download Diagnosis*/
    private suspend fun downloadDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListDiagnosisData(0),
            logout
        )
    }

    private suspend fun downloadCampaignDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertCampaignDiagnosisData(0),
            logout
        )
    }

    /** Download Vitals*/
    private suspend fun downloadVitals(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertListVitalData(0), logout)
    }

    private suspend fun downloadCampaignVitals(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCampaignVitalData(0), logout)
    }

    /** Download Campaign Prior Dx */
    private suspend fun downloadCampaignPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCampaignPriorDxData(0), logout)
    }

    /** Download Campaign History Medication */
    private suspend fun downloadCampaignHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertCampaignHistoryMedicationData(0),
            logout
        )
    }

    /** Download Campaign Family History */
    private suspend fun downloadCampaignFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertCampaignFamilyHistoryData(0),
            logout
        )
    }

    /** Download Campaign Allergy */
    private suspend fun downloadCampaignAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCampaignAllergyData(0), logout)
    }

    /** Download Campaign Risk Factors */
    private suspend fun downloadCampaignRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertCampaignRiskFactorData(0),
            logout
        )
    }

    /** Download Campaign Tobacco Cessation */
    private suspend fun downloadCampaignTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertCampaignTobaccoCessationData(0),
            logout
        )
    }

    /** Download Campaign Schedule */
    private suspend fun downloadCampaignSchedule(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCampaignSchedule(0), logout)
    }


    /** Download Levels Data */
    private suspend fun downloadLevelsRecord(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        val response = checkAuthenticationStatus(
            syncRepository.getAndInsertLevelsData(0),
            logout
        )

        return when (response) {
            is ApiEmptyResponse, is ApiEndResponse -> downloadHealthFacility(logout)
            else -> response
        }
    }

    /** Download Health Facility Data */
    private suspend fun downloadHealthFacility(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        coroutineScope {
            healthFacilityDownloadJob = async {
                checkAuthenticationStatus(syncRepository.getAndInsertHealthFacilityData(0), logout)
            }
        }
        return healthFacilityDownloadJob.await()
    }

    /** Download Prior Dx Data */
    private suspend fun downloadPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertPriorDxData(0), logout)
    }

    /** Download History Medication Data */
    private suspend fun downloadHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertHistoryMedicationData(0),
            logout
        )
    }

    /** Download Family History Data */
    private suspend fun downloadFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertFamilyHistoryData(0), logout)
    }

    /** Download Allergy Data */
    private suspend fun downloadAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertAllergyData(0), logout)
    }

    /** Download Risk Factors Data */
    private suspend fun downloadRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertRiskFactorData(0), logout)
    }

    /** Download Tobacco Cessation Data */
    private suspend fun downloadTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertTobaccoCessationData(0), logout)
    }

    /** Download Intervention Data */
    private suspend fun downloadIntervention(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertInterventionsData(0), logout)
    }

    /** Download Examination Data */
    private suspend fun downloadExamination(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertExaminationData(0), logout)
    }

    /** Download Referral Data */
    private suspend fun downloadReferral(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        val response = healthFacilityDownloadJob.await()
        return when (response) {
            is ApiEndResponse, is ApiEmptyResponse -> {
                checkAuthenticationStatus(syncRepository.getAndInsertReferralData(0), logout)
            }

            else -> response
        }
    }

    /**
     *
     *
     * Update FHIR ID in Generic Entity
     *
     *
     * */

    /** Update Schedule and Patient FHIR ID in Appointment */
    private suspend fun updateFhirIdsInAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAppointmentFhirIds(GenericTypeEnum.APPOINTMENT)
        /** Upload Appointment */
        return uploadAppointment(logout)
    }

    private suspend fun updateFhirIdsInCampaignAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAppointmentFhirIds(GenericTypeEnum.CAMPAIGN_APPOINTMENT)
        /** Upload Campaign Appointment */
        return uploadCampaignAppointment(logout)
    }

    /** Update Schedule FHIR ID in Appointment Patch */
    private suspend fun updateScheduleFhirIdInAppointmentPatch(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAppointmentFhirIdInPatch()
        /** Patch Appointment */
        return patchAppointment(logout)
    }

    /** Update Appointment FHIR ID in Prescription */
    private suspend fun updateFhirIdInPrescription(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updatePrescriptionFhirId()
        /** Upload Prescription */
        return uploadFormPrescriptionData(logout)
    }

    /** Update Appointment FHIR ID in CVD */
    private suspend fun updateFhirIdInCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateCVDFhirIds(GenericTypeEnum.CVD)
        val cvdResponse = uploadCVD(logout)
        return cvdResponse
    }

    /** Update Appointment FHIR ID in CVD */
    private suspend fun updateFhirIdInCampaignCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateCVDFhirIds(GenericTypeEnum.CAMPAIGN_CVD)
        val cvdResponse = uploadCampaignCVD(logout)
        return cvdResponse
    }

    /** Update Appointment FHIR ID in Vitals */
    private suspend fun updateFhirIdInVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateVitalFhirId(GenericTypeEnum.VITAL)
        val vitalResponse = uploadVital(logout)
        return vitalResponse
    }

    private suspend fun updateFhirIdInCampaignVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateVitalFhirId(GenericTypeEnum.CAMPAIGN_VITAL)
        val vitalResponse = uploadCampaignVital(logout)
        return vitalResponse
    }

    /** Update Appointment FHIR ID in Campaign Prior Dx */
    private suspend fun updateFhirIdInCampaignPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updatePriorDxFhirId(GenericTypeEnum.CAMPAIGN_PRIOR_DX)
        return uploadCampaignPriorDx(logout)
    }

    /** Update Appointment FHIR ID in Campaign History Medication */
    private suspend fun updateFhirIdInCampaignHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateHistoryMedicationFhirId(GenericTypeEnum.CAMPAIGN_HISTORY_MEDICATION)
        return uploadCampaignHistoryMedication(logout)
    }

    /** Update Appointment FHIR ID in Campaign Family History */
    private suspend fun updateFhirIdInCampaignFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateFamilyHistoryFhirId(GenericTypeEnum.CAMPAIGN_FAMILY_HISTORY)
        return uploadCampaignFamilyHistory(logout)
    }

    /** Update Appointment FHIR ID in Campaign Allergy */
    private suspend fun updateFhirIdInCampaignAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAllergyFhirId(GenericTypeEnum.CAMPAIGN_ALLERGY)
        return uploadCampaignAllergy(logout)
    }

    /** Update Appointment FHIR ID in Campaign Risk Factors */
    private suspend fun updateFhirIdInCampaignRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateRiskFactorsFhirId(GenericTypeEnum.CAMPAIGN_RISK_FACTORS)
        return uploadCampaignRiskFactors(logout)
    }

    /** Update Appointment FHIR ID in Campaign Tobacco Cessation */
    private suspend fun updateFhirIdInCampaignTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateTobaccoCessationFhirId(GenericTypeEnum.CAMPAIGN_TOBACCO_CESSATION)
        return uploadCampaignTobaccoCessation(logout)
    }

    /** Update Appointment FHIR ID in Campaign Diagnosis */
    private suspend fun updateFhirIdInCampaignDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateDiagnosisFhirId(GenericTypeEnum.CAMPAIGN_DIAGNOSIS)
        return uploadCampaignDiagnosis(logout)
    }


    /** Update Appointment FHIR ID in Diagnosis */
    private suspend fun updateFhirIdInDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateDiagnosisFhirId(GenericTypeEnum.DIAGNOSIS)
        return uploadDiagnosis(logout)
    }

    /** Update FHIR ID in Prior Dx */
    private suspend fun updateFhirIdInPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updatePriorDxFhirId(GenericTypeEnum.PRIOR_DX)
        return uploadPriorDx(logout)
    }

    /** Update FHIR ID in History Medication */
    private suspend fun updateFhirIdInHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateHistoryMedicationFhirId(GenericTypeEnum.HISTORY_MEDICATION)
        return uploadHistoryMedication(logout)
    }

    /** Update Appointment FHIR ID in Family History */
    private suspend fun updateFhirIdInFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateFamilyHistoryFhirId(GenericTypeEnum.FAMILY_HISTORY)
        /** Upload Family History */
        return uploadFamilyHistory(logout)
    }

    /** Update Appointment FHIR ID in Allergy */
    private suspend fun updateFhirIdInAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAllergyFhirId(GenericTypeEnum.ALLERGY)
        /** Upload Allergy */
        return uploadAllergy(logout)
    }

    /** Update Appointment FHIR ID in Risk Factors */
    private suspend fun updateFhirIdInRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateRiskFactorsFhirId(GenericTypeEnum.RISK_FACTOR)
        /** Upload Risk Factors */
        return uploadRiskFactors(logout)
    }

    /** Update Appointment FHIR ID in Tobacco Cessation */
    private suspend fun updateFhirIdInTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateTobaccoCessationFhirId(GenericTypeEnum.TOBACCO_CESSATION)
        /** Upload Tobacco Cessation */
        return uploadTobaccoCessation(logout)
    }

    /** Update FHIR ID in Intervention */
    private suspend fun updateFhirIdInIntervention(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateInterventionFhirId()
        return uploadIntervention(logout)
    }

    /** Update FHIR ID in Examination */
    private suspend fun updateFhirIdInExamination(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateExaminationFhirId()
        return uploadExamination(logout)
    }

    /** Update FHIR ID in Referral */
    private suspend fun updateFhirIdInReferral(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateReferralFhirId()
        return uploadReferral(logout)
    }

    /** Check Session Expiry and Authorization */
    private fun checkAuthenticationStatus(
        responseMapper: ResponseMapper<Any>,
        logout: (Boolean, String) -> Unit
    ): ResponseMapper<Any>? {
        return if (responseMapper is ApiErrorResponse) {
            if (responseMapper.errorMessage == ErrorConstants.SESSION_EXPIRED || responseMapper.errorMessage == ErrorConstants.UNAUTHORIZED) {
                logout(true, responseMapper.errorMessage)
            } else logout(false, responseMapper.errorMessage)
            null
        } else {
            responseMapper
        }
    }
}