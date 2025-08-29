package com.heartcare.agni.data.local.repository.prescription

import com.heartcare.agni.data.local.model.prescription.PrescriptionResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toPrescriptionEntity
import com.heartcare.agni.utils.converters.responseconverter.toPrescriptionResponseLocal
import javax.inject.Inject

class PrescriptionRepositoryImpl @Inject constructor(
    private val prescriptionDao: PrescriptionDao
) :
    PrescriptionRepository {

    override suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long {
        return prescriptionDao.insertPrescription(prescriptionResponseLocal.toPrescriptionEntity())[0].also {
            prescriptionDao.insertPrescriptionMedicines(
                *prescriptionResponseLocal.toListOfPrescriptionDirectionsEntity().toTypedArray()
            )
        }
    }

    override suspend fun getLastPrescription(patientId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPastPrescriptions(patientId).map { it.toPrescriptionResponseLocal() }
    }

    override suspend fun getLastPrescriptionAndMedicine(patientId: String): List<PrescriptionAndMedicineRelation> {
        return prescriptionDao.getPastPrescriptions(patientId)
    }

    override suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPrescriptionByAppointmentId(appointmentId)
            .map { prescriptionAndMedicineRelation ->
                prescriptionAndMedicineRelation.toPrescriptionResponseLocal()
            }
    }

    override suspend fun deletePrescriptionDirectionEntity(prescriptionDirectionsEntity: PrescriptionDirectionsEntity): Int {
        return prescriptionDao.deletePrescriptionDirectionEntity(prescriptionDirectionsEntity)
    }
}