package com.heartcare.agni.data.local.roomdb

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.heartcare.agni.BuildConfig
import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.DiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.local.roomdb.dao.FamilyHistoryDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.HealthFacilityDao
import com.heartcare.agni.data.local.roomdb.dao.HistoryMedicationDao
import com.heartcare.agni.data.local.roomdb.dao.IdentifierDao
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
import com.heartcare.agni.data.local.roomdb.dao.SearchDao
import com.heartcare.agni.data.local.roomdb.dao.TobaccoCessationDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.local.roomdb.entities.allergy.AllergyEntity
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.cvd.CVDEntity
import com.heartcare.agni.data.local.roomdb.entities.cvd.RiskPredictionCharts
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisEntity
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationEntity
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.family.FamilyHistoryEntity
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.local.roomdb.entities.healthfacility.HealthFacilityEntity
import com.heartcare.agni.data.local.roomdb.entities.historymedication.HistoryMedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.intervention.InterventionEntity
import com.heartcare.agni.data.local.roomdb.entities.intervention.InterventionMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.levels.LevelEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.IdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.priordx.PriorDxEntity
import com.heartcare.agni.data.local.roomdb.entities.referral.ReferralEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.RiskFactorEntity
import com.heartcare.agni.data.local.roomdb.entities.schedule.ScheduleEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchHistoryEntity
import com.heartcare.agni.data.local.roomdb.entities.tobacco.TobaccoCessationEntity
import com.heartcare.agni.data.local.roomdb.entities.vitals.VitalEntity
import com.heartcare.agni.data.local.roomdb.typeconverters.DiagnosisTypeConverter
import com.heartcare.agni.data.local.roomdb.typeconverters.TypeConverter
import com.heartcare.agni.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.nio.charset.StandardCharsets
import java.util.UUID

@Database(
    entities = [
        PatientEntity::class,
        GenericEntity::class,
        IdentifierEntity::class,
        SearchHistoryEntity::class,
        MedicationEntity::class,
        PrescriptionEntity::class,
        PrescriptionDirectionsEntity::class,
        MedicineTimingEntity::class,
        ScheduleEntity::class,
        AppointmentEntity::class,
        PatientLastUpdatedEntity::class,
        RiskPredictionCharts::class,
        CVDEntity::class,
        VitalEntity::class,
        DiagnosisMasterEntity::class,
        DiagnosisEntity::class,
        SearchEntity::class,
        LevelEntity::class,
        PriorDxEntity::class,
        HistoryMedicationEntity::class,
        FamilyHistoryEntity::class,
        AllergyEntity::class,
        RiskFactorEntity::class,
        TobaccoCessationEntity::class,
        InterventionMasterEntity::class,
        InterventionEntity::class,
        ExaminationMasterEntity::class,
        ExaminationEntity::class,
        ReferralEntity::class,
        HealthFacilityEntity::class
    ],
    views = [PrescriptionDirectionAndMedicineView::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(TypeConverter::class, DiagnosisTypeConverter::class)
abstract class FhirAppDatabase : RoomDatabase() {

    abstract fun getPatientDao(): PatientDao
    abstract fun getIdentifierDao(): IdentifierDao
    abstract fun getGenericDao(): GenericDao
    abstract fun getSearchDao(): SearchDao
    abstract fun getPrescriptionDao(): PrescriptionDao
    abstract fun getMedicationDao(): MedicationDao
    abstract fun getScheduleDao(): ScheduleDao
    abstract fun getAppointmentDao(): AppointmentDao
    abstract fun getPatientLastUpdatedDao(): PatientLastUpdatedDao
    abstract fun getRiskPredictionDao(): RiskPredictionDao
    abstract fun getCVDDao(): CVDDao
    abstract fun getVitalDao(): VitalDao
    abstract fun getDiagnosisDao(): DiagnosisDao
    abstract fun getLevelsDao(): LevelsDao
    abstract fun getPriorDxDao(): PriorDxDao
    abstract fun getHistoryMedicationDao(): HistoryMedicationDao
    abstract fun getFamilyHistoryDao(): FamilyHistoryDao
    abstract fun getAllergyDao(): AllergyDao
    abstract fun getRiskFactorDao(): RiskFactorDao
    abstract fun getTobaccoCessationDao(): TobaccoCessationDao
    abstract fun getInterventionDao(): InterventionDao
    abstract fun getExaminationDao(): ExaminationDao
    abstract fun getReferralDao(): ReferralDao
    abstract fun getHealthFacilityDao(): HealthFacilityDao

    companion object {
        @Volatile
        private var instance: FhirAppDatabase? = null
        fun getInstance(context: Context, preferenceStorage: PreferenceStorage): FhirAppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, preferenceStorage).also { instance = it }
            }
        }

        private fun buildDatabase(
            context: Context, preferenceStorage: PreferenceStorage
        ): FhirAppDatabase {

            if (preferenceStorage.roomDBEncryptionKey.isBlank()) {
                preferenceStorage.roomDBEncryptionKey = UUID.randomUUID().toString()
            }

            val passphrase: ByteArray =
                preferenceStorage.roomDBEncryptionKey.toByteArray(StandardCharsets.UTF_8)
            val factory = SupportOpenHelperFactory(passphrase)

            return if (BuildConfig.DEBUG) {
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "heartcare_agni")
                    .build()
            } else {
                Room.databaseBuilder(context, FhirAppDatabase::class.java, "heartcare_agni")
                    .openHelperFactory(factory)
                    .build()
            }
        }
    }
}