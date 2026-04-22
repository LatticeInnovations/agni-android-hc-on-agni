package com.heartcare.agni.data.local.repository.appointment

import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentResponseLocal
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(private val appointmentDao: AppointmentDao) :
    AppointmentRepository {

    override suspend fun getAppointmentListByDate(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay).map { appointmentEntity ->
            appointmentEntity.toAppointmentResponseLocal()
        }.groupBy {
            it.patientId
        }.map { (_, appointments) ->
            appointments.minBy { it.createdOn }
        }
    }

    override suspend fun addAppointment(appointmentResponseLocal: AppointmentResponseLocal): List<Long> {
        return appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsOfPatient(patientId).map {
            it.toAppointmentResponseLocal()
        }.groupBy {
            it.slot.start.toddMMMyyyy()
        }.map { (_, appointment) ->
            appointment.minBy { it.createdOn }
        }
    }

    override suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal? {
        return appointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)
            .minByOrNull { it.createdOn }
            ?.toAppointmentResponseLocal()
    }

    override suspend fun getAppointmentByAppointmentId(appointmentId: String): AppointmentResponseLocal {
        return appointmentDao.getAppointmentById(appointmentId)[0].toAppointmentResponseLocal()
    }

    override suspend fun updateAppointment(appointmentResponseLocal: AppointmentResponseLocal): Int {
        return appointmentDao.updateAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsOfPatientByStatus(patientId, status)
            .map { appointmentEntity ->
                appointmentEntity.toAppointmentResponseLocal()
            }
            .groupBy {
                it.slot.start.toddMMMyyyy()
            }.map { (_, appointment) ->
                appointment.minBy { it.createdOn }
            }
    }

    override suspend fun getLastCompletedAppointment(
        patientId: String
    ): AppointmentEntity? {
        return appointmentDao.getLastCompletedAppointment(patientId)
    }

    override suspend fun getAppointmentListByDateRange(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponseLocal> {

        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay)
            .map { it.toAppointmentResponseLocal() }
            .groupBy { it.patientId } // group by patient
            .mapNotNull { (_, patientAppointments) ->

                // group by createdOn
                val earliestPerCreatedOn = patientAppointments
                    .groupBy { it.createdOn.toddMMMyyyy() }
                    .map { (_, sameCreatedOnList) ->
                        sameCreatedOnList.minByOrNull { it.createdOn } // earliest in that group
                    }

                // from those, pick the latest appointment
                earliestPerCreatedOn
                    .filterNotNull()
                    .maxByOrNull { it.createdOn }
            }
    }
}