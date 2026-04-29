package com.heartcare.agni.utils.converters.responseconverter

import com.heartcare.agni.data.local.enums.IdentifierIgnoreEnum
import com.heartcare.agni.data.local.enums.PrescriptionType
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.model.InterventionItem
import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.local.model.examination.ExaminationItem
import com.heartcare.agni.data.local.model.examination.ExaminationResponseLocal
import com.heartcare.agni.data.local.model.prescription.MedicationLocal
import com.heartcare.agni.data.local.model.prescription.PrescriptionResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.AppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignAppointmentDao
import com.heartcare.agni.data.local.roomdb.dao.CampaignScheduleDao
import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.local.roomdb.dao.InterventionDao
import com.heartcare.agni.data.local.roomdb.dao.MedicationDao
import com.heartcare.agni.data.local.roomdb.dao.PatientDao
import com.heartcare.agni.data.local.roomdb.dao.RiskPredictionDao
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.local.roomdb.entities.allergy.AllergyEntity
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignScheduleEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.StaffEntity
import com.heartcare.agni.data.local.roomdb.entities.cvd.CVDEntity
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.data.server.model.campaign.StaffResponse
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisEntity
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal
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
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PermanentAddressEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.priordx.PriorDxEntity
import com.heartcare.agni.data.local.roomdb.entities.referral.ReferralEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.AlcoholEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.FatAndOilEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.FruitsVegetablesEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.MealsOutsideHomeEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.PhysicalActivityEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.RiskFactorEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.SaltEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.SugarEntity
import com.heartcare.agni.data.local.roomdb.entities.risk.TobaccoEntity
import com.heartcare.agni.data.local.roomdb.entities.schedule.ScheduleEntity
import com.heartcare.agni.data.local.roomdb.entities.tobacco.TobaccoCessationEntity
import com.heartcare.agni.data.local.roomdb.entities.vitals.BloodGlucoseMeasurement
import com.heartcare.agni.data.local.roomdb.entities.vitals.Measurement
import com.heartcare.agni.data.local.roomdb.entities.vitals.VitalEntity
import com.heartcare.agni.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisResponse
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.GPSCoordinates
import com.heartcare.agni.data.server.model.patient.GeneralPractitioner
import com.heartcare.agni.data.server.model.patient.ManagingOrganization
import com.heartcare.agni.data.server.model.patient.PatientAddressResponse
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicineTimeResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.data.server.model.risk.AlcoholResponse
import com.heartcare.agni.data.server.model.risk.FatAndOilResponse
import com.heartcare.agni.data.server.model.risk.FruitsVegetablesResponse
import com.heartcare.agni.data.server.model.risk.MealsOutsideHomeResponse
import com.heartcare.agni.data.server.model.risk.PhysicalActivityResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.risk.SaltResponse
import com.heartcare.agni.data.server.model.risk.SugarResponse
import com.heartcare.agni.data.server.model.risk.TobaccoResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.UnitValue
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPatientDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.util.Date
import java.util.Locale

fun PatientResponse.toPatientEntity(): PatientEntity {
    return PatientEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        birthDate = birthDate.toTimeInMilli(),
        mobileNumber = mobileNumber?.toLong(),
        permanentAddress = permanentAddress.toPermanentAddressEntity(),
        fhirId = fhirId,
        mothersName = mothersName,
        fathersName = fathersName,
        spouseName = spouseName,
        generalPractitioner = generalPractitioner?.get(0)?.reference,
        isDeleted = isDeleted,
        managingOrganization = managingOrganization?.reference,
        patientDeceasedReason = patientDeceasedReason,
        patientDeceasedReasonId = patientDeceasedReasonId,
        active = active,
        heartcareId = heartcareId,
        email = email,
        latitude = gpsCoordinates?.latitude,
        longitude = gpsCoordinates?.longitude
    )
}

fun PatientAddressResponse.toPermanentAddressEntity(): PermanentAddressEntity {
    return PermanentAddressEntity(
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2,
        village = village,
        island = island,
        province = province,
        areaCouncil = areaCouncil
    )
}

fun PatientIdentifier.toIdentifierEntity(patientId: String): IdentifierEntity {
    return IdentifierEntity(
        identifierNumber = identifierNumber,
        identifierType = identifierType,
        identifierCode = code,
        patientId = patientId,
        identifierUse = use
    )
}

fun PatientResponse.toListOfIdentifierEntity(): List<IdentifierEntity> {
    return this.identifier
        .filter { it.code != IdentifierIgnoreEnum.MEDICAL_RECORD.value && !it.identifierType.contains(IdentifierIgnoreEnum.HEARTCARE_TYPE.value) }
        .map {
            it.toIdentifierEntity(this.id)
        }
}

fun PatientAndIdentifierEntity.toPatientResponse(): PatientResponse {
    return PatientResponse(
        id = patientEntity.id,
        firstName = patientEntity.firstName,
        lastName = patientEntity.lastName,
        identifier = identifiers.map { it.toPatientIdentifier() },
        gender = patientEntity.gender,
        birthDate = Date(patientEntity.birthDate).time.toPatientDate(),
        mobileNumber = patientEntity.mobileNumber?.toString(),
        permanentAddress = patientEntity.permanentAddress.toPatientAddressResponse(),
        fhirId = patientEntity.fhirId,
        mothersName = patientEntity.mothersName,
        fathersName = patientEntity.fathersName,
        spouseName = patientEntity.spouseName,
        isDeleted = patientEntity.isDeleted,
        managingOrganization = patientEntity.managingOrganization?.let {
            ManagingOrganization(
                reference = it
            )
        },
        generalPractitioner = patientEntity.generalPractitioner?.let {
            listOf(
                GeneralPractitioner(
                    reference = it
                )
            )
        },
        patientDeceasedReason = patientEntity.patientDeceasedReason,
        patientDeceasedReasonId = patientEntity.patientDeceasedReasonId,
        appUpdatedDate = null,
        active = patientEntity.active,
        heartcareId = patientEntity.heartcareId,
        email = patientEntity.email,
        gpsCoordinates = GPSCoordinates(
            longitude = patientEntity.longitude,
            latitude = patientEntity.latitude
        )
    )
}

fun IdentifierEntity.toPatientIdentifier(): PatientIdentifier {
    return PatientIdentifier(
        identifierType = identifierType,
        identifierNumber = identifierNumber,
        code = identifierCode,
        use = identifierUse
    )
}

fun PermanentAddressEntity.toPatientAddressResponse(): PatientAddressResponse {
    return PatientAddressResponse(
        postalCode = postalCode,
        country = country,
        addressLine2 = addressLine2,
        village = village,
        province = province,
        areaCouncil = areaCouncil,
        island = island
    )
}

fun List<GenericEntity>.toListOfId(): List<String> {
    return this.map { it.id }
}

internal fun <T> List<T>.toNoBracketAndNoSpaceString(): String {
    return this.toString().replace("[", "").replace("]", "").replace(" ", "")
}

internal suspend fun PrescriptionResponse.toPrescriptionEntity(
    patientDao: PatientDao,
): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId!!,
        prescriptionDate = generatedOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        appointmentId = appointmentUuid!!,
        patientFhirId = patientFhirId,
        prescriptionFhirId = prescriptionFhirId,
        prescriptionType = PrescriptionType.FORM.type
    )
}

internal fun PrescriptionResponseLocal.toPrescriptionEntity(): PrescriptionEntity {
    return PrescriptionEntity(
        id = prescriptionId,
        prescriptionDate = generatedOn,
        patientId = patientId,
        appointmentId = appointmentId,
        patientFhirId = patientFhirId,
        prescriptionFhirId = prescriptionFhirId,
        prescriptionType = PrescriptionType.FORM.type
    )
}

internal suspend fun PrescriptionResponse.toListOfPrescriptionDirectionsEntity(medicationDao: MedicationDao): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medReqUuid,
            medFhirId = medication.medFhirId,
            qtyPerDose = medication.qtyPerDose,
            frequency = medication.frequency,
            timing = medication.timing?.let { timing ->
                medicationDao.getMedicalDosageByMedicalDosageId(
                    timing
                )
            },
            duration = medication.duration,
            qtyPrescribed = medication.qtyPrescribed,
            note = medication.note,
            prescriptionId = prescriptionId!!,
            medReqFhirId = medication.medReqFhirId,
            brandName = medication.brandName,
            doseFormCode = medication.doseFormCode,
            doseForm = medication.doseForm
        )
    }
}

internal fun PrescriptionResponseLocal.toListOfPrescriptionDirectionsEntity(): List<PrescriptionDirectionsEntity> {
    return prescription.map { medication ->
        PrescriptionDirectionsEntity(
            id = medication.medReqUuid,
            medFhirId = medication.medFhirId,
            qtyPerDose = medication.qtyPerDose,
            frequency = medication.frequency,
            timing = medication.timing,
            duration = medication.duration,
            qtyPrescribed = medication.qtyPrescribed,
            note = medication.note,
            prescriptionId = prescriptionId,
            medReqFhirId = medication.medReqFhirId,
            brandName = medication.brandName,
            doseFormCode = medication.doseFormCode,
            doseForm = medication.doseForm
        )
    }
}

internal fun List<MedicineTimeResponse>.toListOfMedicineDirectionsEntity(): List<MedicineTimingEntity> {
    return map { medicineTimeResponse ->
        MedicineTimingEntity(
            medicalDosage = medicineTimeResponse.medInstructionVal,
            medicalDosageId = medicineTimeResponse.medInstructionCode
        )
    }
}

internal fun ScheduleResponse.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = uuid,
        scheduleFhirId = scheduleId,
        startTime = planningHorizon.start,
        endTime = planningHorizon.end,
        bookedSlots = bookedSlots!!,
        roleId = roleId!!,
        active = active!!,
        practitionerId = practitionerId!!,
        hospitalId = hospitalId,
        hospitalFhirId = hospitalFhirId,
        hospitalName = hospitalName!!,
        hospitalCode = hospitalCode!!
    )
}

internal fun ScheduleResponse.toCampaignScheduleEntity(): CampaignScheduleEntity {
    return CampaignScheduleEntity(
        id = uuid,
        scheduleFhirId = scheduleId,
        startTime = planningHorizon.start,
        endTime = planningHorizon.end,
        bookedSlots = bookedSlots ?: 0,
        roleId = roleId,
        active = active ?: true,
        practitionerId = practitionerId,
        hospitalId = hospitalId,
        hospitalFhirId = hospitalFhirId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = campaignId!!
    )
}

internal fun ScheduleEntity.toScheduleResponse(): ScheduleResponse {
    return ScheduleResponse(
        uuid = id,
        scheduleId = scheduleFhirId,
        bookedSlots = bookedSlots,
        planningHorizon = Slot(
            start = startTime,
            end = endTime
        ),
        roleId = roleId,
        active = active,
        practitionerId = practitionerId,
        hospitalId = hospitalId,
        hospitalFhirId = hospitalFhirId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode
    )
}

internal fun CampaignScheduleEntity.toScheduleResponse(): ScheduleResponse {
    return ScheduleResponse(
        uuid = id,
        scheduleId = scheduleFhirId,
        bookedSlots = bookedSlots,
        planningHorizon = Slot(
            start = startTime,
            end = endTime
        ),
        roleId = roleId,
        active = active,
        practitionerId = practitionerId,
        hospitalId = hospitalId,
        hospitalFhirId = hospitalFhirId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = campaignId
    )
}

// Appointment Response from Server
internal suspend fun AppointmentResponse.toAppointmentEntity(
    patientDao: PatientDao,
    scheduleDao: ScheduleDao
): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        scheduleId = scheduleDao.getScheduleStartTimeByFhirId(scheduleId)!!,
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode!!
    )
}

internal suspend fun AppointmentResponse.toCampaignAppointmentEntity(
    patientDao: PatientDao,
    campaignScheduleDao: CampaignScheduleDao
): CampaignAppointmentEntity {
    return CampaignAppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientDao.getPatientIdByFhirId(patientFhirId)!!,
        scheduleId = campaignScheduleDao.getScheduleStartTimeByFhirId(scheduleId) ?: Date(),
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = campaignId!!
    )
}

internal suspend fun AppointmentEntity.toAppointmentResponse(
    scheduleDao: ScheduleDao,
    hospitalCode: String
): AppointmentResponse {
    return AppointmentResponse(
        uuid = id,
        createdOn = createdOn,
        appointmentId = appointmentFhirId,
        patientFhirId = patientId,
        scheduleId = scheduleDao.getFhirIdByStartTime(scheduleId, hospitalCode)
            ?: scheduleDao.getScheduleByStartTime(scheduleId.time, hospitalCode)!!.id,
        slot = Slot(
            start = startTime,
            end = endTime
        ),
        status = status,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = null,
        slotId = null,
        practitionerId = null,
        hospitalFhirId = null,
        hospitalId = null,
        hospitalName = null,
        hospitalCode = null,
        campaignId = null,
        appUpdatedDate = Date()
    )
}

internal fun AppointmentEntity.toAppointmentResponseLocal(): AppointmentResponseLocal {
    return AppointmentResponseLocal(
        uuid = id,
        createdOn = createdOn,
        appointmentId = appointmentFhirId,
        patientId = patientId,
        scheduleId = scheduleId,
        slot = Slot(
            start = startTime,
            end = endTime
        ),
        status = status,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = null,
        recordType = RecordType.FACILITY
    )
}

internal fun CampaignAppointmentEntity.toAppointmentResponseLocal(): AppointmentResponseLocal {
    return AppointmentResponseLocal(
        uuid = id,
        createdOn = createdOn,
        appointmentId = appointmentFhirId,
        patientId = patientId,
        scheduleId = scheduleId,
        slot = Slot(
            start = startTime,
            end = endTime
        ),
        status = status,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = campaignId,
        recordType = RecordType.SCREENING_SITE
    )
}

internal fun AppointmentResponseLocal.toCampaignAppointmentEntity(): CampaignAppointmentEntity {
    return CampaignAppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientId,
        scheduleId = scheduleId,
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode,
        campaignId = campaignId ?: ""
    )
}

// Appointment Response from Local
internal fun AppointmentResponseLocal.toAppointmentEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = uuid,
        appointmentFhirId = appointmentId,
        createdOn = createdOn,
        patientId = patientId,
        scheduleId = scheduleId,
        status = status,
        startTime = slot.start,
        endTime = slot.end,
        appointmentType = appointmentType,
        inProgressTime = inProgressTime,
        roleId = roleId,
        slotId = slotId,
        practitionerId = practitionerId,
        hospitalFhirId = hospitalFhirId,
        hospitalId = hospitalId,
        hospitalName = hospitalName,
        hospitalCode = hospitalCode!!
    )
}

internal fun PrescriptionAndMedicineRelation.toPrescriptionResponseLocal(): PrescriptionResponseLocal {
    return PrescriptionResponseLocal(
        patientId = prescriptionEntity.patientId,
        patientFhirId = prescriptionEntity.patientFhirId,
        appointmentId = prescriptionEntity.appointmentId,
        generatedOn = prescriptionEntity.prescriptionDate,
        prescriptionId = prescriptionEntity.id,
        prescription = prescriptionDirectionAndMedicineView.map { prescriptionDirectionAndMedicineView -> prescriptionDirectionAndMedicineView.toMedicationLocal() },
        prescriptionFhirId = prescriptionEntity.prescriptionFhirId
    )
}

internal fun PrescriptionDirectionAndMedicineView.toMedicationLocal(): MedicationLocal {
    return MedicationLocal(
        doseForm = medicationEntity.doseForm,
        duration = prescriptionDirectionsEntity.duration,
        frequency = prescriptionDirectionsEntity.frequency,
        medFhirId = medicationEntity.medFhirId,
        note = prescriptionDirectionsEntity.note,
        qtyPerDose = prescriptionDirectionsEntity.qtyPerDose,
        qtyPrescribed = prescriptionDirectionsEntity.qtyPrescribed,
        timing = prescriptionDirectionsEntity.timing,
        medReqFhirId = prescriptionDirectionsEntity.medReqFhirId,
        medReqUuid = prescriptionDirectionsEntity.id,
        medName = medicationEntity.medName,
        medUnit = medicationEntity.medUnit,
        brandName = prescriptionDirectionsEntity.brandName,
        doseFormCode = prescriptionDirectionsEntity.doseFormCode
    )
}

internal fun PatientLastUpdatedResponse.toPatientLastUpdatedEntity(): PatientLastUpdatedEntity {
    return PatientLastUpdatedEntity(
        patientId = uuid,
        lastUpdated = timestamp
    )
}

internal fun CVDResponse.toCVDEntity(): CVDEntity {
    return CVDEntity(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentId else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) appointmentId else null,
        patientId = patientId,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = risk,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight,
        weightUnit = weightUnit,
        chiefComplaint = chiefComplaint,
        screeningDate = screeningDate,
        heartAttackHistory = heartAttackHistory,
        campaignId = campaignId
    )
}


internal suspend fun CVDResponse.toCVDEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao,
    riskPredictionDao: RiskPredictionDao
): CVDEntity {
    val patient =
        patientDao.getPatientDataById(patientDao.getPatientIdByFhirId(patientId)!!)[0].patientEntity
    val cholesterolUnits = listOf("mmol/L", "mg/dl")
    var cholesterolInMMHG: Double? = null
    if (cholesterol != null) {
        cholesterolInMMHG =
            if (cholesterolUnits.indexOf(cholesterolUnit) == 1) String.format(
                Locale.getDefault(),
                "%.1f",
                cholesterol * 0.0259
            ).toDouble()
            else cholesterol
    }
    val riskPercent = riskPredictionDao.predictRisk(
        patient.gender[0].uppercaseChar().toString(),
        smoker,
        patient.birthDate.toAge(),
        bpSystolic,
        cholesterolInMMHG,
        bmi,
        "oc",
        diabetic
    ).toInt()
    return CVDEntity(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = riskPercent,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight,
        weightUnit = weightUnit,
        chiefComplaint = chiefComplaint,
        screeningDate = screeningDate,
        heartAttackHistory = heartAttackHistory,
        campaignId = campaignId
    )
}

internal fun CVDEntity.toCVDResponse(): CVDResponse {
    return CVDResponse(
        cvdFhirId = cvdFhirId,
        cvdUuid = cvdUuid,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentId!! else campaignAppointmentId!!,
        patientId = patientId,
        bmi = bmi,
        bpDiastolic = bpDiastolic,
        bpSystolic = bpSystolic,
        cholesterol = cholesterol,
        cholesterolUnit = cholesterolUnit,
        diabetic = diabetic,
        heightCm = heightCm,
        createdOn = createdOn,
        heightInch = heightInch,
        heightFt = heightFt,
        risk = risk,
        practitionerName = practitionerName,
        smoker = smoker,
        weight = weight,
        appUpdatedDate = null,
        weightUnit = weightUnit,
        chiefComplaint = chiefComplaint,
        screeningDate = screeningDate,
        heartAttackHistory = heartAttackHistory,
        campaignId = campaignId
    )
}

internal fun VitalEntity.toVitalResponse(): VitalResponse {
    return VitalResponse(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentId!! else campaignAppointmentId!!,
        campaignId = campaignId,
        bloodGlucose = bloodGlucose?.run {
            UnitValue(
                unit = unit!!,
                value = value!!,
                type = type
            )
        },
        appUpdatedDate = appUpdatedDate,
        practitionerName = practitionerName,
        footExamination = footExamination,
        eyeExamination = eyeExamination,
        abdominalCircumference = abdominalCircumference?.run {
            UnitValue(
                unit = unit!!,
                value = value!!,
                type = null,
            )
        },
        hipCircumference = hipCircumference?.run {
            UnitValue(
                unit = unit!!,
                value = value!!,
                type = null,
            )
        },
        hbA1cPercentage = hbA1cPercentage,
        serumCreatinine = serumCreatinine?.run {
            UnitValue(
                unit = unit!!,
                value = value!!,
                type = null,
            )
        },
        serumPotassium = serumPotassium?.run {
            UnitValue(
                unit = unit!!,
                value = value!!,
                type = null,
            )
        },
        urineProtein = urineProtein,
        urineKetones = urineKetones,
        others = others
    )
}

internal fun VitalResponse.toVitalEntity(): VitalEntity {
    return VitalEntity(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentId else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) appointmentId else null,
        campaignId = campaignId,
        appUpdatedDate = appUpdatedDate,
        practitionerName = practitionerName,
        bloodGlucose = bloodGlucose?.run {
            BloodGlucoseMeasurement(
                value = value,
                unit = unit,
                type = type,
            )
        },
        footExamination = footExamination,
        eyeExamination = eyeExamination,
        abdominalCircumference = abdominalCircumference?.run {
            Measurement(
                value = value,
                unit = unit
            )
        },
        hipCircumference = hipCircumference?.run {
            Measurement(
                value = value,
                unit = unit
            )
        },
        hbA1cPercentage = hbA1cPercentage,
        serumCreatinine = serumCreatinine?.run {
            Measurement(
                value = value,
                unit = unit
            )
        },
        serumPotassium = serumPotassium?.run {
            Measurement(
                value = value,
                unit = unit
            )
        },
        urineProtein = urineProtein,
        urineKetones = urineKetones,
        others = others,
    )
}

internal suspend fun VitalResponse.toVitalEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao
): VitalEntity {
    return this.toVitalEntity().copy(
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        campaignId = campaignId
    )
}

internal fun DiagnosisMasterResponse.toDiagnosisMasterEntity(): DiagnosisMasterEntity {
    return DiagnosisMasterEntity(
        id = diagnosisId,
        code = code,
        display = display
    )
}

internal fun DiagnosisLocal.toDiagnosisEntity(): DiagnosisEntity {
    return DiagnosisEntity(
        diagnosisUuid = diagnosisUuid,
        appointmentId = appointmentId, fhirId = diagnosisFhirId,
        createdOn = createdOn,
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName,
        patientId = patientId,
        progressNote = progressNote
    )
}

internal fun DiagnosisEntity.toDiagnosisLocal(): DiagnosisLocal {
    return DiagnosisLocal(
        diagnosisUuid = diagnosisUuid,
        appointmentId = appointmentId, diagnosisFhirId = fhirId,
        createdOn = createdOn,
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName,
        patientId = patientId,
        progressNote = progressNote
    )
}


internal suspend fun DiagnosisResponse.toDiagnosisEntity(
    studentDao: PatientDao,
    appointmentDao: AppointmentDao
): DiagnosisEntity {
    return DiagnosisEntity(
        diagnosisUuid = diagnosisUuid,
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        fhirId = diagnosisFhirId,
        createdOn = createdOn,
        diagnosis = diagnosis,
        symptoms = symptoms,
        practitionerName = practitionerName,
        patientId = studentDao.getPatientIdByFhirId(patientId)!!,
        progressNote = progressNote
    )
}

internal fun DiagnosisLocal.toDiagnosisData(): DiagnosisData {
    return DiagnosisData(
        diagnosisUuid = diagnosisUuid,
        appointmentId = appointmentId,
        createdOn = createdOn,
        diagnosis = diagnosis.map { it.code },
        symptoms = symptoms.map { it.code }.ifEmpty { null },
        patientId = patientId
    )
}

internal fun List<MedicationResponse>.toListOfMedicationEntity(): List<MedicationEntity> {
    return this.map { medication ->
        MedicationEntity(
            medFhirId = medication.medFhirId,
            medCodeName = medication.code,
            medName = medication.name,
            doseForm = medication.doseForm,
            doseFormCode = medication.doseFormCode,
            activeIngredient = medication.activeIngredient,
            activeIngredientCode = medication.activeIngredientCode,
            medUnit = medication.medUnit,
            medNumeratorVal = medication.medNumeratorVal,
            isOTC = medication.isOTC,
            status = medication.status,
            classId = medication.classId,
            className = medication.className,
            categoryId = medication.categoryId,
            categoryName = medication.categoryName,
            brandList = medication.brandList,
        )
    }
}

internal fun MedicationEntity.toMedicationResponse(): MedicationResponse {
    return MedicationResponse(
        medFhirId = medFhirId,
        code = medCodeName,
        name = medName,
        isOTC = isOTC,
        doseForm = doseForm,
        doseFormCode = doseFormCode,
        status = status,
        activeIngredient = activeIngredient,
        activeIngredientCode = activeIngredientCode,
        medUnit = medUnit,
        medNumeratorVal = medNumeratorVal,
        classId = classId,
        className = className,
        categoryId = categoryId,
        categoryName = categoryName,
        brandList = brandList
    )
}

internal fun LevelResponse.toLevelEntity(): LevelEntity {
    return LevelEntity(
        fhirId = fhirId,
        code = code,
        levelType = levelType,
        name = name,
        population = population,
        precedingLevelId = precedingLevelId,
        secondaryName = secondaryName,
        status = status
    )
}

internal fun LevelEntity.toLevelResponse(): LevelResponse {
    return LevelResponse(
        fhirId = fhirId,
        code = code,
        levelType = levelType,
        name = name,
        population = population,
        precedingLevelId = precedingLevelId,
        secondaryName = secondaryName,
        status = status
    )
}

internal fun PriorDxResponse.toPriorDxEntity(): PriorDxEntity {
    return PriorDxEntity(
        priorDxUuid = priorDxUuid,
        priorDxFhirId = priorDxFhirId,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentId else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) appointmentId else null,
        campaignId = campaignId,
        cancer = cancer,
        createdOn = createdOn!!,
        hasAids = hasAids,
        hasAsthma = hasAsthma,
        hasCancer = hasCancer,
        hasChronicKidneyDiseases = hasChronicKidneyDiseases,
        hasChronicObstructivePulmonaryDisease = hasChronicObstructivePulmonaryDisease,
        hasCovid = hasCovid,
        hasDiabetes = hasDiabetes,
        hasHeartDiseases = hasHeartDiseases,
        hasHypercholesterolaemia = hasHypercholesterolaemia,
        hasHypertension = hasHypertension,
        hasOthers = hasOthers,
        hasTransientIschaemicAttack = hasTransientIschaemicAttack,
        hasTuberculosis = hasTuberculosis,
        others = others,
        patientId = patientId,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!
    )
}

suspend fun PriorDxResponse.toPriorDxEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao
): PriorDxEntity {
    return PriorDxEntity(
        priorDxUuid = priorDxUuid,
        priorDxFhirId = priorDxFhirId,
        appointmentId = if (campaignId.isNullOrEmpty()) appointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        campaignAppointmentId = if (!campaignId.isNullOrEmpty()) campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId) else null,
        campaignId = campaignId,
        cancer = cancer,
        createdOn = createdOn!!,
        hasAids = hasAids,
        hasAsthma = hasAsthma,
        hasCancer = hasCancer,
        hasChronicKidneyDiseases = hasChronicKidneyDiseases,
        hasChronicObstructivePulmonaryDisease = hasChronicObstructivePulmonaryDisease,
        hasCovid = hasCovid,
        hasDiabetes = hasDiabetes,
        hasHeartDiseases = hasHeartDiseases,
        hasHypercholesterolaemia = hasHypercholesterolaemia,
        hasHypertension = hasHypertension,
        hasOthers = hasOthers,
        hasTransientIschaemicAttack = hasTransientIschaemicAttack,
        hasTuberculosis = hasTuberculosis,
        others = others,
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!
    )
}

internal fun PriorDxEntity.toPriorDxResponse(): PriorDxResponse {
    return PriorDxResponse(
        priorDxUuid = priorDxUuid,
        priorDxFhirId = priorDxFhirId,
        appointmentId = (appointmentId ?: campaignAppointmentId)!!,
        campaignId = campaignId,
        cancer = cancer,
        createdOn = createdOn,
        hasAids = hasAids,
        hasAsthma = hasAsthma,
        hasCancer = hasCancer,
        hasChronicKidneyDiseases = hasChronicKidneyDiseases,
        hasChronicObstructivePulmonaryDisease = hasChronicObstructivePulmonaryDisease,
        hasCovid = hasCovid,
        hasDiabetes = hasDiabetes,
        hasHeartDiseases = hasHeartDiseases,
        hasHypercholesterolaemia = hasHypercholesterolaemia,
        hasHypertension = hasHypertension,
        hasOthers = hasOthers,
        hasTransientIschaemicAttack = hasTransientIschaemicAttack,
        hasTuberculosis = hasTuberculosis,
        others = others,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        appUpdatedDate = null
    )
}

internal fun HistoryMedicationResponse.toHistoryMedicationEntity(): HistoryMedicationEntity {
    return HistoryMedicationEntity(
        uuid = uuid,
        fhirId = fhirId,
        adherence = adherence,
        appUpdatedDate = appUpdatedDate,
        appointmentId = if (campaignId == null) appointmentId else null,
        campaignAppointmentId = if (campaignId != null) appointmentId else null,
        hasSideEffect = hasSideEffect,
        medicinePrescribed = medicinePrescribed,
        medicinePrescribedOthers = medicinePrescribedOthers,
        patientId = patientId,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        sideEffects = sideEffects,
        campaignId= campaignId
    )
}

suspend fun HistoryMedicationResponse.toHistoryMedicationEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao
): HistoryMedicationEntity {
    val localAppointmentId = if (campaignId != null) {
        campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId)
    } else {
        appointmentDao.getAppointmentIdByFhirId(appointmentId)
    }

    return HistoryMedicationEntity(
        uuid = uuid,
        fhirId = fhirId,
        adherence = adherence,
        appUpdatedDate = appUpdatedDate,
        appointmentId = if (campaignId == null) localAppointmentId else null,
        campaignAppointmentId = if (campaignId != null) localAppointmentId else null,
        hasSideEffect = hasSideEffect,
        medicinePrescribed = medicinePrescribed,
        medicinePrescribedOthers = medicinePrescribedOthers,
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        sideEffects = sideEffects,
        campaignId = campaignId
    )
}

internal fun HistoryMedicationEntity.toHistoryMedicationResponse(): HistoryMedicationResponse {
    return HistoryMedicationResponse(
        uuid = uuid,
        fhirId = fhirId,
        adherence = adherence,
        appUpdatedDate = appUpdatedDate,
        appointmentId = (campaignAppointmentId ?: appointmentId)!!,
        hasSideEffect = hasSideEffect,
        medicinePrescribed = medicinePrescribed,
        medicinePrescribedOthers = medicinePrescribedOthers,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        sideEffects = sideEffects,
        campaignId = campaignId
    )
}

internal fun FamilyHistoryResponse.toFamilyHistoryEntity(): FamilyHistoryEntity {
    return FamilyHistoryEntity(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = if (campaignId == null) appointmentId else null,
        campaignAppointmentId = if (campaignId != null) appointmentId else null,
        campaignId = campaignId,
        appUpdatedDate = appUpdatedDate,
        familyDiseases = familyDiseases,
        occurrenceAgeData = occurrenceAgeData,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!
    )
}

suspend fun FamilyHistoryResponse.toFamilyHistoryEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao
): FamilyHistoryEntity {
    val localAppointmentId = if (campaignId != null) {
        campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId)
    } else {
        appointmentDao.getAppointmentIdByFhirId(appointmentId)
    }
    return FamilyHistoryEntity(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = if (campaignId == null) localAppointmentId else null,
        campaignAppointmentId = if (campaignId != null) localAppointmentId else null,
        campaignId = campaignId,
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        familyDiseases = familyDiseases,
        occurrenceAgeData = occurrenceAgeData
    )
}

internal fun FamilyHistoryEntity.toFamilyHistoryResponse(): FamilyHistoryResponse {
    return FamilyHistoryResponse(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = (campaignAppointmentId ?: appointmentId)!!,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        familyDiseases = familyDiseases,
        occurrenceAgeData = occurrenceAgeData,
        campaignId = campaignId
    )
}

internal fun AllergyResponse.toAllergyEntity(): AllergyEntity {
    return AllergyEntity(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        campaignId = campaignId,
        appointmentId = if (campaignId == null) appointmentId else null,
        campaignAppointmentId = if (campaignId != null) appointmentId else null,
        appUpdatedDate = appUpdatedDate,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        allergy = allergy
    )
}

suspend fun AllergyResponse.toAllergyEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao,
    campaignAppointmentDao: CampaignAppointmentDao
): AllergyEntity {
    val localAppointmentId = if (campaignId != null) {
        campaignAppointmentDao.getAppointmentIdByFhirId(appointmentId)
    } else {
        appointmentDao.getAppointmentIdByFhirId(appointmentId)
    }
    return AllergyEntity(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = if (campaignId == null) localAppointmentId else null,
        campaignAppointmentId = if (campaignId != null) localAppointmentId else null,
        campaignId = campaignId,
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        allergy = allergy,
    )
}

internal fun AllergyEntity.toAllergyResponse(): AllergyResponse {
    return AllergyResponse(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = (campaignAppointmentId ?: appointmentId)!!,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        allergy = allergy,
        campaignId = campaignId
    )
}

internal fun RiskFactorResponse.toRiskFactorEntity(): RiskFactorEntity {
    return RiskFactorEntity(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = appointmentId,
        appUpdatedDate = appUpdatedDate,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        tobacco = tobacco?.run {
            TobaccoEntity(
                tobaccoUser = tobaccoUser,
                tobaccoItemType = tobaccoItemType,
                tobaccoOther = tobaccoOther,
                consumptionAmount = consumptionAmount,
                consumptionUnit = consumptionUnit,
                startAge = startAge,
                willingToQuit = willingToQuit
            )
        },
        alcohol = alcohol?.run {
            AlcoholEntity(
                consumedWithin30Days = consumedWithin30Days,
                alcoholQ1 = alcoholQ1,
                alcoholQ2 = alcoholQ2,
                alcoholQ3 = alcoholQ3
            )
        },
        fruitsVegetables = fruitsVegetables?.run {
            FruitsVegetablesEntity(
                consumptionInWeek = consumptionInWeek,
                fruitsDays = fruitsDays,
                fruitServings = fruitServings,
                vegetableDays = vegetableDays,
                vegetableServings = vegetableServings
            )
        },
        physicalActivity = physicalActivity?.run {
            PhysicalActivityEntity(
                weeklyEngagement = weeklyEngagement,
                vigorousDays = vigorousDays,
                vigorousTime = vigorousTime,
                moderateDays = moderateDays,
                moderateTime = moderateTime
            )
        },
        salt = salt?.run {
            SaltEntity(
                saltAmount = saltAmount,
                saltAddMeal = saltAddMeal,
                saltAddCooking = saltAddCooking,
                saltProcessedFood = saltProcessedFood
            )
        },
        fatAndOil = fatAndOil?.run {
            FatAndOilEntity(
                oilUsed = oilUsed,
                fatFoodFrequency = fatFoodFrequency,
                otherFatAndOils = otherFatAndOils
            )
        },
        sugar = sugar?.run {
            SugarEntity(
                softDrinkFrequency = softDrinkFrequency,
                juiceFrequency = juiceFrequency
            )
        },
        mealsOutsideHome = mealsOutsideHome?.run {
            MealsOutsideHomeEntity(
                eatsOut = eatsOut,
                mealsPerWeek = mealsPerWeek
            )
        }
    )
}

suspend fun RiskFactorResponse.toRiskFactorEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): RiskFactorEntity {
    return this.toRiskFactorEntity().copy(
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
    )
}

internal fun RiskFactorEntity.toRiskFactorResponse(): RiskFactorResponse {
    return RiskFactorResponse(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        tobacco = tobacco?.run {
            if (tobaccoUser == null) null
            else TobaccoResponse(
                tobaccoUser = tobaccoUser,
                tobaccoItemType = tobaccoItemType,
                tobaccoOther = tobaccoOther,
                consumptionAmount = consumptionAmount,
                consumptionUnit = consumptionUnit,
                startAge = startAge,
                willingToQuit = willingToQuit
            )
        },
        alcohol = alcohol?.run {
            if (consumedWithin30Days == null) null
            else AlcoholResponse(
                consumedWithin30Days = consumedWithin30Days,
                alcoholQ1 = alcoholQ1,
                alcoholQ2 = alcoholQ2,
                alcoholQ3 = alcoholQ3
            )
        },
        fruitsVegetables = fruitsVegetables?.run {
            if (consumptionInWeek == null) null
            else FruitsVegetablesResponse(
                consumptionInWeek = consumptionInWeek,
                fruitsDays = fruitsDays,
                fruitServings = fruitServings,
                vegetableDays = vegetableDays,
                vegetableServings = vegetableServings
            )
        },
        physicalActivity = physicalActivity?.run {
            if (weeklyEngagement == null) null
            else PhysicalActivityResponse(
                weeklyEngagement = weeklyEngagement,
                vigorousDays = vigorousDays,
                vigorousTime = vigorousTime,
                moderateDays = moderateDays,
                moderateTime = moderateTime
            )
        },
        salt = salt?.run {
            SaltResponse(
                saltAmount = saltAmount,
                saltAddMeal = saltAddMeal,
                saltAddCooking = saltAddCooking,
                saltProcessedFood = saltProcessedFood
            )
        },
        fatAndOil = fatAndOil?.run {
            FatAndOilResponse(
                oilUsed = oilUsed,
                fatFoodFrequency = fatFoodFrequency,
                otherFatAndOils = otherFatAndOils
            )
        },
        sugar = sugar?.run {
            SugarResponse(
                softDrinkFrequency = softDrinkFrequency,
                juiceFrequency = juiceFrequency
            )
        },
        mealsOutsideHome = mealsOutsideHome?.run {
            if (eatsOut == null) null
            else MealsOutsideHomeResponse(
                eatsOut = eatsOut,
                mealsPerWeek = mealsPerWeek
            )
        }
    )
}

internal fun TobaccoCessationResponse.toTobaccoCessationEntity(): TobaccoCessationEntity {
    return TobaccoCessationEntity(
        uuid = uuid,
        fhirId = fhirId,
        patientId = patientId,
        appointmentId = appointmentId,
        appUpdatedDate = appUpdatedDate,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        tobaccoUse = tobaccoUse,
        briefAdvice = briefAdvice,
        assessedStatus = assessedStatus,
        assistQuit = assistQuit,
        dateOfPlan = dateOfPlan,
        pharmacotherapy = pharmacotherapy,
        planStatus = planStatus
    )
}

suspend fun TobaccoCessationResponse.toTobaccoCessationEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): TobaccoCessationEntity {
    return this.toTobaccoCessationEntity().copy(
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
    )
}

internal fun TobaccoCessationEntity.toTobaccoCessationResponse(): TobaccoCessationResponse {
    return TobaccoCessationResponse(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        tobaccoUse = tobaccoUse,
        briefAdvice = briefAdvice,
        assessedStatus = assessedStatus,
        assistQuit = assistQuit,
        dateOfPlan = dateOfPlan,
        pharmacotherapy = pharmacotherapy,
        planStatus = planStatus
    )
}

fun InterventionMasterResponse.toInterventionMasterEntity(): InterventionMasterEntity {
    return InterventionMasterEntity(
        fhirId = fhirId,
        code = code,
        name = name,
        secondaryName = secondaryName,
        status = status
    )
}

fun ScreeningSiteMasterResponse.toScreeningSiteMasterEntity(): ScreeningSiteMasterEntity {
    return ScreeningSiteMasterEntity(
        id = id,
        name = name,
        location = location,
        areaCouncil = areaCouncil,
        serviceMode = serviceMode,
        fromDate = fromDate,
        toDate = toDate,
        status = status,
        staff = staff.map { it.toStaffEntity() }
    )
}

fun StaffResponse.toStaffEntity(): StaffEntity {
    return StaffEntity(
        id = id,
        name = name,
        mobile = mobile,
        email = email,
        isTeamLead = isTeamLead
    )
}

fun ScreeningSiteMasterEntity.toScreeningSiteMasterResponse(): ScreeningSiteMasterResponse {
    return ScreeningSiteMasterResponse(
        id = id,
        name = name,
        location = location,
        areaCouncil = areaCouncil,
        serviceMode = serviceMode,
        fromDate = fromDate,
        toDate = toDate,
        status = status,
        staff = staff.map { it.toStaffResponse() }
    )
}

fun StaffEntity.toStaffResponse(): StaffResponse {
    return StaffResponse(
        id = id,
        name = name,
        mobile = mobile,
        email = email,
        isTeamLead = isTeamLead
    )
}

fun InterventionMasterEntity.toInterventionMasterResponse(): InterventionMasterResponse {
    return InterventionMasterResponse(
        fhirId = fhirId,
        code = code,
        name = name,
        secondaryName = secondaryName,
        status = status
    )
}

fun InterventionResponse.toInterventionEntity(): InterventionEntity {
    return InterventionEntity(
        uuid = uuid!!,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        interventions = interventions
    )
}

suspend fun InterventionResponse.toInterventionEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): InterventionEntity {
    return this.toInterventionEntity().copy(
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!,
    )
}

suspend fun InterventionEntity.toInterventionResponseLocal(
    interventionDao: InterventionDao
): InterventionResponseLocal {
    return InterventionResponseLocal(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        interventions = interventions.map { fhirId ->
            val intervention = interventionDao.getInterventionByFhirId(fhirId)
            InterventionItem(
                fhirId = fhirId,
                code = intervention.code,
                display = intervention.name
            )
        }
    )
}

fun ExaminationMasterResponse.toExaminationMasterEntity(): ExaminationMasterEntity {
    return ExaminationMasterEntity(
        fhirId = fhirId,
        code = code,
        name = name,
        secondaryName = secondaryName,
        status = status
    )
}

fun ExaminationMasterEntity.toExaminationMasterResponse(): ExaminationMasterResponse {
    return ExaminationMasterResponse(
        fhirId = fhirId,
        code = code,
        name = name,
        secondaryName = secondaryName,
        status = status
    )
}

fun ExaminationResponse.toExaminationEntity(): ExaminationEntity{
    return ExaminationEntity(
        uuid = uuid!!,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        examinations = examinations
    )
}

suspend fun ExaminationResponse.toExaminationEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): ExaminationEntity {
    return this.toExaminationEntity().copy(
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!
    )
}

suspend fun ExaminationEntity.toExaminationResponseLocal(
    examinationDao: ExaminationDao
): ExaminationResponseLocal {
    return ExaminationResponseLocal(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        examinations = examinations.map { fhirId ->
            val examination = examinationDao.getExaminationByFhirId(fhirId)
            ExaminationItem(
                fhirId = fhirId,
                code = examination.code,
                display = examination.name
            )
        }
    )
}

fun ReferralResponse.toReferralEntity(): ReferralEntity{
    return ReferralEntity(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId!!,
        practitionerName = practitionerName!!,
        healthFacilityId = healthFacilityId,
        note = note,
        sourceHealthFacilityId = sourceHealthFacilityId!!,
        sourceIslandId = sourceIslandId!!,
    )
}

suspend fun ReferralResponse.toReferralEntity(
    patientDao: PatientDao,
    appointmentDao: AppointmentDao
): ReferralEntity {
    return this.toReferralEntity().copy(
        appointmentId = appointmentDao.getAppointmentIdByFhirId(appointmentId),
        patientId = patientDao.getPatientIdByFhirId(patientId)!!
    )
}

fun ReferralEntity.toReferralResponse(): ReferralResponse {
    return ReferralResponse(
        uuid = uuid,
        fhirId = fhirId,
        appUpdatedDate = appUpdatedDate,
        appointmentId = appointmentId,
        patientId = patientId,
        practitionerId = practitionerId,
        practitionerName = practitionerName,
        healthFacilityId = healthFacilityId,
        note = note,
        sourceHealthFacilityId = sourceHealthFacilityId,
        sourceIslandId = sourceIslandId
    )
}

fun HealthFacilityResponse.toHealthFacilityEntity(): HealthFacilityEntity {
    return HealthFacilityEntity(
        healthFacilityId = healthFacilityId,
        code = code,
        heartcareId = heartcareId,
        islandId = islandId,
        name = name
    )
}

fun HealthFacilityEntity.toHealthFacilityResponse(): HealthFacilityResponse {
    return HealthFacilityResponse(
        code = code,
        healthFacilityId = healthFacilityId,
        heartcareId = heartcareId,
        islandId = islandId,
        name = name
    )
}

fun HealthFacilityEntity.toLevelResponse(): LevelResponse {
    return LevelResponse(
        fhirId = healthFacilityId,
        code = code,
        levelType = "health-facility",
        name = name,
        population = null,
        precedingLevelId = islandId,
        secondaryName = null,
        status = "active"
    )
}
