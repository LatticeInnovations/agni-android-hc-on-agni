package com.heartcare.agni.data.server.repository.sync

import com.heartcare.agni.data.local.enums.DispenseStatusEnum
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.PhotoDeleteEnum
import com.heartcare.agni.data.local.enums.PhotoUploadTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CVDDao
import com.heartcare.agni.data.local.roomdb.dao.DispenseDao
import com.heartcare.agni.data.local.roomdb.dao.FamilyHistoryDao
import com.heartcare.agni.data.local.roomdb.dao.FileUploadDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.dao.HistoryMedicationDao
import com.heartcare.agni.data.local.roomdb.dao.LabTestAndMedDao
import com.heartcare.agni.data.local.roomdb.dao.LevelsDao
import com.heartcare.agni.data.local.roomdb.dao.MedicationDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.PatientLastUpdatedDao
import com.heartcare.agni.data.local.roomdb.dao.PrescriptionDao
import com.heartcare.agni.data.local.roomdb.dao.PriorDxDao
import com.heartcare.agni.data.local.roomdb.dao.RelationDao
import com.heartcare.agni.data.local.roomdb.dao.RiskFactorDao
import com.heartcare.agni.data.local.roomdb.dao.RiskPredictionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.heartcare.agni.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.heartcare.agni.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.heartcare.agni.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.IdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.heartcare.agni.data.local.roomdb.entities.relation.RelationEntity
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.create.LabDocumentIdResponse
import com.heartcare.agni.data.server.model.create.MedDocumentIdResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.dispense.response.DispenseData
import com.heartcare.agni.data.server.model.dispense.response.MedicineDispenseResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.labormed.labtest.LabTestResponse
import com.heartcare.agni.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicineTimeResponse
import com.heartcare.agni.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.relatedperson.RelatedPersonResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationRecommendationResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationResponse
import com.heartcare.agni.data.server.model.vaccination.ManufacturerResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.constants.ErrorConstants
import com.heartcare.agni.utils.constants.ErrorConstants.APPOINTMENT_ERROR
import com.heartcare.agni.utils.constants.ErrorConstants.DUPLICATE_RECORD
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters
import com.heartcare.agni.utils.converters.responseconverter.Vaccination.toImmunizationEntity
import com.heartcare.agni.utils.converters.responseconverter.Vaccination.toImmunizationFileEntity
import com.heartcare.agni.utils.converters.responseconverter.Vaccination.toImmunizationRecommendationEntity
import com.heartcare.agni.utils.converters.responseconverter.Vaccination.toManufacturerEntity
import com.heartcare.agni.utils.converters.responseconverter.toAllergyEntity
import com.heartcare.agni.utils.converters.responseconverter.toAppointmentEntity
import com.heartcare.agni.utils.converters.responseconverter.toCVDEntity
import com.heartcare.agni.utils.converters.responseconverter.toDispensePrescriptionEntity
import com.heartcare.agni.utils.converters.responseconverter.toFamilyHistoryEntity
import com.heartcare.agni.utils.converters.responseconverter.toHistoryMedicationEntity
import com.heartcare.agni.utils.converters.responseconverter.toLabTestEntity
import com.heartcare.agni.utils.converters.responseconverter.toLabTestPhotoResponseLocal
import com.heartcare.agni.utils.converters.responseconverter.toLevelEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfDispenseDataEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfId
import com.heartcare.agni.utils.converters.responseconverter.toListOfIdentifierEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfLabTestAndMedPhotoEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfLabTestPhotoEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfMedicationEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfMedicineDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfMedicineDispenseListEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.heartcare.agni.utils.converters.responseconverter.toListOfPrescriptionPhotoEntity
import com.heartcare.agni.utils.converters.responseconverter.toMedRecordPhotoResponseLocal
import com.heartcare.agni.utils.converters.responseconverter.toPatientEntity
import com.heartcare.agni.utils.converters.responseconverter.toPatientLastUpdatedEntity
import com.heartcare.agni.utils.converters.responseconverter.toPrescriptionEntity
import com.heartcare.agni.utils.converters.responseconverter.toPriorDxEntity
import com.heartcare.agni.utils.converters.responseconverter.toRelationEntity
import com.heartcare.agni.utils.converters.responseconverter.toRiskFactorEntity
import com.heartcare.agni.utils.converters.responseconverter.toScheduleEntity
import com.heartcare.agni.utils.converters.responseconverter.toSymptomsAndDiagnosisEntity
import com.heartcare.agni.utils.converters.responseconverter.toVitalEntity
import com.heartcare.agni.utils.file.DeleteFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

open class SyncRepositoryDatabaseTransactions(
    private val patientApiService: PatientApiService,
    private val patientDao: PatientDao,
    private val genericDao: GenericDao,
    private val relationDao: RelationDao,
    private val medicationDao: MedicationDao,
    private val prescriptionDao: PrescriptionDao,
    private val scheduleDao: ScheduleDao,
    private val appointmentDao: AppointmentDao,
    private val patientLastUpdatedDao: PatientLastUpdatedDao,
    private val cvdDao: CVDDao,
    private val vitalDao: VitalDao,
    private val symptomsAndDiagnosisDao: SymptomsAndDiagnosisDao,
    private val labTestAndMedDao: LabTestAndMedDao,
    private val dispenseDao: DispenseDao,
    private val fileUploadDao: FileUploadDao,
    private val deleteFileManager: DeleteFileManager,
    private val immunizationRecommendationDao: ImmunizationRecommendationDao,
    private val immunizationDao: ImmunizationDao,
    private val manufacturerDao: ManufacturerDao,
    private val levelsDao: LevelsDao,
    private val riskPredictionDao: RiskPredictionDao,
    private val priorDxDao: PriorDxDao,
    private val historyMedicationDao: HistoryMedicationDao,
    private val familyHistoryDao: FamilyHistoryDao,
    private val allergyDao: AllergyDao,
    private val riskFactorDao: RiskFactorDao
) {


    protected suspend fun insertPatient(body: List<PatientResponse>) {
        //Insert Patient Data
        patientDao.insertPatientData(*body.map { it.toPatientEntity() }.toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()
        val identifierList = mutableListOf<IdentifierEntity>()

        body.map { patientResponse ->
            listOfGenericEntity.addAll(
                listOf(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId!!,
                        type = GenericTypeEnum.FHIR_IDS,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_PRESCRIPTION,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_PRESCRIPTION_PHOTO,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_DISPENSE,
                        syncType = SyncType.POST
                    ),
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = patientResponse.id,
                        payload = patientResponse.fhirId,
                        type = GenericTypeEnum.FHIR_IDS_OTC,
                        syncType = SyncType.POST
                    )
                )
            )
            patientResponse.toListOfIdentifierEntity().let { listOfIdentifiers ->
                identifierList.addAll(listOfIdentifiers)
            }
        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

        //Insert Identifier Data
        patientDao.insertIdentifiers(*identifierList.toTypedArray())
    }

    protected suspend fun insertRelations(body: List<RelatedPersonResponse>) {
        val relationEntity = mutableListOf<RelationEntity>()
        body.map { relatedPersonResponse ->
            if (relatedPersonResponse.relationship.isNotEmpty()) {
                relatedPersonResponse.relationship.map { relationship ->
                    relationEntity.add(
                        relationship.toRelationEntity(
                            relatedPersonResponse.id,
                            patientDao,
                            patientApiService
                        )
                    )
                }
            }
        }
        if (relationEntity.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                relationDao.insertRelation(
                    *relationEntity.toTypedArray()
                )
            }
        }
    }

    protected suspend fun insertFormPrescriptions(body: List<PrescriptionResponse>) {
        prescriptionDao.insertPrescription(*body.map { prescriptionResponse ->
            prescriptionResponse.toPrescriptionEntity(
                patientDao
            )
        }.toTypedArray())
        val medicineDirections = mutableListOf<PrescriptionDirectionsEntity>()
        body.forEach { prescriptionResponse ->
            medicineDirections.addAll(
                prescriptionResponse.toListOfPrescriptionDirectionsEntity(
                    medicationDao
                )
            )
        }
        prescriptionDao.insertPrescriptionMedicines(
            *medicineDirections.toTypedArray()
        )
    }

    protected suspend fun insertPhotoPrescriptions(body: List<PrescriptionPhotoResponse>) {
        val savedPhotoPrescription = body.filter { it.status == PhotoDeleteEnum.SAVED.value }
        prescriptionDao.insertPrescription(
            *savedPhotoPrescription.map { prescriptionResponse ->
                prescriptionResponse.toPrescriptionEntity(
                    patientDao
                )
            }.toTypedArray()
        )
        val prescriptionPhotos = mutableListOf<PrescriptionPhotoEntity>()
        savedPhotoPrescription.forEach { prescriptionResponse ->
            prescriptionPhotos.addAll(
                prescriptionResponse.toListOfPrescriptionPhotoEntity()
            )
        }
        prescriptionDao.insertPrescriptionPhotos(
            *prescriptionPhotos.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()
        savedPhotoPrescription.map { prescriptionPhotoResponse ->
            prescriptionPhotoResponse.prescription.map {
                it.filename
            }.forEach { fileName ->
                listOfGenericEntity.add(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = prescriptionPhotoResponse.prescriptionId,
                        payload = fileName,
                        type = GenericTypeEnum.PRESCRIPTION_PHOTO,
                        syncType = SyncType.POST
                    )
                )
            }
        }
        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

        body.filter { it.status == PhotoDeleteEnum.DELETE.value }
            .map { deletedPhotoPrescription ->
                fileUploadDao.deleteFile(deletedPhotoPrescription.prescription[0].filename)
                deleteFileManager.removeFromInternalStorage(deletedPhotoPrescription.prescription[0].filename)
                prescriptionDao.deletePrescriptionPhoto(deletedPhotoPrescription.toListOfPrescriptionPhotoEntity()[0])
                    .also {
                        prescriptionDao.deletePrescriptionEntity(
                            deletedPhotoPrescription.toPrescriptionEntity(
                                patientDao
                            )
                        )
                    }
            }
    }

    protected suspend fun insertMedication(body: List<MedicationResponse>) {
        medicationDao.insertMedication(
            *body.toListOfMedicationEntity().toTypedArray()
        )
    }

    protected suspend fun insertMedicationTiming(body: List<MedicineTimeResponse>) {
        medicationDao.insertMedicineDosageInstructions(
            *body.toListOfMedicineDirectionsEntity().toTypedArray()
        )
    }

    protected suspend fun insertSchedule(body: List<ScheduleResponse>) {
        scheduleDao.insertScheduleEntity(*body.map { scheduleResponse ->
            scheduleResponse.toScheduleEntity()
        }.toTypedArray())
    }

    protected suspend fun insertAppointment(body: List<AppointmentResponse>) {
        appointmentDao.insertAppointmentEntity(*body.map { appointmentResponse ->
            appointmentResponse.toAppointmentEntity(patientDao, scheduleDao)
        }.toTypedArray())
    }

    protected suspend fun insertCVD(body: List<CVDResponse>) {
        cvdDao.insertCVDRecord(*body.map { cvdResponse ->
            cvdResponse.toCVDEntity(patientDao, appointmentDao, riskPredictionDao)
        }.toTypedArray())
    }

    protected suspend fun insertVital(body: List<VitalResponse>) {
        //Insert Vital Data
        vitalDao.insertVital(*body.map { it.toVitalEntity(patientDao, appointmentDao) }
            .toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertSymDiag(body: List<SymptomsAndDiagnosisResponse>) {
        //Insert Vital Data
        symptomsAndDiagnosisDao.insertSymptomsAndDiagnosis(*body.map {
            it.toSymptomsAndDiagnosisEntity(
                patientDao,
                appointmentDao
            )
        }
            .toTypedArray())

        val listOfGenericEntity = mutableListOf<GenericEntity>()

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertPatientFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            patientDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertPhotoPrescriptionFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                prescriptionDao.updatePrescriptionFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.prescriptionFiles!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateDocumentFhirId(
                        prescriptionResponse.documentUuid,
                        prescriptionResponse.documentfhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertPrescriptionAndMedicationRequestFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                prescriptionDao.updatePrescriptionFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.prescription!!.forEach { prescriptionResponse ->
                    prescriptionDao.updateMedReqFhirId(
                        prescriptionResponse.medReqUuid,
                        prescriptionResponse.medReqFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertScheduleFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null, ErrorConstants.SCHEDULE_EXISTS -> {
                    scheduleDao.updateScheduleFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }

                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertAppointmentFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                appointmentDao.updateAppointmentFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }


    protected suspend fun insertPatientLastUpdated(body: List<PatientLastUpdatedResponse>) {
        //Insert Patient Last Updated Data
        patientLastUpdatedDao.insertPatientLastUpdatedData(*body.map { it.toPatientLastUpdatedEntity() }
            .toTypedArray())
    }

    protected suspend fun insertCVDFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    cvdDao.updateCVDFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                APPOINTMENT_ERROR, DUPLICATE_RECORD -> {
                    cvdDao.deleteCVD(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertVitalFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            vitalDao.updateVitalFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertSymDiagFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>
    ): Int {
        body.map { createResponse ->
            symptomsAndDiagnosisDao.updateSymDiagFhirId(
                createResponse.id!!, createResponse.fhirId!!
            )
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    private suspend fun deleteGenericEntityByListOfIds(idsToDelete: List<String>): Int {
        return genericDao.deleteSyncPayload(idsToDelete)
    }

    protected suspend fun deleteGenericEntityData(listOfGenericEntities: List<GenericEntity>): Int {
        return genericDao.deleteSyncPayload(listOfGenericEntities.toListOfId())
    }


    protected suspend fun insertLabTest(body: List<LabTestResponse>, type: String) {
        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.toLabTestPhotoResponseLocal(
                        labTestResponse,
                        appointmentDao,
                        patientDao
                    ).toLabTestEntity(type)
                }.also { labTests ->
                    labTestAndMedDao.insertLabAndMedTest(*labTests.toTypedArray())
                }
        }

        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.DELETE.value }
                .map {
                    fileUploadDao.deleteFile(it.documents[0].filename)
                    deleteFileManager.removeFromInternalStorage(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedPhoto(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedEntity(it.diagnosticUuid)
                }
        }

        val labTestAndMedPhotoEntity = mutableListOf<LabTestAndMedPhotoEntity>()
        body.forEach { response ->
            labTestAndMedPhotoEntity.addAll(
                response.toListOfLabTestPhotoEntity()
            )
        }
        labTestAndMedDao.insertLabTestsAndMedPhotos(
            *labTestAndMedPhotoEntity.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()

        body.map { labTestResponse ->
            labTestResponse.diagnosticReport.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.documents.forEach { fileName ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = it.diagnosticReportFhirId,
                                payload = fileName.filename,
                                type = GenericTypeEnum.PHOTO_DOWNLOAD,
                                syncType = SyncType.POST
                            )
                        )

                    }

                }

        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertMedicalRecord(body: List<MedicalRecordResponse>, type: String) {
        body.map { medicalRecordResponse ->
            medicalRecordResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.toMedRecordPhotoResponseLocal(
                        medicalRecordResponse,
                        appointmentDao, patientDao
                    ).toLabTestEntity(type)
                }.also { labTests ->
                    labTestAndMedDao.insertLabAndMedTest(*labTests.toTypedArray())
                }

        }
        body.map { labTestResponse ->
            labTestResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.DELETE.value }
                .map {
                    fileUploadDao.deleteFile(it.documents[0].filename)
                    deleteFileManager.removeFromInternalStorage(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedPhoto(it.documents[0].filename)
                    labTestAndMedDao.deleteLabTestAndMedEntity(it.medicalReportUuid)
                }
        }
        val labTestAndMedPhotoEntity = mutableSetOf<LabTestAndMedPhotoEntity>()
        body.forEach { response ->
            labTestAndMedPhotoEntity.addAll(
                response.toListOfLabTestAndMedPhotoEntity()
            )
        }
        labTestAndMedDao.insertLabTestsAndMedPhotos(
            *labTestAndMedPhotoEntity.toTypedArray()
        )
        val listOfGenericEntity = mutableListOf<GenericEntity>()

        body.map { labTestResponse ->
            labTestResponse.medicalRecord.filter { it.status == PhotoDeleteEnum.SAVED.value }
                .map {
                    it.documents.forEach { fileName ->
                        listOfGenericEntity.add(
                            GenericEntity(
                                id = UUID.randomUUID().toString(),
                                patientId = it.medicalRecordFhirId,
                                payload = fileName.filename,
                                type = GenericTypeEnum.PHOTO_DOWNLOAD,
                                syncType = SyncType.POST
                            )
                        )

                    }

                }

        }

        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )

    }

    protected suspend fun insertLabOrMedFhirId(
        listOfGenericEntities: List<GenericEntity>, body: List<CreateResponse>, type: String
    ): Int {
        body.map { createResponse ->
            labTestAndMedDao.updateLabTestAndFhirId(
                createResponse.id!!, createResponse.fhirId!!
            )
            if (type == PhotoUploadTypeEnum.LAB_TEST.value) {
                val labDocumentIdResponse =
                    GsonConverters.deserializeList<LabDocumentIdResponse>(createResponse.files)

                labDocumentIdResponse!!.forEach { labTestResponse ->
                    labTestAndMedDao.updateDocumentFhirId(
                        labTestResponse.labDocumentUuid,
                        labTestResponse.labDocumentfhirId
                    )
                }
            } else {
                val medDocumentIdResponse =
                    GsonConverters.deserializeList<MedDocumentIdResponse>(createResponse.files)

                medDocumentIdResponse!!.forEach { medRecordResponse ->
                    labTestAndMedDao.updateDocumentFhirId(
                        medRecordResponse.medicalDocumentUuid,
                        medRecordResponse.medicalDocumentfhirId
                    )
                }
            }
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }


    protected suspend fun insertDispenseFhirId(
        listOfGenericEntities: List<GenericEntity>,
        body: List<CreateResponse>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            if (createResponse.error == null) {
                dispenseDao.updateDispenseFhirId(
                    createResponse.id!!, createResponse.fhirId!!
                )
                createResponse.medicineDispensedList!!.forEach { medicineResponse ->
                    dispenseDao.updateMedicineDispenseFhirId(
                        medicineResponse.medDispenseUuid,
                        medicineResponse.medDispenseFhirId
                    )
                }
            } else {
                idsToDelete.remove(createResponse.id)
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertDispense(body: List<MedicineDispenseResponse>) {
        dispenseDao.insertPrescriptionDispenseData(*body.map { medDispenseResponse ->
            medDispenseResponse.toDispensePrescriptionEntity(
                patientDao,
                prescriptionDao
            )
        }.toTypedArray())

        val dispenseRecords = mutableListOf<DispenseDataEntity>()
        body.forEach { medDispenseResponse ->
            dispenseRecords.addAll(
                medDispenseResponse.dispenseData.map { dispenseData ->
                    dispenseData.toListOfDispenseDataEntity(
                        patientDao,
                        prescriptionDao,
                        appointmentDao,
                        medDispenseResponse.prescriptionFhirId
                    )
                }
            )
        }
        dispenseDao.insertDispenseDataEntity(
            *dispenseRecords.toTypedArray()
        )

        val dispensedMedicationList = mutableListOf<MedicineDispenseListEntity>()
        body.forEach { medDispenseResponse ->
            medDispenseResponse.dispenseData.forEach { dispenseData ->
                dispensedMedicationList.addAll(
                    dispenseData.toListOfMedicineDispenseListEntity(
                        patientDao
                    )
                )
            }
        }
        dispenseDao.insertMedicineDispenseDataList(
            *dispensedMedicationList.toTypedArray()
        )
    }

    protected suspend fun insertNotDispensedPrescriptions() {
        dispenseDao.insertPrescriptionDispenseData(
            *prescriptionDao.getAllFormPrescriptions().filter {
                it.id !in dispenseDao.getAllDispense().map { it.prescriptionId }
            }.map { prescription ->
                DispensePrescriptionEntity(
                    patientId = prescription.patientId,
                    prescriptionId = prescription.id,
                    status = DispenseStatusEnum.NOT_DISPENSED.code
                )
            }.toTypedArray()
        )
    }

    protected suspend fun insertOTC(body: List<DispenseData>) {
        dispenseDao.insertDispenseDataEntity(
            *body.map {
                it.toListOfDispenseDataEntity(
                    patientDao,
                    prescriptionDao,
                    appointmentDao,
                    null
                )
            }
                .toTypedArray()
        )

        val dispensedMedicationList = mutableListOf<MedicineDispenseListEntity>()
        body.forEach { dispenseData ->
            dispensedMedicationList.addAll(
                dispenseData.toListOfMedicineDispenseListEntity(
                    patientDao
                )
            )
        }
        dispenseDao.insertMedicineDispenseDataList(
            *dispensedMedicationList.toTypedArray()
        )
    }

    protected suspend fun insertImmunizationRecommendation(body: List<ImmunizationRecommendationResponse>) {
        immunizationRecommendationDao.insertImmunizationRecommendation(
            *body.map { immunizationRecommendationResponse ->
                patientDao.getPatientIdByFhirId(immunizationRecommendationResponse.patientId)!!
                    .let {
                        immunizationRecommendationResponse.toImmunizationRecommendationEntity(it)
                    }
            }.toTypedArray()
        )
    }

    protected suspend fun insertImmunization(body: List<ImmunizationResponse>) {
        immunizationDao.insertImmunization(
            *body.map { immunizationResponse ->
                val patientId = patientDao.getPatientIdByFhirId(immunizationResponse.patientId)!!
                val appointmentId =
                    appointmentDao.getAppointmentIdByFhirId(immunizationResponse.appointmentId)
                immunizationResponse.toImmunizationEntity(patientId, appointmentId)
            }.toTypedArray()
        )

        body.map { immunizationResponse ->
            immunizationResponse.toImmunizationFileEntity()?.let { immunizationFiles ->
                immunizationDao.insertImmunizationFiles(*immunizationFiles.toTypedArray())
            }
        }

        val listOfGenericEntity = mutableListOf<GenericEntity>()
        body.map { immunizationResponse ->
            immunizationResponse.immunizationFiles?.forEach { file ->
                listOfGenericEntity.add(
                    GenericEntity(
                        id = UUID.randomUUID().toString(),
                        patientId = immunizationResponse.immunizationUuid,
                        payload = file.filename,
                        type = GenericTypeEnum.PRESCRIPTION_PHOTO,
                        syncType = SyncType.POST
                    )
                )
            }
        }
        genericDao.insertGenericEntity(
            *listOfGenericEntity.toTypedArray()
        )
    }

    protected suspend fun insertManufacturer(body: List<ManufacturerResponse>) {
        manufacturerDao.insertManufacturer(
            *body.map { it.toManufacturerEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertImmunizationFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        body.forEach { createResponse ->
            immunizationDao.updateFhirId(createResponse.id!!, createResponse.fhirId!!)
        }
        return deleteGenericEntityData(listOfGenericEntities)
    }

    protected suspend fun insertPriorDxFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    priorDxDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    priorDxDao.deletePriorDx(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertHistoryMedicationFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    historyMedicationDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    historyMedicationDao.deleteHistoryMedication(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertFamilyHistoryFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    familyHistoryDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    familyHistoryDao.deleteFamilyHistory(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertAllergyFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    allergyDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    allergyDao.deleteAllergy(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertRiskFactorsFhirIds(
        body: List<CreateResponse>,
        listOfGenericEntities: List<GenericEntity>
    ): Int {
        val idsToDelete = mutableSetOf<String>()
        idsToDelete.addAll(listOfGenericEntities.map { genericEntity -> genericEntity.id })
        body.forEach { createResponse ->
            when (createResponse.error) {
                null -> {
                    riskFactorDao.updateFhirId(
                        createResponse.id!!, createResponse.fhirId!!
                    )
                }
                DUPLICATE_RECORD -> {
                    riskFactorDao.deleteRiskFactor(createResponse.id!!)
                }
                else -> {
                    idsToDelete.remove(createResponse.id)
                }
            }
        }
        return deleteGenericEntityByListOfIds(idsToDelete.toList())
    }

    protected suspend fun insertLevels(body: List<LevelResponse>) {
        levelsDao.insertLevelEntity(
            *body.map { it.toLevelEntity() }.toTypedArray()
        )
    }

    protected suspend fun insertPriorDx(body: List<PriorDxResponse>) {
        priorDxDao.insertPriorDxRecord(
            *body.map { it.toPriorDxEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertHistoryMedication(body: List<HistoryMedicationResponse>) {
        historyMedicationDao.insertHistoryMedicationRecord(
            *body.map { it.toHistoryMedicationEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertFamilyHistory(body: List<FamilyHistoryResponse>) {
        familyHistoryDao.insertFamilyHistoryRecord(
            *body.map { it.toFamilyHistoryEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertAllergy(body: List<AllergyResponse>) {
        allergyDao.insertAllergyRecord(
            *body.map { it.toAllergyEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }

    protected suspend fun insertRiskFactors(body: List<RiskFactorResponse>) {
        riskFactorDao.insertRiskFactorRecord(
            *body.map { it.toRiskFactorEntity(patientDao, appointmentDao) }.toTypedArray()
        )
    }
}