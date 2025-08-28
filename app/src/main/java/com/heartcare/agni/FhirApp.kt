package com.heartcare.agni

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.heartcare.agni.data.local.enums.SyncStatusMessageEnum
import com.heartcare.agni.data.local.enums.WorkerStatus
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepositoryImpl
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepositoryImpl
import com.heartcare.agni.data.local.roomdb.FhirAppDatabase
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.ExaminationApiService
import com.heartcare.agni.data.server.api.HistoryAndTestsApiService
import com.heartcare.agni.data.server.api.InterventionApiService
import com.heartcare.agni.data.server.api.LevelsApiService
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.api.PrescriptionApiService
import com.heartcare.agni.data.server.api.ScheduleAndAppointmentApiService
import com.heartcare.agni.data.server.api.SymptomsAndDiagnosisService
import com.heartcare.agni.data.server.api.VaccinationApiService
import com.heartcare.agni.data.server.api.VitalApiService
import com.heartcare.agni.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.heartcare.agni.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepositoryImpl
import com.heartcare.agni.data.server.repository.sync.SyncRepository
import com.heartcare.agni.data.server.repository.sync.SyncRepositoryImpl
import com.heartcare.agni.service.sync.SyncService
import com.heartcare.agni.service.workmanager.request.WorkRequestBuilders
import com.heartcare.agni.utils.converters.gson.DateDeserializer
import com.heartcare.agni.utils.converters.gson.DateSerializer
import com.heartcare.agni.utils.file.DeleteFileManager
import com.heartcare.agni.utils.network.CheckNetwork
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltAndroidApp
class FhirApp : Application() {

    @Inject
    lateinit var fhirAppDatabase: FhirAppDatabase

    @Inject
    lateinit var preferenceStorage: PreferenceStorage

    @Inject
    lateinit var patientApiService: PatientApiService

    @Inject
    lateinit var prescriptionApiService: PrescriptionApiService

    @Inject
    lateinit var scheduleAndAppointmentApiService: ScheduleAndAppointmentApiService

    @Inject
    lateinit var cvdApiService: CVDApiService
    @Inject
    lateinit var vitalApiService: VitalApiService
    @Inject
    lateinit var symptomsAndDiagnosisService: SymptomsAndDiagnosisService

    @Inject
    lateinit var vaccinationApiService: VaccinationApiService
    @Inject
    lateinit var levelsApiService: LevelsApiService
    @Inject
    lateinit var historyAndTestsApiService: HistoryAndTestsApiService
    @Inject
    lateinit var interventionApiService: InterventionApiService

    @Inject
    lateinit var examinationApiService: ExaminationApiService

    @Inject
    lateinit var deleteFileManager: DeleteFileManager

    private lateinit var _syncRepository: SyncRepository
    internal val syncRepository get() = _syncRepository
    private lateinit var _genericRepository: GenericRepository
    internal val genericRepository get() = _genericRepository
    private lateinit var _workRequestBuilder: WorkRequestBuilders
    internal val workRequestBuilder get() = _workRequestBuilder
    private lateinit var _syncService: SyncService
    internal val syncService get() = _syncService
    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    private lateinit var _symDiagnosisRepository: SymptomsAndDiagnosisRepository
    private val symDiagnosisRepository get() = _symDiagnosisRepository
    internal var syncWorkerStatus = MutableLiveData<WorkerStatus>()
    internal var photosWorkerStatus = MutableLiveData<WorkerStatus>()
    private val isSyncing = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        val preferenceRepository: PreferenceRepository = PreferenceRepositoryImpl(preferenceStorage)

        _syncRepository = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            scheduleAndAppointmentApiService,
            cvdApiService,
            vitalApiService,
            symptomsAndDiagnosisService,
            vaccinationApiService,
            levelsApiService,
            historyAndTestsApiService,
            interventionApiService,
            examinationApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            preferenceRepository,
            deleteFileManager,
            fhirAppDatabase.getMedicationDao(),
            fhirAppDatabase.getPrescriptionDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao(),
            fhirAppDatabase.getPatientLastUpdatedDao(),
            fhirAppDatabase.getCVDDao(),
            fhirAppDatabase.getVitalDao(),
            fhirAppDatabase.getSymptomsAndDiagnosisDao(),
            fhirAppDatabase.getFileUploadDao(),
            fhirAppDatabase.getImmunizationRecommendationDao(),
            fhirAppDatabase.getImmunizationDao(),
            fhirAppDatabase.getManufacturerDao(),
            fhirAppDatabase.getLevelsDao(),
            fhirAppDatabase.getRiskPredictionDao(),
            fhirAppDatabase.getPriorDxDao(),
            fhirAppDatabase.getHistoryMedicationDao(),
            fhirAppDatabase.getFamilyHistoryDao(),
            fhirAppDatabase.getAllergyDao(),
            fhirAppDatabase.getRiskFactorDao(),
            fhirAppDatabase.getTobaccoCessationDao(),
            fhirAppDatabase.getInterventionDao(),
            fhirAppDatabase.getExaminationDao()
        )

        _genericRepository = GenericRepositoryImpl(
            fhirAppDatabase.getGenericDao(),
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao()
        )

        _symDiagnosisRepository = SymptomsAndDiagnosisRepositoryImpl(
            symptomsAndDiagnosisService,
            fhirAppDatabase.getSymptomsAndDiagnosisDao()
        )
        if (!this::_workRequestBuilder.isInitialized) {
            _workRequestBuilder = WorkRequestBuilders(this)
        }

        if (!this::_syncService.isInitialized) {
            _syncService =
                SyncService(
                    this,
                    syncRepository,
                    genericRepository,
                    preferenceRepository,
                    symptomsAndDiagnosisRepository = symDiagnosisRepository
                )
        }
    }

    internal suspend fun launchSyncing() {
        if (isSyncing.compareAndSet(false, true)) {
            try {
                if (CheckNetwork.isInternetAvailable(applicationContext)) {
                    val listOfErrors = mutableListOf<String>()
                    syncWorkerStatus.postValue(WorkerStatus.IN_PROGRESS)
                    preferenceStorage.syncStatus = SyncStatusMessageEnum.SYNCING_IN_PROGRESS.display
                    syncService.syncLauncher { _, errorMessage ->
                        // as there will be multiple callbacks from different coroutines
                        // list of errors is maintained.
                        // if the list is empty, then all the api calls were successful.
                        listOfErrors.add(errorMessage)
                    }.also {
                        preferenceStorage.lastSyncTime = Date().time
                        if (listOfErrors.isEmpty()) {
                            preferenceStorage.syncStatus =
                                SyncStatusMessageEnum.SYNCING_COMPLETED.display
                            syncWorkerStatus.postValue(WorkerStatus.SUCCESS)
                        } else {
                            preferenceStorage.syncStatus =
                                SyncStatusMessageEnum.SYNCING_FAILED.display
                            syncWorkerStatus.postValue(WorkerStatus.FAILED)
                        }
                    }
                }
            } finally {
                isSyncing.set(false)
            }
        }
    }

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
        }
    }
}