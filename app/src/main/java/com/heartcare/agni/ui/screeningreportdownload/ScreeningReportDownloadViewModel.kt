package com.heartcare.agni.ui.screeningreportdownload

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.allergy.AllergyRepository
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.diagnosis.DiagnosisRepository
import com.heartcare.agni.data.local.repository.examination.ExaminationRepository
import com.heartcare.agni.data.local.repository.family.FamilyHistoryRepository
import com.heartcare.agni.data.local.repository.historymedication.HistoryMedicationRepository
import com.heartcare.agni.data.local.repository.intervention.InterventionRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepository
import com.heartcare.agni.data.local.repository.priordx.PriorDxRepository
import com.heartcare.agni.data.local.repository.risk.RiskFactorRepository
import com.heartcare.agni.data.local.repository.tobacco.TobaccoCessationRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.di.dispatcher.MainDispatcher
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy
import com.heartcare.agni.utils.pdf.PDFHelper.generatePdf
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.chiefComplaintSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.diagnosisAndPrescriptionSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.headerSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.historySection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.pdfCss
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.personalInfoSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.riskFactorsSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.riskScoreSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.testResultSection
import com.heartcare.agni.utils.pdf.AssessmentReportPDF.tobaccoCessationSection
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltViewModel
class ScreeningReportDownloadViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository,
    private val appointmentRepository: AppointmentRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val priorDxRepository: PriorDxRepository,
    private val historyMedicationRepository: HistoryMedicationRepository,
    private val familyHistoryRepository: FamilyHistoryRepository,
    private val allergyRepository: AllergyRepository,
    private val riskFactorRepository: RiskFactorRepository,
    private val tobaccoCessationRepository: TobaccoCessationRepository,
    private val vitalRepository: VitalRepository,
    private val diagnosisRepository: DiagnosisRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val examinationRepository: ExaminationRepository,
    private val interventionRepository: InterventionRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @param:MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var appointmentList by mutableStateOf(listOf<AppointmentResponseLocal>())

    fun getAppointmentsList(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            appointmentList = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.hospitalCode == user.hospitalCode &&
                            appointmentResponseLocal.slot.start.time < Date().toEndOfDay()
                            && (
                            appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value
                                    || appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value
                            )
                }
        }
    }

    fun getAssessmentsByAppointmentId(appointment: AppointmentResponseLocal, context: Context) {
        viewModelScope.launch(ioDispatcher) {
            val cvd =
                cvdAssessmentRepository.getCVDRecordByAppointmentIds(appointment.uuid).getOrNull(0)
            val priorDx =
                priorDxRepository.getPriorDxRecordsByAppointmentIds(appointment.uuid).getOrNull(0)
            val historyMedication =
                historyMedicationRepository.getHistoryMedicationRecordsByAppointmentIds(
                    appointment.uuid
                ).getOrNull(0)
            val familyHistory =
                familyHistoryRepository.getFamilyHistoryRecordsByAppointmentIds(appointment.uuid)
                    .getOrNull(0)
            val allergy =
                allergyRepository.getAllergyRecordsByAppointmentIds(appointment.uuid).getOrNull(0)
            val riskFactor =
                riskFactorRepository.getRiskFactorRecordsByAppointmentIds(appointment.uuid)
                    .getOrNull(0)
            val tobaccoCessation =
                tobaccoCessationRepository.getTobaccoCessationRecordsByAppointmentIds(appointment.uuid)
                    .getOrNull(0)
            val vital = vitalRepository.getVitalByAppointmentId(appointment.uuid).getOrNull(0)
            val diagnosis = diagnosisRepository.getPastDiagnosisByAppointmentId(appointment.uuid).getOrNull(0)
            val prescription =
                prescriptionRepository.getLastPrescriptionAndMedicineByAppointmentId(appointment.uuid)
                    .getOrNull(0)
            val examination =
                examinationRepository.getExaminationListByAppointmentId(appointment.uuid)
                    .getOrNull(0)
            val intervention =
                interventionRepository.getInterventionListByAppointmentId(appointment.uuid)
                    .getOrNull(0)
            val html =
                """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8" />
            ${pdfCss()}
        </head>
        <body>
            ${headerSection()}
            ${personalInfoSection(patient!!, appointment)}
            ${riskScoreSection(patient!!, cvd)}
            ${chiefComplaintSection(cvd)}
            ${testResultSection(vital)}
            ${historySection(priorDx, familyHistory, historyMedication, allergy)}
            ${riskFactorsSection(riskFactor)}
            ${tobaccoCessationSection(tobaccoCessation)}
            ${diagnosisAndPrescriptionSection(diagnosis, prescription, examination, intervention)}
        </body>
        </html>
    """.trimIndent()
            val fileName = "${patient?.heartcareId}-${patient?.firstName} ${patient?.lastName}-${
                appointment.slot
                    .start.toddMMMyyyy()
            }"

            withContext(mainDispatcher) {
                generatePdf(context, html, fileName)
            }
        }
    }
}