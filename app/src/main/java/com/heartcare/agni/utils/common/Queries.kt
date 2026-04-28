package com.heartcare.agni.utils.common

import androidx.annotation.Keep
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.AppointmentTypeEnum
import com.heartcare.agni.data.local.enums.ChangeTypeEnum
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.LastVisit
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.model.appointment.AppointmentInfo
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.patch.ChangeRequest
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierAndAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastThreeMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastWeek
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastYear
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import timber.log.Timber
import java.util.Date

object Queries {
    internal suspend fun addPatientToQueue(
        patient: PatientResponse,
        scheduleRepository: ScheduleRepository,
        genericRepository: GenericRepository,
        preferenceRepository: PreferenceRepository,
        appointmentRepository: AppointmentRepository,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        addedToQueue: (List<Long>) -> Unit
    ) {
        val user = preferenceRepository.getUserDetails()!!
        val selectedSlot = Date().toSlotStartTime()
        var scheduleId = Date(
            selectedSlot.toCurrentTimeInMillis(
                Date()
            )
        )
        var scheduleFhirId: String? = null
        scheduleRepository.getScheduleByStartTime(
            selectedSlot.toCurrentTimeInMillis(
                Date()
            ), user.hospitalCode
        ).let { scheduleResponse ->
            if (scheduleResponse != null) {
                Timber.d("manseeyy already scheduled")
                scheduleId = scheduleResponse.planningHorizon.start
                scheduleFhirId = scheduleResponse.scheduleId
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(
                        bookedSlots = scheduleResponse.bookedSlots!! + 1
                    ), RecordType.FACILITY
                )
            } else {
                val uuid = UUIDBuilder.generateUUID()

                val user = preferenceRepository.getUserDetails()!!
                val schedule = ScheduleResponse(
                    uuid = uuid,
                    scheduleId = null,
                    planningHorizon = Slot(
                        start = Date(
                            selectedSlot.toCurrentTimeInMillis(
                                Date()
                            )
                        ),
                        end = Date(
                            selectedSlot.to30MinutesAfter(
                                Date()
                            )
                        )
                    ),
                    bookedSlots = null,
                    roleId = null,
                    active = null,
                    practitionerId = null,
                    hospitalId = null,
                    hospitalFhirId = null,
                    hospitalName = null,
                    hospitalCode = null,
                    campaignId = null
                )
                scheduleRepository.insertSchedule(
                    schedule.copy(
                        bookedSlots = 1,
                        roleId = user.accountGroupId.toString(),
                        active = true,
                        practitionerId = user.fhirId,
                        hospitalId = user.hospitalId.toString(),
                        hospitalFhirId = null,
                        hospitalName = user.hospitalName,
                        hospitalCode = user.hospitalCode
                    ), RecordType.FACILITY
                )
                genericRepository.insertSchedule(
                    schedule
                )
            }
        }.also {
            val appointmentId = UUIDBuilder.generateUUID()
            val createdOn = Date()
            val slot = Slot(
                start = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                end = Date(
                    Date().toAppointmentTime().to5MinutesAfter(
                        Date()
                    )
                )
            )
            val user = preferenceRepository.getUserDetails()!!
            addedToQueue(
                appointmentRepository.addAppointment(
                    AppointmentResponseLocal(
                        appointmentId = null,
                        uuid = appointmentId,
                        patientId = patient.id,
                        scheduleId = scheduleId,
                        createdOn = createdOn,
                        slot = slot,
                        status = AppointmentStatusEnum.WALK_IN.value,
                        appointmentType = AppointmentTypeEnum.WALK_IN.code,
                        inProgressTime = null,
                        roleId = user.accountGroupId.toString(),
                        slotId = null,
                        practitionerId = user.fhirId,
                        hospitalFhirId = null,
                        hospitalId = user.hospitalId.toString(),
                        hospitalName = user.hospitalName,
                        hospitalCode = user.hospitalCode,
                        campaignId = null,
                        recordType = RecordType.FACILITY
                    )
                ).also {
                    genericRepository.insertAppointment(
                        AppointmentResponse(
                            appointmentId = null,
                            uuid = appointmentId,
                            patientFhirId = patient.fhirId ?: patient.id,
                            scheduleId = scheduleFhirId
                                ?: scheduleRepository.getScheduleByStartTime(
                                    scheduleId.time,
                                    user.hospitalCode
                                )?.uuid!!,
                            createdOn = createdOn,
                            slot = slot,
                            status = AppointmentStatusEnum.WALK_IN.value,
                            appointmentType = AppointmentTypeEnum.WALK_IN.code,
                            inProgressTime = null,
                            roleId = null,
                            slotId = null,
                            practitionerId = null,
                            hospitalFhirId = null,
                            hospitalId = null,
                            hospitalName = null,
                            hospitalCode = null,
                            appUpdatedDate = Date(),
                            campaignId = null
                        )
                    )
                    updatePatientLastUpdated(
                        patient.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            )
        }
    }

    internal suspend fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        appointmentRepository: AppointmentRepository,
        genericRepository: GenericRepository,
        scheduleRepository: ScheduleRepository,
        preferenceRepository: PreferenceRepository,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        updated: (Int) -> Unit
    ) {
        val user = preferenceRepository.getUserDetails()!!
        updated(
            appointmentRepository.updateAppointment(
                appointment.copy(
                    status = AppointmentStatusEnum.ARRIVED.value
                )
            ).also {
                if (appointment.appointmentId.isNullOrBlank()) {
                    genericRepository.insertAppointment(
                        AppointmentResponse(
                            appointmentId = null,
                            createdOn = appointment.createdOn,
                            uuid = appointment.uuid,
                            patientFhirId = patient.fhirId ?: patient.id,
                            scheduleId = scheduleRepository.getScheduleByStartTime(
                                appointment.scheduleId.time,
                                user.hospitalCode
                            )?.scheduleId
                                ?: scheduleRepository.getScheduleByStartTime(
                                    appointment.scheduleId.time,
                                    user.hospitalCode
                                )?.uuid!!,
                            slot = appointment.slot,
                            status = AppointmentStatusEnum.ARRIVED.value,
                            appointmentType = appointment.appointmentType,
                            inProgressTime = appointment.inProgressTime,
                            roleId = null,
                            slotId = null,
                            practitionerId = null,
                            hospitalFhirId = null,
                            hospitalId = null,
                            hospitalName = null,
                            hospitalCode = null,
                            appUpdatedDate = Date(),
                            campaignId = null
                        )
                    )
                } else {
                    genericRepository.insertOrUpdateAppointmentPatch(
                        appointmentFhirId = appointment.appointmentId,
                        patientFhirId = patient.fhirId!!,
                        map = mapOf(
                            Pair(
                                "status",
                                ChangeRequest(
                                    operation = ChangeTypeEnum.REPLACE.value,
                                    value = AppointmentStatusEnum.ARRIVED.value
                                )
                            )
                        )
                    )
                    updatePatientLastUpdated(
                        patient.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            }
        )
    }

    suspend fun updatePatientLastUpdated(
        patientId: String,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        genericRepository: GenericRepository
    ) {
        val patientLastUpdatedResponse = PatientLastUpdatedResponse(
            uuid = patientId,
            timestamp = Date()
        )
        patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
        genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
    }

    internal suspend fun getSearchListWithLastVisited(
        lastVisited: String,
        searchList: List<PatientAndIdentifierEntity>,
        appointmentRepository: AppointmentRepository
    ): List<PatientAndIdentifierEntity> {
        val listWithCompletedAppointment = mutableListOf<PatientAndIdentifierAndAppointmentEntity>()
        val fromTime = when (lastVisited) {
            LastVisit.LAST_WEEK.label -> lastWeek()
            LastVisit.LAST_MONTH.label -> lastMonth()
            LastVisit.LAST_THREE_MONTHS.label -> lastThreeMonth()
            LastVisit.LAST_YEAR.label -> lastYear()
            else -> Date(0L)
        }
        searchList.forEach { patientAndIdentifierEntity ->
            val lastCompletedAppointment =
                appointmentRepository.getLastCompletedAppointment(patientAndIdentifierEntity.patientEntity.id)
            if (lastCompletedAppointment != null && lastCompletedAppointment.startTime > fromTime) {
                listWithCompletedAppointment.add(
                    PatientAndIdentifierAndAppointmentEntity(
                        patientAndIdentifierEntity = patientAndIdentifierEntity,
                        appointmentEntity = lastCompletedAppointment
                    )
                )
            }
        }
        return listWithCompletedAppointment.sortedByDescending {
            it.appointmentEntity.startTime
        }.map {
            it.patientAndIdentifierEntity
        }
    }

    internal suspend fun checkAndUpdateAppointmentStatusToInProgress(
        inProgressTime: Date,
        patient: PatientResponse,
        appointmentResponseLocal: AppointmentResponseLocal,
        appointmentRepository: AppointmentRepository,
        genericRepository: GenericRepository,
        scheduleRepository: ScheduleRepository,
        preferenceRepository: PreferenceRepository
    ) {
        if (appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value
            || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value
        ) {
            appointmentRepository.updateAppointment(
                appointmentResponseLocal.copy(
                    status = AppointmentStatusEnum.IN_PROGRESS.value,
                    inProgressTime = inProgressTime
                )
            )
            if (appointmentResponseLocal.appointmentId.isNullOrBlank()) {
                val user = preferenceRepository.getUserDetails()!!
                val isCampaign = appointmentResponseLocal.recordType == RecordType.SCREENING_SITE
                val resolvedScheduleId = if (isCampaign && !appointmentResponseLocal.campaignId.isNullOrEmpty()) {
                    scheduleRepository.getCampaignScheduleByStartTime(
                        appointmentResponseLocal.scheduleId.time,
                        appointmentResponseLocal.campaignId
                    )?.scheduleId
                        ?: scheduleRepository.getCampaignScheduleByCampaign(
                            appointmentResponseLocal.campaignId
                        )?.uuid!!
                } else {
                    scheduleRepository.getScheduleByStartTime(
                        appointmentResponseLocal.scheduleId.time,
                        user.hospitalCode
                    )?.scheduleId
                        ?: scheduleRepository.getScheduleByStartTime(
                            appointmentResponseLocal.scheduleId.time,
                            user.hospitalCode
                        )?.uuid!!
                }
                val genericType = if (isCampaign) GenericTypeEnum.CAMPAIGN_APPOINTMENT else GenericTypeEnum.APPOINTMENT
                genericRepository.insertAppointment(
                    AppointmentResponse(
                        appointmentId = null,
                        createdOn = appointmentResponseLocal.createdOn,
                        uuid = appointmentResponseLocal.uuid,
                        patientFhirId = patient.fhirId ?: patient.id,
                        scheduleId = resolvedScheduleId,
                        slot = appointmentResponseLocal.slot,
                        status = AppointmentStatusEnum.IN_PROGRESS.value,
                        appointmentType = appointmentResponseLocal.appointmentType,
                        inProgressTime = inProgressTime,
                        roleId = null,
                        slotId = null,
                        practitionerId = null,
                        hospitalFhirId = null,
                        hospitalId = null,
                        hospitalName = null,
                        hospitalCode = null,
                        appUpdatedDate = Date(),
                        campaignId = appointmentResponseLocal.campaignId
                    ),
                    type = genericType
                )
            } else {
                genericRepository.insertOrUpdateAppointmentPatch(
                    appointmentFhirId = appointmentResponseLocal.appointmentId,
                    patientFhirId = patient.fhirId!!,
                    map = mapOf(
                        Pair(
                            "generatedOn",
                            inProgressTime
                        ),
                        Pair(
                            "status",
                            ChangeRequest(
                                operation = ChangeTypeEnum.REPLACE.value,
                                value = AppointmentStatusEnum.IN_PROGRESS.value
                            )
                        )
                    )
                )
            }
        }
    }

    suspend fun getInProgressCompletedAppointmentIds(
        patientId: String,
        appointmentRepository: AppointmentRepository
    ): List<String> {
        val standardAppointments = appointmentRepository.getAppointmentsOfPatient(patientId)
            .filter { appointment ->
                appointment.status == AppointmentStatusEnum.IN_PROGRESS.value
                        || appointment.status == AppointmentStatusEnum.COMPLETED.value
            }
        return standardAppointments.groupBy { appointment -> appointment.slot.start.toddMMMyyyy() }
            .map { (_, appointments) ->
                appointments.minBy { it.createdOn }.uuid
            }
    }
    suspend fun getScreenSiteAppointmentIds(
        patientId: String,
        appointmentRepository: AppointmentRepository
    ): List<String> {
        val appointments = appointmentRepository.getCampaignAppointmentsOfPatient(patientId)
            .filter { appointment ->
                appointment.status == AppointmentStatusEnum.WALK_IN.value
            }
        return appointments.map { it.uuid }


    }

    suspend fun loadAppointmentInfo(
        patientId: String,
        hospitalCode: String,
        maxNumberOfAppointmentsInADay: Int,
        appointmentRepository: AppointmentRepository
    ): AppointmentInfo {
        val todayStart = Date().toTodayStartDate()
        val todayEnd = Date().toEndOfDay()

        val appointment = appointmentRepository
            .getAppointmentsOfPatientByStatus(patientId, AppointmentStatusEnum.SCHEDULED.value)
            .sortedBy { it.createdOn }
            .firstOrNull { response ->
                response.slot.start.time in (todayStart..todayEnd) &&
                        response.hospitalCode == hospitalCode
            }

        val appointmentResponse = appointmentRepository
            .getAppointmentsOfPatientByDate(patientId, todayStart, todayEnd)

        val existsInOtherHospital = appointmentResponse?.hospitalCode?.let { it != hospitalCode } == true

        val canAddAssessment =
            (appointmentResponse?.status == AppointmentStatusEnum.ARRIVED.value ||
                    appointmentResponse?.status == AppointmentStatusEnum.WALK_IN.value ||
                    appointmentResponse?.status == AppointmentStatusEnum.IN_PROGRESS.value) &&
                    appointmentResponse.hospitalCode == hospitalCode

        val isAppointmentCompleted =
            appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value &&
                    appointmentResponse.hospitalCode == hospitalCode

        val ifAllSlotsBooked = appointmentRepository
            .getAppointmentListByDate(todayStart, todayEnd)
            .count { local ->
                local.status != AppointmentStatusEnum.CANCELLED.value &&
                        local.hospitalCode == hospitalCode
            } >= maxNumberOfAppointmentsInADay

        return AppointmentInfo(
            appointment = appointment,
            existsInOtherHospital = existsInOtherHospital,
            canAddAssessment = canAddAssessment,
            isAppointmentCompleted = isAppointmentCompleted,
            ifAllSlotsBooked = ifAllSlotsBooked
        )
    }

    suspend fun getAppointment(
        patientId: String,
        hospitalCode: String,
        campaignId: String? = null,
        appointmentRepository: AppointmentRepository
    ): AppointmentResponseLocal? {
        return if (campaignId != null) {
            appointmentRepository.loadAppointmentForCampaign(patientId, campaignId)
        } else {
            appointmentRepository
                .getAppointmentListByDate(Date().toTodayStartDate(), Date().toEndOfDay())
                .sortedBy { it.createdOn }
                .firstOrNull { appointmentEntity ->
                    appointmentEntity.patientId == patientId &&
                            appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value &&
                            appointmentEntity.hospitalCode == hospitalCode
                }
        }
    }

    /** Screening Site Master Data — returns only active sites within today's date range */
    internal suspend fun getScreeningSites(
        screeningSiteDao: ScreeningSiteDao
    ): List<ScreeningSiteMasterEntity> {
        return screeningSiteDao.getActiveScreeningSites()
    }

    /** Facility specific logic path (Day-wise) */
    internal suspend fun addPatientToFacilityQueue(
        patient: PatientResponse,
        scheduleRepository: ScheduleRepository,
        genericRepository: GenericRepository,
        preferenceRepository: PreferenceRepository,
        appointmentRepository: AppointmentRepository,
        patientLastUpdatedRepository: PatientLastUpdatedRepository,
        addedToQueue: (List<Long>) -> Unit
    ) {
        addPatientToQueue(
            patient,
            scheduleRepository,
            genericRepository,
            preferenceRepository,
            appointmentRepository,
            patientLastUpdatedRepository,
            addedToQueue
        )
    }

    /** Campaign specific logic path (Full Campaign Duration) */
    internal suspend fun addPatientToCampaignQueue(
        patient: PatientResponse,
        campaignId: String,
        scheduleRepository: ScheduleRepository,
        genericRepository: GenericRepository,
        appointmentRepository: AppointmentRepository,
        addedToQueue: (List<Long>) -> Unit
    ) {
        val existingAppointment = appointmentRepository.loadAppointmentForCampaign(patient.id, campaignId)
        
        if (existingAppointment != null) {
            Timber.d("Patient already has an appointment for this campaign: ${existingAppointment.uuid}")
            addedToQueue(listOf(1L)) 
            return
        }

        val selectedSlot = Date().toSlotStartTime()
        var scheduleId = Date(selectedSlot.toCurrentTimeInMillis(Date()))
        var scheduleFhirId: String? = null
        scheduleRepository.getCampaignScheduleByCampaign(campaignId).let { scheduleResponse ->
            if (scheduleResponse != null) {
                scheduleId = scheduleResponse.planningHorizon.start
                scheduleFhirId= scheduleResponse.scheduleId
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(bookedSlots = (scheduleResponse.bookedSlots!!) + 1),
                    RecordType.SCREENING_SITE
                )
            } else {
                val uuid = UUIDBuilder.generateUUID()
                val schedule = ScheduleResponse(
                    uuid = uuid,
                    scheduleId = null,
                    planningHorizon = Slot(
                        start = Date(
                            selectedSlot.toCurrentTimeInMillis(
                                Date()
                            )
                        ),
                        end = Date(
                            selectedSlot.to30MinutesAfter(
                                Date()
                            )
                        )
                    ),
                    bookedSlots = null,
                    roleId = null,
                    active = null,
                    practitionerId = null,
                    hospitalId = null,
                    hospitalFhirId = null,
                    hospitalName = null,
                    hospitalCode = null,
                    campaignId = campaignId
                )
                scheduleRepository.insertSchedule(schedule.copy(
                    bookedSlots =  1,
                    active = true
                ), RecordType.SCREENING_SITE)
                genericRepository.insertSchedule(schedule, GenericTypeEnum.CAMPAIGN_SCHEDULE)
            }
        }.also {
            val appointmentId = UUIDBuilder.generateUUID()
            val createdOn = Date()
            val slot = Slot(
                start = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                end = Date(Date().toAppointmentTime().to5MinutesAfter(Date()))
            )
            
            addedToQueue(
                appointmentRepository.addAppointment(
                    AppointmentResponseLocal(
                        appointmentId = null,
                        uuid = appointmentId,
                        patientId = patient.id,
                        scheduleId = scheduleId,
                        createdOn = createdOn,
                        slot = slot,
                        status = AppointmentStatusEnum.WALK_IN.value,
                        appointmentType = AppointmentTypeEnum.WALK_IN.code,
                        inProgressTime = null,
                        roleId = null,
                        slotId = null,
                        practitionerId = null,
                        hospitalFhirId = null,
                        hospitalId = null,
                        hospitalName = null,
                        hospitalCode = null,
                        campaignId = campaignId,
                        recordType = RecordType.SCREENING_SITE
                    )
                ).also {
                    genericRepository.insertAppointment(
                        AppointmentResponse(
                            uuid = appointmentId,
                            createdOn = createdOn,
                            appointmentId = null,
                            patientFhirId = patient.fhirId ?: patient.id,
                            scheduleId = scheduleFhirId?: scheduleRepository.getCampaignScheduleByStartTime(scheduleId.time, campaignId)?.uuid!!,
                            slot = slot,
                            status = AppointmentStatusEnum.WALK_IN.value,
                            appointmentType = AppointmentTypeEnum.WALK_IN.code,
                            inProgressTime = null,
                            roleId = null,
                            slotId = null,
                            practitionerId = null,
                            hospitalFhirId = null,
                            hospitalId = null,
                            hospitalName = null,
                            hospitalCode = null,
                            campaignId = campaignId,
                            appUpdatedDate = Date()
                        ),
                        type = GenericTypeEnum.CAMPAIGN_APPOINTMENT
                    )
                }
            )
        }
    }

    /** Context-aware Appointment Info Loading for Campaigns */
    internal suspend fun loadCampaignAppointmentInfo(
        patientId: String,
        campaignId: String,
        appointmentRepository: AppointmentRepository,
        cvdAssessmentRepository: CVDAssessmentRepository,
        screeningSiteDao: ScreeningSiteDao
    ): AppointmentInfo {
        val appointmentResponse = appointmentRepository.loadAppointmentForCampaign(patientId, campaignId)
        
        val latestCvd = cvdAssessmentRepository.getLatestCVDForCampaign(patientId, campaignId)
        val screeningSite = screeningSiteDao.getScreeningSiteById(campaignId)
        
        var isDuplicateCVDForCampaign = false
        if (latestCvd != null && screeningSite != null) {
            val fromDateMilli = screeningSite.fromDate.toTimeInMilli()
            val toDateMilli = screeningSite.toDate.toTimeInMilli()
            val currentTime = Date().time
            if (currentTime in fromDateMilli..toDateMilli) {
                isDuplicateCVDForCampaign = true
            }
        }

        val canAddAssessment = appointmentResponse != null && 
                               appointmentResponse.status == AppointmentStatusEnum.WALK_IN.value &&
                               !isDuplicateCVDForCampaign

        val isAppointmentCompleted = appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value

        return AppointmentInfo(
            appointment = appointmentResponse,
            existsInOtherHospital = false,
            canAddAssessment = canAddAssessment,
            isAppointmentCompleted = isAppointmentCompleted,
            ifAllSlotsBooked = false, // Campaign logic typically bypasses strict slot limits
            isDuplicateCVDForCampaign = isDuplicateCVDForCampaign
        )
    }

    /** Outreach / Screening Site Appointment Info — detects if a record exists for current campaign */
    internal suspend fun loadHistoryCampaignAppointmentInfo(
        patientId: String,
        campaignId: String,
        appointmentRepository: AppointmentRepository,
    ): AppointmentInfo {
        val appointmentResponse = appointmentRepository.loadAppointmentForCampaign(patientId, campaignId)
       return AppointmentInfo(
            appointment = appointmentResponse,
            existsInOtherHospital = false,
            canAddAssessment = appointmentResponse != null &&
                    appointmentResponse.status == AppointmentStatusEnum.WALK_IN.value,
            isAppointmentCompleted = false,
            ifAllSlotsBooked = false,
            isDuplicateCVDForCampaign = false
        )
    }
    /** Campaign-specific Vital Info — detects if a vital record exists within the campaign window */
    internal suspend fun loadCampaignVitalInfo(
        patientId: String,
        campaignId: String,
        appointmentRepository: AppointmentRepository,
        vitalRepository: VitalRepository,
        screeningSiteDao: ScreeningSiteDao
    ): CampaignVitalInfo {
        val appointment = appointmentRepository.loadAppointmentForCampaign(patientId, campaignId)
        val existingVital = vitalRepository.getLatestVitalForCampaign(patientId, campaignId)
        val screeningSite = screeningSiteDao.getScreeningSiteById(campaignId)

        val isWithinCampaignWindow = if (screeningSite != null) {
            val now = Date().time
            now in screeningSite.fromDate.toTimeInMilli()..screeningSite.toDate.toTimeInMilli()
        } else false

        // hasExistingRecord = true means UPDATE mode (load existing vital into form)
        val hasExistingRecord = existingVital != null && isWithinCampaignWindow

        return CampaignVitalInfo(
            appointment = appointment,
            existingVital = existingVital,
            hasExistingRecord = hasExistingRecord
        )
    }
}

/** Holds campaign-specific vital state returned from loadCampaignVitalInfo */
@Keep
data class CampaignVitalInfo(
    val appointment: AppointmentResponseLocal?,
    val existingVital: VitalResponse?,
    val hasExistingRecord: Boolean  // true = update mode; false = create new
)