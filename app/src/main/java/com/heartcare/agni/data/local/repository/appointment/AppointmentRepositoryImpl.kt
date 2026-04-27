package com.heartcare.agni.data.local.repository.appointment

import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentResponseLocal
import javax.inject.Inject

import com.heartcare.agni.data.local.roomdb.dao.CampaignAppointmentDao
import com.heartcare.agni.utils.converters.responseconverter.*

class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val campaignAppointmentDao: CampaignAppointmentDao
) : AppointmentRepository {

    override suspend fun getAppointmentListByDate(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponseLocal> {
        // For facility queue, only return facility appointments
        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay).map { it.toAppointmentResponseLocal() }
            .groupBy { it.patientId }
            .map { (_, appointments) -> appointments.minBy { it.createdOn } }
    }

    override suspend fun addAppointment(appointmentResponseLocal: AppointmentResponseLocal): List<Long> {
        return if (appointmentResponseLocal.recordType == RecordType.SCREENING_SITE) {
            campaignAppointmentDao.insertAppointmentEntity(appointmentResponseLocal.toCampaignAppointmentEntity())
        } else {
            appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        }
    }

    override suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentResponseLocal> {
        // Aggregate for a unified history view
        val facility = appointmentDao.getAppointmentsOfPatient(patientId).map { it.toAppointmentResponseLocal() }
        val campaign = campaignAppointmentDao.getAppointmentsOfPatient(patientId).map { it.toAppointmentResponseLocal() }
        
        return (facility + campaign).groupBy { it.slot.start.toddMMMyyyy() }
            .map { (_, appointment) -> appointment.minBy { it.createdOn } }
    }

    override suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal? {
        // Check facility first
        val facility = appointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)
            .minByOrNull { it.createdOn }
            ?.toAppointmentResponseLocal()
        if (facility != null) return facility

        // Then campaign
        return campaignAppointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)
            .minByOrNull { it.createdOn }
            ?.toAppointmentResponseLocal()
    }

    override suspend fun getAppointmentByAppointmentId(appointmentId: String): AppointmentResponseLocal {
        val facility = appointmentDao.getAppointmentById(appointmentId)
        if (facility.isNotEmpty()) return facility[0].toAppointmentResponseLocal()
        
        val campaign = campaignAppointmentDao.getAppointmentById(appointmentId)
        if (campaign.isNotEmpty()) return campaign[0].toAppointmentResponseLocal()

        throw NoSuchElementException("Appointment not found with id: $appointmentId")
    }

    override suspend fun updateAppointment(appointmentResponseLocal: AppointmentResponseLocal): Int {
        return if (appointmentResponseLocal.recordType == RecordType.SCREENING_SITE) {
            campaignAppointmentDao.updateAppointmentEntity(appointmentResponseLocal.toCampaignAppointmentEntity())
        } else {
            appointmentDao.updateAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        }
    }

    override suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponseLocal> {
        val facility = appointmentDao.getAppointmentsOfPatientByStatus(patientId, status).map { it.toAppointmentResponseLocal() }
        val campaign = campaignAppointmentDao.getAppointmentsOfPatientByStatus(patientId, status).map { it.toAppointmentResponseLocal() }
        
        return (facility + campaign).groupBy { it.slot.start.toddMMMyyyy() }
            .map { (_, appointment) -> appointment.minBy { it.createdOn } }
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
            .flatMap { (_, patientAppointments) ->

                // group by day
                patientAppointments
                    .groupBy { it.slot.start.toddMMMyyyy() }
                    .mapNotNull { (_, sameDayAppointments) ->
                        // pick earliest appointment of that day
                        sameDayAppointments.minByOrNull { it.createdOn }
                    }
            }
    }
            
    override suspend fun loadAppointmentForCampaign(
        patientId: String,
        campaignId: String
    ): AppointmentResponseLocal? {
        return campaignAppointmentDao.getAppointmentOfPatientByCampaign(patientId, campaignId)
            ?.toAppointmentResponseLocal()
    }

    override suspend fun getCampaignAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentResponseLocal> {
        return campaignAppointmentDao.getAppointmentsOfPatient(patientId)
            .map { it.toAppointmentResponseLocal() }
    }
}