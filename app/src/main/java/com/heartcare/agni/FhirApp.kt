package com.heartcare.agni

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.heartcare.agni.data.local.enums.SyncStatusMessageEnum
import com.heartcare.agni.data.local.enums.WorkerStatus
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLogger
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLoggerImpl
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepositoryImpl
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepositoryImpl
import com.heartcare.agni.data.local.roomdb.FhirAppDatabase
import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.CampaignApiService
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
import com.heartcare.agni.data.server.repository.sync.SyncRepository
import com.heartcare.agni.data.server.repository.sync.SyncRepositoryImpl
import com.heartcare.agni.service.sync.SyncService
import com.heartcare.agni.service.workmanager.request.WorkRequestBuilders
import com.heartcare.agni.utils.common.ScreeningSiteSeeder
import com.heartcare.agni.utils.converters.gson.DateDeserializer
import com.heartcare.agni.utils.converters.gson.DateSerializer
import com.heartcare.agni.utils.network.CheckNetwork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    lateinit var diagnosisApiService: DiagnosisApiService

    @Inject
    lateinit var levelsApiService: LevelsApiService
    @Inject
    lateinit var historyAndTestsApiService: HistoryAndTestsApiService
    @Inject
    lateinit var interventionApiService: InterventionApiService

    @Inject
    lateinit var examinationApiService: ExaminationApiService

    @Inject
    lateinit var referralApiService: ReferralApiService

    @Inject
    lateinit var campaignApiService: CampaignApiService

    @Inject
    lateinit var crashlytics: FirebaseCrashlytics

    private lateinit var _syncRepository: SyncRepository
    internal val syncRepository get() = _syncRepository
    private lateinit var _genericRepository: GenericRepository
    internal val genericRepository get() = _genericRepository
    private lateinit var _workRequestBuilder: WorkRequestBuilders
    internal val workRequestBuilder get() = _workRequestBuilder
    private lateinit var _syncService: SyncService
    internal val syncService get() = _syncService

    private lateinit var _crashlyticsLogger: CrashlyticsLogger
    internal val crashlyticsLogger get() = _crashlyticsLogger

    val sessionExpireFlow = MutableLiveData<Map<String, Any>>(emptyMap())

    internal var syncWorkerStatus = MutableLiveData<WorkerStatus>()
    private val isSyncing = AtomicBoolean(false)

    lateinit var screeningSiteSeeder: ScreeningSiteSeeder
    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("sqlcipher")
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }

        val preferenceRepository: PreferenceRepository = PreferenceRepositoryImpl(preferenceStorage)

        _crashlyticsLogger = CrashlyticsLoggerImpl(
            crashlytics
        )

        _syncRepository = SyncRepositoryImpl(
            patientApiService,
            prescriptionApiService,
            scheduleAndAppointmentApiService,
            cvdApiService,
            vitalApiService,
            diagnosisApiService,
            levelsApiService,
            historyAndTestsApiService,
            interventionApiService,
            examinationApiService,
            referralApiService,
            campaignApiService,
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getGenericDao(),
            preferenceRepository,
            fhirAppDatabase.getMedicationDao(),
            fhirAppDatabase.getPrescriptionDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao(),
            fhirAppDatabase.getPatientLastUpdatedDao(),
            fhirAppDatabase.getCVDDao(),
            fhirAppDatabase.getVitalDao(),
            fhirAppDatabase.getDiagnosisDao(),
            fhirAppDatabase.getLevelsDao(),
            fhirAppDatabase.getRiskPredictionDao(),
            fhirAppDatabase.getPriorDxDao(),
            fhirAppDatabase.getHistoryMedicationDao(),
            fhirAppDatabase.getFamilyHistoryDao(),
            fhirAppDatabase.getAllergyDao(),
            fhirAppDatabase.getRiskFactorDao(),
            fhirAppDatabase.getTobaccoCessationDao(),
            fhirAppDatabase.getInterventionDao(),
            fhirAppDatabase.getExaminationDao(),
            fhirAppDatabase.getHealthFacilityDao(),
            fhirAppDatabase.getReferralDao(),
            fhirAppDatabase.getScreeningSiteMasterDao(),
            fhirAppDatabase.getCampaignScheduleDao(),
            fhirAppDatabase.getCampaignAppointmentDao(),
            crashlyticsLogger
        )
        /* To load dummy data for Screen site
            will remove this once api is ready
         */
        screeningSiteSeeder= ScreeningSiteSeeder(fhirAppDatabase.getScreeningSiteMasterDao(), preferenceRepository)


        _genericRepository = GenericRepositoryImpl(
            fhirAppDatabase.getGenericDao(),
            fhirAppDatabase.getPatientDao(),
            fhirAppDatabase.getScheduleDao(),
            fhirAppDatabase.getAppointmentDao(),
            fhirAppDatabase.getCampaignScheduleDao(),
            fhirAppDatabase.getCampaignAppointmentDao()
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
                    preferenceRepository
                )
        }

        /* To load dummy data for Screen site
            will remove this once api is ready
         */
        CoroutineScope(Dispatchers.IO).launch {
            getScreeningSites(fhirAppDatabase.getScreeningSiteMasterDao(),screeningSiteSeeder)
        }

    }
    internal suspend fun getScreeningSites(
        screeningSiteDao: ScreeningSiteDao,
        screeningSiteSeeder: ScreeningSiteSeeder
    ): List<ScreeningSiteMasterEntity> {
        screeningSiteSeeder.seedMockData()
        return screeningSiteDao.getScreeningSiteMaster()
    }


    internal suspend fun launchSyncing() {
        // load dummy screen site data
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
            } catch (e: Exception){
                Timber.e(e, e.localizedMessage)
                crashlyticsLogger.logException(e)
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