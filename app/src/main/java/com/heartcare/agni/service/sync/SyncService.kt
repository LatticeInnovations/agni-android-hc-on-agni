package com.heartcare.agni.service.sync

import android.content.Context
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
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
import kotlinx.coroutines.launch

class SyncService(
    private val context: Context,
    private val syncRepository: SyncRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository
) {

    private lateinit var patientDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var scheduleDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var appointmentPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var prescriptionPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var interventionPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var examinationPatchJob: Deferred<ResponseMapper<Any>?>
    private lateinit var interventionMasterDownloadJob: Deferred<ResponseMapper<Any>?>
    private lateinit var examinationMasterDownloadJob: Deferred<ResponseMapper<Any>?>

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
                    async {
                        uploadPatientAndScheduleJob(logout)
                    },
                    async {
                        patchPatient(logout)
                    },
                    async {
                        patchPrescription(logout)
                    },
                    async {
                        patchIntervention(logout)
                    },
                    async {
                        patchExamination(logout)
                    },
                    async {
                        uploadPatientLastUpdatedData(logout)
                    },
                    async {
                        downloadLevelsRecord(logout)
                    },
                    async {
                        downloadDiagnosisMasterList(logout)
                    },
                    async {
                        downloadMedicationTiming(logout)
                    },
                    async {
                        downloadMedication(logout)
                    },
                    async {
                        downloadInterventionMasterList(logout)
                    },
                    async {
                        downloadExaminationMasterList(logout)
                    }
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
                },
                appointmentPatchJob
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse
            }.apply {
                if (this) {
                    scheduleDownloadJob = async {
                        downloadSchedule(logout)
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
                scheduleDownloadJob
            ).all { responseMapper -> responseMapper is ApiEndResponse }.apply {
                if (this) {
                    downloadAppointment(logout)
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

    /** Upload Appointment */
    private suspend fun uploadAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAppointmentPostData(), logout)?.apply {
            if (this is ApiEmptyResponse) {

                updateFhirIdInCVD(logout)
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInVital(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInPrescription(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInSymDiagnosis(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInPriorDx(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInHistoryMedication(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInFamilyHistory(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInAllergy(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInRiskFactors(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInTobaccoCessation(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInIntervention(logout)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updateFhirIdInExamination(logout)
                }
            }
        }
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
        return checkAuthenticationStatus(syncRepository.sendCVDPostData(), logout)
    }

    /** Upload Diagnosis */
    private suspend fun uploadSymDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendDiagnosisPostData(), logout)
    }

    /** Upload Vital */
    private suspend fun uploadVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendVitalPostData(), logout)
    }

    /** Upload Prior Dx */
    private suspend fun uploadPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendPriorDxPostData(), logout)
    }

    /** Upload History Medication */
    private suspend fun uploadHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendHistoryMedicationPostData(), logout)
    }

    /** Upload Family History */
    private suspend fun uploadFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendFamilyHistoryPostData(), logout)
    }

    /** Upload Allergy */
    private suspend fun uploadAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAllergyPostData(), logout)
    }

    /** Upload Risk Factors */
    private suspend fun uploadRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendRiskFactorPostData(), logout)
    }

    /** Upload Tobacco Cessation */
    private suspend fun uploadTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendTobaccoCessationPostData(), logout)
    }

    /** Upload Intervention */
    private suspend fun uploadIntervention(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendInterventionPostData(), logout)
    }

    /** Upload Examination */
    private suspend fun uploadExamination(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendExaminationPostData(), logout)
    }

    /**
     *
     *
     * Patch Syncing
     *
     *
     * */

    /** Patch Patient */
    private suspend fun patchPatient(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.sendPersonPatchData(), logout)
    }

    /** Patch Appointment */
    private suspend fun patchAppointment(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.sendAppointmentPatchData(), logout)
    }

    /** Patch Prescription */
    internal suspend fun patchPrescription(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            prescriptionPatchJob = async {
                checkAuthenticationStatus(syncRepository.sendPrescriptionPutData(), logout)
            }
        }
    }

    /** Patch Intervention */
    internal suspend fun patchIntervention(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            interventionPatchJob = async {
                checkAuthenticationStatus(syncRepository.sentInterventionPutData(), logout)
            }
        }
    }

    /** Patch Examination */
    internal suspend fun patchExamination(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            examinationPatchJob = async {
                checkAuthenticationStatus(syncRepository.sentExaminationPutData(), logout)
            }
        }
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
    private suspend fun downloadAppointment(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            awaitAll(
                async {
                    checkAuthenticationStatus(syncRepository.getAndInsertAppointment(0), logout)
                },
                prescriptionPatchJob
            ).all { responseMapper ->
                responseMapper is ApiEmptyResponse || responseMapper is ApiEndResponse
            }.apply {
                if (this) {
                    downloadPatientLastUpdated(logout)
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadCVD(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadVitals(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadPriorDx(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadHistoryMedication(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadFamilyHistory(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadAllergy(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadRiskFactors(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadTobaccoCessation(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadSymDiagnosis(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        downloadFormPrescription(null, logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        interventionMasterDownloadJob.await()
                        interventionPatchJob.await()
                        downloadIntervention(logout)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        examinationMasterDownloadJob.await()
                        examinationPatchJob.await()
                        downloadExamination(logout)
                    }
                }
            }
        }
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
    internal suspend fun downloadMedication(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertMedication(0), logout)
    }

    /** Download Intervention Master */
    internal suspend fun downloadInterventionMasterList(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            interventionMasterDownloadJob = async {
                checkAuthenticationStatus(syncRepository.getAndInsertInterventionMaster(0), logout)
            }
        }
    }

    /** Download Test and Examinations Master */
    internal suspend fun downloadExaminationMasterList(logout: (Boolean, String) -> Unit) {
        coroutineScope {
            examinationMasterDownloadJob = async {
                checkAuthenticationStatus(syncRepository.getAndInsertExaminationMaster(0), logout)
            }
        }
    }

    /** Download Diagnosis Master */
    internal suspend fun downloadDiagnosisMasterList(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertDiagnosisMaster(), logout)
    }

    /** Download Medication Timing */
    private suspend fun downloadMedicationTiming(logout: (Boolean, String) -> Unit) {
        if (preferenceRepository.getLastMedicineDosageInstructionSyncDate() == 0L) {
            checkAuthenticationStatus(syncRepository.getMedicineTime(), logout)
        }
    }

    /** Download Patient Last Updated */
    private suspend fun downloadPatientLastUpdated(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertPatientLastUpdatedData(), logout)
    }

    /** Download CVD*/
    private suspend fun downloadCVD(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertCVD(0), logout)
    }

    /** Download Diagnosis*/
    private suspend fun downloadSymDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(
            syncRepository.getAndInsertListDiagnosisData(0),
            logout
        )
    }

    /** Download Vitals*/
    private suspend fun downloadVitals(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        return checkAuthenticationStatus(syncRepository.getAndInsertListVitalData(0), logout)
    }

    /** Download Levels Data */
    private suspend fun downloadLevelsRecord(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertLevelsData(0), logout)
    }

    /** Download Prior Dx Data */
    private suspend fun downloadPriorDx(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertPriorDxData(0), logout)
    }

    /** Download History Medication Data */
    private suspend fun downloadHistoryMedication(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertHistoryMedicationData(0), logout)
    }

    /** Download Family History Data */
    private suspend fun downloadFamilyHistory(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertFamilyHistoryData(0), logout)
    }

    /** Download Allergy Data */
    private suspend fun downloadAllergy(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertAllergyData(0), logout)
    }

    /** Download Risk Factors Data */
    private suspend fun downloadRiskFactors(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertRiskFactorData(0), logout)
    }

    /** Download Tobacco Cessation Data */
    private suspend fun downloadTobaccoCessation(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertTobaccoCessationData(0), logout)
    }

    /** Download Intervention Data */
    private suspend fun downloadIntervention(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertInterventionsData(0), logout)
    }

    /** Download Examination Data */
    private suspend fun downloadExamination(logout: (Boolean, String) -> Unit) {
        checkAuthenticationStatus(syncRepository.getAndInsertExaminationData(0), logout)
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
        genericRepository.updateAppointmentFhirIds()
        /** Upload Appointment */
        return uploadAppointment(logout)
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
        genericRepository.updateCVDFhirIds()
        return uploadCVD(logout)
    }

    /** Update Appointment FHIR ID in Vitals */
    private suspend fun updateFhirIdInVital(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateVitalFhirId()
        return uploadVital(logout)
    }

    /** Update Appointment FHIR ID in Diagnosis */
    private suspend fun updateFhirIdInSymDiagnosis(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateSymDiagFhirId()
        return uploadSymDiagnosis(logout)
    }

    /** Update FHIR ID in Prior Dx */
    private suspend fun updateFhirIdInPriorDx(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updatePriorDxFhirId()
        return uploadPriorDx(logout)
    }

    /** Update FHIR ID in History Medication */
    private suspend fun updateFhirIdInHistoryMedication(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateHistoryMedicationFhirId()
        return uploadHistoryMedication(logout)
    }

    /** Update FHIR ID in Family History */
    private suspend fun updateFhirIdInFamilyHistory(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateFamilyHistoryFhirId()
        return uploadFamilyHistory(logout)
    }

    /** Update FHIR ID in Allergy */
    private suspend fun updateFhirIdInAllergy(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateAllergyFhirId()
        return uploadAllergy(logout)
    }

    /** Update FHIR ID in Risk Factors */
    private suspend fun updateFhirIdInRiskFactors(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateRiskFactorsFhirId()
        return uploadRiskFactors(logout)
    }

    /** Update FHIR ID in Tobacco Cessation */
    private suspend fun updateFhirIdInTobaccoCessation(logout: (Boolean, String) -> Unit): ResponseMapper<Any>? {
        genericRepository.updateTobaccoCessationFhirId()
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