package com.heartcare.agni.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heartcare.agni.ui.appointments.AppointmentsScreen
import com.heartcare.agni.ui.appointments.schedule.ScheduleAppointments
import com.heartcare.agni.ui.cvd.CVDRiskAssessmentScreen
import com.heartcare.agni.ui.diagnosis.DiagnosisScreen
import com.heartcare.agni.ui.diagnosis.add.AddDiagnosisScreen
import com.heartcare.agni.ui.dispense.DrugDispenseScreen
import com.heartcare.agni.ui.dispense.otc.OTCScreen
import com.heartcare.agni.ui.dispense.prescription.dispenseprescription.DispensePrescriptionScreen
import com.heartcare.agni.ui.historyandtests.HistoryTakingAndTestsScreen
import com.heartcare.agni.ui.historyandtests.allergy.AddAllergyScreen
import com.heartcare.agni.ui.historyandtests.family.AddFamilyHistoryScreen
import com.heartcare.agni.ui.historyandtests.medication.AddMedicationScreen
import com.heartcare.agni.ui.historyandtests.priordx.AddPriorDxScreen
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorScreen
import com.heartcare.agni.ui.historyandtests.risk.view.RiskFactorsViewScreen
import com.heartcare.agni.ui.historyandtests.tobacco.add.AddTobaccoCessationScreen
import com.heartcare.agni.ui.historyandtests.tobacco.view.TobaccoCessationViewScreen
import com.heartcare.agni.ui.householdmember.HouseholdMembersScreen
import com.heartcare.agni.ui.householdmember.addhouseholdmember.AddHouseholdMember
import com.heartcare.agni.ui.householdmember.connectpatient.ConnectPatient
import com.heartcare.agni.ui.householdmember.searchresult.SearchResult
import com.heartcare.agni.ui.intervention.InterventionScreen
import com.heartcare.agni.ui.intervention.add.AddInterventionScreen
import com.heartcare.agni.ui.labtestandmedicalrecord.photo.upload.PhotoUploadScreen
import com.heartcare.agni.ui.labtestandmedicalrecord.photo.view.PhotoViewScreen
import com.heartcare.agni.ui.landingscreen.LandingScreen
import com.heartcare.agni.ui.login.OtpScreen
import com.heartcare.agni.ui.login.PhoneEmailScreen
import com.heartcare.agni.ui.login.pin.PinScreen
import com.heartcare.agni.ui.login.forgotpassword.ForgotPasswordScreen
import com.heartcare.agni.ui.login.createpassword.CreatePasswordScreen
import com.heartcare.agni.ui.login.forgotpassword.otp.AuthenticateOtpScreen
import com.heartcare.agni.ui.login.userpassword.UserPasswordScreen
import com.heartcare.agni.ui.patienteditscreen.address.EditPatientAddress
import com.heartcare.agni.ui.patienteditscreen.basicinfo.EditBasicInformation
import com.heartcare.agni.ui.patienteditscreen.identification.EditIdentification
import com.heartcare.agni.ui.patientlandingscreen.PatientLandingScreen
import com.heartcare.agni.ui.patientprofile.PatientProfile
import com.heartcare.agni.ui.patientregistration.PatientRegistration
import com.heartcare.agni.ui.patientregistration.preview.PatientRegistrationPreview
import com.heartcare.agni.ui.patientregistration.step4.ConfirmRelationship
import com.heartcare.agni.ui.prescription.PrescriptionScreen
import com.heartcare.agni.ui.prescription.photo.upload.PrescriptionPhotoUploadScreen
import com.heartcare.agni.ui.prescription.photo.view.PrescriptionPhotoViewScreen
import com.heartcare.agni.ui.searchpatient.SearchPatient
import com.heartcare.agni.ui.signup.SignUpPhoneEmailScreen
import com.heartcare.agni.ui.signup.SignUpScreen
import com.heartcare.agni.ui.vitalsscreen.VitalsScreen
import com.heartcare.agni.ui.vitalsscreen.addvitals.AddVitalsScreen
import com.heartcare.agni.ui.symptomsanddiagnosis.SymptomsAndDiagnosisScreen
import com.heartcare.agni.ui.symptomsanddiagnosis.selectsymptoms.SelectSymptomScreen
import com.heartcare.agni.ui.vaccination.error.VaccinationErrorScreen
import com.heartcare.agni.ui.vaccination.VaccinationScreen
import com.heartcare.agni.ui.vaccination.add.AddVaccinationScreen
import com.heartcare.agni.ui.vaccination.view.ViewVaccinationScreen

@Composable
fun NavigationAppHost(navController: NavController, startDest: String) {
    NavHost(navController = navController as NavHostController, startDestination = startDest) {
        composable(Screen.UserIdPasswordScreen.route) { UserPasswordScreen(navController) }
        composable(Screen.CreatePasswordScreen.route) { CreatePasswordScreen(navController) }
        composable(Screen.PinScreen.route) { PinScreen(navController) }
        composable(Screen.ForgotPasswordScreen.route) { ForgotPasswordScreen(navController) }
        composable(Screen.AuthenticateOtpScreen.route) { AuthenticateOtpScreen(navController) }
        composable(Screen.PhoneEmailScreen.route) { PhoneEmailScreen(navController) }
        composable(Screen.SignUpPhoneEmailScreen.route) { SignUpPhoneEmailScreen(navController) }
        composable(Screen.SignUpScreen.route) { SignUpScreen(navController = navController) }
        composable(Screen.OtpScreen.route) { OtpScreen(navController) }
        composable(Screen.LandingScreen.route) { LandingScreen(navController = navController) }
        composable(Screen.SearchPatientScreen.route) { SearchPatient(navController = navController) }
        composable(Screen.PatientRegistrationScreen.route) { PatientRegistration(navController = navController) }
        composable(Screen.PatientRegistrationPreviewScreen.route) {
            PatientRegistrationPreview(
                navController = navController
            )
        }
        composable(Screen.PatientLandingScreen.route) { PatientLandingScreen(navController = navController) }
        composable(Screen.HouseholdMembersScreen.route) { HouseholdMembersScreen(navController = navController) }
        composable(Screen.AddHouseholdMember.route) { AddHouseholdMember(navController = navController) }
        composable(Screen.ConfirmRelationship.route) { ConfirmRelationship(navController = navController) }
        composable(Screen.SearchResult.route) { SearchResult(navController = navController) }
        composable(Screen.ConnectPatient.route) { ConnectPatient(navController = navController) }
        composable(Screen.Prescription.route) { PrescriptionScreen(navController = navController) }
        composable(Screen.PatientProfile.route) { PatientProfile(navController = navController) }
        composable(Screen.EditBasicInfo.route) { EditBasicInformation(navController = navController) }
        composable(Screen.EditIdentification.route) { EditIdentification(navController = navController) }
        composable(Screen.EditAddress.route) { EditPatientAddress(navController = navController) }
        composable(Screen.Appointments.route) { AppointmentsScreen(navController = navController) }
        composable(Screen.ScheduleAppointments.route) { ScheduleAppointments(navController = navController) }
        composable(Screen.PrescriptionPhotoUploadScreen.route) {
            PrescriptionPhotoUploadScreen(
                navController = navController
            )
        }
        composable(Screen.PrescriptionPhotoViewScreen.route) {
            PrescriptionPhotoViewScreen(
                navController = navController
            )
        }
        composable(Screen.CVDRiskAssessmentScreen.route) { CVDRiskAssessmentScreen(navController) }
        composable(Screen.VitalsScreen.route) { VitalsScreen(navController = navController) }
        composable(Screen.AddVitalsScreen.route) { AddVitalsScreen(navController = navController) }

        composable(Screen.SymptomsAndDiagnosisScreen.route) { SymptomsAndDiagnosisScreen(navController = navController) }
        composable(Screen.AddSymptomsScreen.route) { SelectSymptomScreen(navController = navController) }
        composable(Screen.LabAndMedPhotoUploadScreen.route) { PhotoUploadScreen(navController = navController) }
        composable(Screen.LabAndMedRecordPhotoViewScreen.route) { PhotoViewScreen(navController = navController) }

        composable(Screen.DrugDispenseScreen.route) { DrugDispenseScreen(navController = navController) }
        composable(Screen.DispensePrescriptionScreen.route) { DispensePrescriptionScreen(navController = navController) }
        composable(Screen.OTCScreen.route) { OTCScreen(navController = navController) }

        composable(Screen.VaccinationScreen.route) { VaccinationScreen(navController = navController) }
        composable(Screen.AddVaccinationScreen.route) { AddVaccinationScreen(navController = navController) }
        composable(Screen.ViewVaccinationScreen.route) { ViewVaccinationScreen(navController = navController) }
        composable(Screen.VaccinationErrorScreen.route) { VaccinationErrorScreen(navController = navController) }

        composable(Screen.HistoryTakingAndTestsScreen.route) { HistoryTakingAndTestsScreen(navController = navController) }
        composable(Screen.AddPriorDxScreen.route) { AddPriorDxScreen(navController = navController) }
        composable(Screen.AddMedicationScreen.route) { AddMedicationScreen(navController = navController) }
        composable(Screen.AddFamilyHistoryScreen.route) { AddFamilyHistoryScreen(navController = navController) }
        composable(Screen.AddAllergyScreen.route) { AddAllergyScreen(navController = navController) }
        composable(
            route = Screen.RiskFactorsViewScreen.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500)
                )
            }
        ) { RiskFactorsViewScreen(navController = navController) }
        composable(Screen.AddRiskFactorsScreen.route) { AddRiskFactorScreen(navController = navController) }

        composable(
            route = Screen.TobaccoCessationViewScreen.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500)
                )
            }
        ) { TobaccoCessationViewScreen(navController = navController) }
        composable(Screen.AddTobaccoCessationScreen.route) { AddTobaccoCessationScreen(navController = navController) }
        composable(Screen.DiagnosisScreen.route) { DiagnosisScreen(navController = navController) }
        composable(Screen.AddDiagnosisScreen.route) { AddDiagnosisScreen(navController = navController) }
        composable(Screen.InterventionScreen.route) { InterventionScreen(navController = navController) }
        composable(Screen.AddInterventionScreen.route) { AddInterventionScreen(navController = navController) }
    }
}