package com.heartcare.agni.utils.common

import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.AppointmentTypeEnum
import com.heartcare.agni.data.local.enums.ChangeTypeEnum
import com.heartcare.agni.data.local.enums.LastVisit
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.patch.ChangeRequest
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierAndAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastThreeMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastWeek
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.lastYear
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toSlotStartTime
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
                    )
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
                    hospitalCode = null
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
                    )
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
                        hospitalCode = user.hospitalCode
                    )
                ).also {
                    genericRepository.insertAppointment(
                        AppointmentResponse(
                            appointmentId = null,
                            uuid = appointmentId,
                            patientFhirId = patient.fhirId ?: patient.id,
                            scheduleId = scheduleFhirId
                                ?: scheduleRepository.getScheduleByStartTime(scheduleId.time, user.hospitalCode)?.uuid!!,
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
                            appUpdatedDate = Date()
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
                            scheduleId = scheduleRepository.getScheduleByStartTime(appointment.scheduleId.time, user.hospitalCode)?.scheduleId
                                ?: scheduleRepository.getScheduleByStartTime(appointment.scheduleId.time, user.hospitalCode)?.uuid!!,
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
                            appUpdatedDate = Date()
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
            || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value) {
            appointmentRepository.updateAppointment(
                appointmentResponseLocal.copy(
                    status = AppointmentStatusEnum.IN_PROGRESS.value,
                    inProgressTime = inProgressTime
                )
            )
            if (appointmentResponseLocal.appointmentId.isNullOrBlank()) {
                val user = preferenceRepository.getUserDetails()!!
                genericRepository.insertAppointment(
                    AppointmentResponse(
                        appointmentId = null,
                        createdOn = appointmentResponseLocal.createdOn,
                        uuid = appointmentResponseLocal.uuid,
                        patientFhirId = patient.fhirId ?: patient.id,
                        scheduleId = scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time, user.hospitalCode)?.scheduleId
                            ?: scheduleRepository.getScheduleByStartTime(appointmentResponseLocal.scheduleId.time, user.hospitalCode)?.uuid!!,
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
                        appUpdatedDate = Date()
                    )
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
}