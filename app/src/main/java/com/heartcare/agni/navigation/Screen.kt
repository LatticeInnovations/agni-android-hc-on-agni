package com.heartcare.agni.navigation

sealed class Screen(val route: String) {
    data object UserIdPasswordScreen: Screen("user_id_password_screen")
    data object CreatePasswordScreen: Screen("create_new_password_screen")
    data object PinScreen: Screen("pin_screen")
    data object ForgotPasswordScreen: Screen("forgot_password_screen")
    data object AuthenticateOtpScreen: Screen("auth_otp_screen")
    data object LandingScreen : Screen("landing_screen")
    data object SearchPatientScreen : Screen("search_patient")
    data object PatientRegistrationScreen : Screen("patient_registration")
    data object PatientRegistrationPreviewScreen : Screen("patient_registration_preview")
    data object PatientLandingScreen : Screen("patient_landing")
    data object Prescription : Screen("prescription")
    data object PatientProfile : Screen("patient_profile")
    data object EditBasicInfo : Screen("edit_basic_info")
    data object EditIdentification : Screen("edit_identification")
    data object EditAddress : Screen("edit_address")
    data object Appointments : Screen("appointments")
    data object ScheduleAppointments : Screen("schedule_appointments")
    data object CVDRiskAssessmentScreen : Screen("cvd_risk_assessment")
    data object VitalsScreen : Screen("vitals_screen")
    data object AddVitalsScreen : Screen("add_vitals_screen")

    data object HistoryTakingAndTestsScreen : Screen("history_taking_and_tests_screen")
    data object AddPriorDxScreen : Screen("add_prior_dx_screen")
    data object AddMedicationScreen : Screen("add_medication_screen")
    data object AddFamilyHistoryScreen : Screen("add_family_history_screen")
    data object AddAllergyScreen : Screen("add_allergy_screen")
    data object RiskFactorsViewScreen : Screen("view_risk_factors_screen")
    data object AddRiskFactorsScreen : Screen("add_risk_factors_screen")
    data object TobaccoCessationViewScreen : Screen("view_tobacco_cessation_screen")
    data object AddTobaccoCessationScreen : Screen("add_tobacco_cessation_screen")

    data object DiagnosisScreen : Screen("diagnosis_screen")
    data object AddDiagnosisScreen : Screen("add_diagnosis_screen")

    data object InterventionScreen : Screen("intervention_screen")
    data object AddInterventionScreen : Screen("add_intervention_screen")

    data object TestExaminationScreen : Screen("test_examination_screen")
    data object AddTestExaminationScreen : Screen("add_test_examination_screen")

    data object ReferralScreen : Screen("referral_screen")
    data object ViewReferralScreen : Screen("view_referral_screen")
    data object AddReferralScreen : Screen("add_referral_screen")

    data object ScreeningReportDownloadScreen : Screen("screening_report_download_screen")
}