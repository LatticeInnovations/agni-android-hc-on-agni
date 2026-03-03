package com.heartcare.agni.utils.pdf

import com.heartcare.agni.data.local.enums.FamilyHistoryEnum.Companion.familyHistoryDisplayFromCode
import com.heartcare.agni.data.local.enums.FatFrequency.Companion.fatFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.FatType
import com.heartcare.agni.data.local.enums.FatType.Companion.fatTypeDisplayFromCode
import com.heartcare.agni.data.local.enums.FruitJuiceFrequency.Companion.fruitJuiceFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.KnowEnum
import com.heartcare.agni.data.local.enums.MedicationAdherence.Companion.getAdherenceDisplay
import com.heartcare.agni.data.local.enums.Pharmacotherapy.Companion.pharmacotherapyDisplayFromCode
import com.heartcare.agni.data.local.enums.QuitPlan.Companion.quitPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.SaltAmountEnum.Companion.saltAmountDisplayFromCode
import com.heartcare.agni.data.local.enums.SaltFrequencyEnum.Companion.saltFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.SoftDrinkFrequency.Companion.softDrinkFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.StatusOfPlan.Companion.statusOfPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.TobaccoProduct
import com.heartcare.agni.data.local.enums.TobaccoUsage.Companion.tobaccoUsageDisplayFromCode
import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.examination.ExaminationResponseLocal
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.ui.historyandtests.medication.getListOfHistoryMedication
import com.heartcare.agni.ui.historyandtests.priordx.getListOfPriorDx
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMMMMddyyyy
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMMyyyy

object PDFHelper {

    fun pdfCss(): String {
        return """
        <style>
            @page {
                size: A4;
                margin: 20px 6px;
            }

            body {
                font-family: Roboto;
                background-color: #ffffff;
                margin: 0;
                padding: 0;
                color: #1C1B1F;
            }

            .container {
                background-color: #f4f6f8;
                margin: 30px 12px 80px 12px;
            }

            .header {
                display: flex;
                align-items: center;
            }

            .logo {
                height: 63px;
                margin: 0px 12px;
            }

            .title {
                font-family: Montserrat;
                font-size: 36px;
                font-weight: 500;
                color: #018DCA;
                letter-spacing: -0.5px;
            }

            .section-header {
                background-color: #C9DAF8;
                padding: 18px 14px;
                font-weight: 400;
                font-size: 16px;
                position: relative;
                margin-bottom: 20px;
                letter-spacing: 0.5px;
            }

            .section-header::after {
                content: "";
                position: absolute;
                right: 0;
                top: 0;
                height: 100%;
                width: 55px;
                background-color: #006198;
            }

            .row {
                padding: 18px 14px;
                display: flex;
                gap: 24px;
            }

            .multiple-items {
                justify-content: space-between;
            }

            .multiple-items .item {
                flex: 1;
            }

            .col {
                flex: 1;
                display: flex;
                gap: 6px;
            }

            .label {
                font-weight: 500;
                font-size: 14px;
                color: #79747E;
                line-height: 20px;
                letter-spacing: 0.1px;
            }

            .sub-label {
                font-weight: 500;
                font-size: 12px;
                color: #79747E;
                line-height: 16px;
                letter-spacing: 0.5px;
            }

            .value {
                font-weight: 400;
                font-size: 14px;
                color: #1C1B1F;
                line-height: 20px;
                letter-spacing: 0.25px;
            }
            
            .prescribed-label {
                font-weight: 400;
                font-size: 14px;
                color: #D96F31;
                line-height: 20px;
                letter-spacing: 0.25px;
            }
        </style>
    """.trimIndent()
    }

    fun headerSection(): String {
        return """
        <div class="header">
            <img src="file:///android_asset/heartcare-agni-logo.png" class="logo" />
            <div class="title">HeartCare</div>
        </div>
    """.trimIndent()
    }

    fun personalInfoSection(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal
    ): String {

        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Personal information</div>

                <div class="row">
                    <div class="col">
                        <div class="label">Heartcare ID:</div>
                        <div class="value">${patient.heartcareId}</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col">
                        <div class="label">Patient name:</div>
                        <div class="value">${patient.firstName} ${patient.lastName}</div>
                    </div>
                </div>

                <div class="row multiple-items">
                    <div class="col">
                        <div class="label">Date of birth (Age):</div>
                        <div class="value">${patient.birthDate.toTimeInMilli().toAge()}</div>
                    </div>

                    <div class="col">
                        <div class="label">Sex:</div>
                        <div class="value">${patient.gender.capitalizeFirst()}</div>
                    </div>

                    <div class="col">
                        <div class="label">Date of visit:</div>
                        <div class="value">${appointment.slot.start.toddMMMyyyy()}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun riskScoreSection(
        patient: PatientResponse,
        cvd: CVDResponse?
    ): String {

        val riskPrediction = cvd?.risk?.let { "$it%" } ?: "--"

        val riskStatus = cvd?.let {
            "${patient.gender[0].uppercase()}/${patient.birthDate.toTimeInMilli().toAge()}, " +
                    "${if (it.diabetic == 0) "non-diabetic" else "diabetic"}, " +
                    "${if (it.smoker == 0) "not a tobacco user" else "tobacco user"}, " +
                    if (it.heartAttackHistory == 0) "without a history of heart attack or stroke"
                    else "with a history of heart attack or stroke"
        } ?: "--"

        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Risk score</div>

                <div class="row">
                    <div>
                        <div class="label">Risk prediction:</div>
                        <div class="value">$riskPrediction</div>
                    </div>
                </div>

                <div class="row">
                    <div>
                        <div class="label">Risk status:</div>
                        <div class="value">$riskStatus</div>
                    </div>
                </div>
                
                <div class="row multiple-items">
                                <div class="item">
                                    <div class="label">Weight:</div>
                                    <div class="value">${cvd?.let { "${it.weight} ${it.weightUnit}" } ?: "--"}</div>
                                </div>

                                <div class="item">
                                    <div class="label">BMI:</div>
                                    <div class="value">${cvd?.let { "${it.bmi}" } ?: "--"}</div>
                                </div>

                                <div class="item">

                                </div>
                            </div>

                            <div class="row multiple-items">
                                <div class="item">
                                    <div class="label">Height:</div>
                                    <div class="value">${
            cvd?.let {
                if (it.heightCm != null) "${it.heightCm} cm"
                else if (it.heightFt != null || it.heightInch != null) "${it.heightFt} ft ${it.heightInch ?: 0} in"
                else "--"
            } ?: "--"
        }</div>
                                </div>

                                <div class="item">
                                    <div class="label">Blood pressure:</div>
                                    <div class="value">${cvd?.let { "${it.bpSystolic}/${it.bpDiastolic} mmhg" } ?: "--"}</div>
                                </div>

                                <div class="item">
                                    <div class="label">Total cholesterol:</div>
                                    <div class="value">${cvd?.cholesterol?.let { "$it ${cvd.cholesterolUnit}" } ?: "--"}</div>
                                </div>
                            </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun chiefComplaintSection(cvd: CVDResponse?): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Chief complaint</div>
                <div class="row">
                    <div class="value">${cvd?.chiefComplaint ?: "--"}</div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun testResultSection(vital: VitalResponse?): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Test results</div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Glucose</div>
                        <div class="value">${vital?.bloodGlucose?.let { "${it.value} ${it.unit}" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Abdominal circumference</div>
                        <div class="value">${vital?.abdominalCircumference?.let { "${it.value} ${it.unit}" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Serum creatinine</div>
                        <div class="value">${vital?.serumCreatinine?.let { "${it.value} ${it.unit}" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Foot examination</div>
                        <div class="value">${vital?.footExamination ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Hip circumference</div>
                        <div class="value">${vital?.hipCircumference?.let { "${it.value} ${it.unit}" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Serum potassium</div>
                        <div class="value">${vital?.serumPotassium?.let { "${it.value} ${it.unit}" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Eye examination</div>
                        <div class="value">${vital?.eyeExamination ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">HbAlc</div>
                        <div class="value">${vital?.hbA1cPercentage?.let { "${it}%" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Urine ketone</div>
                        <div class="value">${vital?.urineKetones ?: "--"}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun historySection(
        priorDx: PriorDxResponse?,
        familyHistory: FamilyHistoryResponse?,
        historyMedication: HistoryMedicationResponse?,
        allergy: AllergyResponse?
    ): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">History</div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Prior diagnosis</div>
                        <div class="value">${priorDx?.let { getListOfPriorDx(it).ifEmpty { listOf("--") }.joinToString(", ") } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Family history</div>
                        <div class="value">${familyHistory?.familyDiseases?.takeIf { it.isNotEmpty() }?.joinToString(", ") { familyHistoryDisplayFromCode(it)} ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Adherence</div>
                        <div class="value">${historyMedication?.adherence?.let { getAdherenceDisplay(it) } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Taking prescribed medication for</div>
                        <div class="value">${historyMedication?.adherence?.let { "${getAdherenceDisplay(it)}, " } ?: ""} ${historyMedication?.let { getListOfHistoryMedication(it).ifEmpty { listOf("--") }.joinToString(", ") } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Allergies</div>
                        <div class="value">${allergy?.allergy ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Side effects</div>
                        <div class="value">${historyMedication?.sideEffects ?: "--"}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun riskFactorsSection(riskFactors: RiskFactorResponse?): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Risk factors</div>
                
                <!-- Tobacco -->
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Tobacco</div>
                        <div class="sub-label">Current use</div>
                        <div class="value">${riskFactors?.tobacco?.let { if (it.tobaccoUser) "Yes" else "No" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Products used</div>
                        <div class="value">${riskFactors?.tobacco?.tobaccoItemType?.let { TobaccoProduct.tobaccoTypeDisplayFromCode(it) +
                if (it == TobaccoProduct.OTHER.code) " (${riskFactors.tobacco.tobaccoOther})" else "" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Age when started</div>
                        <div class="value">${riskFactors?.tobacco?.startAge?.let { "$it years" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Avg daily use</div>
                        <div class="value">${riskFactors?.tobacco?.consumptionAmount?.let { "$it ${riskFactors.tobacco.consumptionUnit}" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Willing to quit</div>
                        <div class="value">${riskFactors?.tobacco?.willingToQuit?.let { if (it) "Yes" else "No" } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Alcohol -->
                <div class="row">
                    <div class="item">
                        <div class="label">Alcohol</div>
                        <div class="sub-label">Consumed in the last 30 days?</div>
                        <div class="value">${riskFactors?.alcohol?.consumedWithin30Days?.let { if (it) "Yes" else "No" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Had at least one standard drink</div>
                        <div class="value">${riskFactors?.alcohol?.alcoholQ1?.let { "$it times in past 30 days" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Standard drinks per occasion</div>
                        <div class="value">${riskFactors?.alcohol?.alcoholQ2?.let { "$it" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">≥ 6 standard drinks in a single occasion</div>
                        <div class="value">${riskFactors?.alcohol?.alcoholQ3?.let { "$it times in past 30 days" } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Fruits & vegetables -->
                <div class="row">
                    <div class="item">
                        <div class="label">Fruits & vegetables</div>
                        <div class="sub-label">Eat weekly?</div>
                        <div class="value">${riskFactors?.fruitsVegetables?.consumptionInWeek?.let { if (it) "Yes" else "No" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Frequency - fruits</div>
                        <div class="value">${riskFactors?.fruitsVegetables?.fruitsDays?.let { "$it days a week" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Servings of fruits</div>
                        <div class="value">${riskFactors?.fruitsVegetables?.fruitServings?.let { "$it" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Frequency - veg</div>
                        <div class="value">${riskFactors?.fruitsVegetables?.vegetableDays?.let { "$it days a week" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Servings of veg</div>
                        <div class="value">${riskFactors?.fruitsVegetables?.vegetableServings?.let { "$it" } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Physical activity -->
                <div class="row">
                    <div class="item">
                        <div class="label">Physical activity</div>
                        <div class="sub-label">Weekly activity</div>
                        <div class="value">${riskFactors?.physicalActivity?.weeklyEngagement?.let { if (it) "Active for at least 10 mins/week" else "Not active for at least 10 mins/week" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Vigorous activity</div>
                        <div class="value">${riskFactors?.physicalActivity?.let { "${it.vigorousTime} minutes, ${it.vigorousDays} days a week" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Moderate intensity activity</div>
                        <div class="value">${riskFactors?.physicalActivity?.let { "${it.moderateTime} minutes, ${it.moderateDays} days a week" } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Salt -->
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Salt or salty sauces</div>
                        <div class="sub-label">Amount consumed</div>
                        <div class="value">${riskFactors?.salt?.saltAmount?.let { saltAmountDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Adding salt while eating</div>
                        <div class="value">${riskFactors?.salt?.saltAddMeal?.let { saltFrequencyDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Adding salt while cooking</div>
                        <div class="value">${riskFactors?.salt?.saltAddCooking?.let { saltFrequencyDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Eating processed food high in salt</div>
                        <div class="value">${riskFactors?.salt?.saltProcessedFood?.let { saltFrequencyDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Fats & oils -->
                <div class="row">
                    <div class="item">
                        <div class="label">Fats & oils</div>
                        <div class="value">${
                            riskFactors?.fatAndOil?.let { fat -> 
                                fat.oilUsed?.let { oilUsed ->
                                    fatTypeDisplayFromCode(oilUsed).let {
                                        it + if (oilUsed == FatType.OTHERS.code) " (${fat.otherFatAndOils})" else ""
                                    }
                                } +
                                fat.fatFoodFrequency?.let { frequency ->
                                    ", ${fatFrequencyDisplayFromCode(frequency)}"
                                }
                            } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Sugars -->
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Sugars - soft drinks & fruit juice</div>
                        <div class="sub-label">Soft drinks consumed</div>
                        <div class="value">${ riskFactors?.sugar?.softDrinkFrequency?.let { softDrinkFrequencyDisplayFromCode(it)  } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Fruit juice consumed</div>
                        <div class="value">${ riskFactors?.sugar?.juiceFrequency?.let { fruitJuiceFrequencyDisplayFromCode(it)  } ?: "--"}</div>
                    </div>
                </div>
                
                <!-- Dining out -->
                <div class="row">
                    <div class="item">
                        <div class="label">Dining out</div>
                        <div class="value">${ riskFactors?.mealsOutsideHome?.eatsOut?.let { if (it) "${riskFactors.mealsOutsideHome.mealsPerWeek} meals out each week" else KnowEnum.DO_NOT_KNOW.display } ?: "--"}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun tobaccoCessationSection(tobaccoCessation: TobaccoCessationResponse?): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Tobacco cessation</div>
                
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Tobacco use status</div>
                        <div class="sub-label">Does the patient use tobacco?</div>
                        <div class="value">${tobaccoCessation?.tobaccoUse?.let { tobaccoUsageDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Brief advice</div>
                        <div class="sub-label">Has patient been given personalized advice on quitting tobacco use?</div>
                        <div class="value">${tobaccoCessation?.briefAdvice?.let { if (it) "Yes" else "No" } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Assessed status</div>
                        <div class="sub-label">Is the patient ready to quit?</div>
                        <div class="value">${tobaccoCessation?.assessedStatus?.let { if (it) "Yes" else "No" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Assist to quit</div>
                        <div class="sub-label">Quit plan completed?</div>
                        <div class="value">${tobaccoCessation?.assistQuit?.let { quitPlanDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Pharmacotherapy</div>
                        <div class="sub-label">Pharmacotherapy (PH) provided?</div>
                        <div class="value">${tobaccoCessation?.pharmacotherapy?.let { pharmacotherapyDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">&nbsp;</div>
                        <div class="sub-label">Start date of plan</div>
                        <div class="value">${tobaccoCessation?.dateOfPlan?.toMMMMddyyyy() ?: "--"}</div>
                    </div>
                </div>
                <div class="row">
                    <div class="item">
                        <div class="sub-label">Status of plan</div>
                        <div class="value">${tobaccoCessation?.planStatus?.let { statusOfPlanDisplayFromCode(it) } ?: "--"}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    fun diagnosisAndPrescriptionSection(
        diagnosis: DiagnosisLocal?,
        prescription: PrescriptionAndMedicineRelation?,
        examination: ExaminationResponseLocal?,
        intervention: InterventionResponseLocal?
    ): String {
        return """
        <div class="container">
            <div class="section">
                <div class="section-header">Diagnosis & prescription</div>
                
                <div class="row">
                    <div class="item">
                        <div class="label">Diagnosis:</div>
                        <div class="value">${diagnosis?.diagnosis?.takeIf { it.isNotEmpty() }?.joinToString(separator = "<br>") { "${it.code}, ${it.display}" } ?: "--"}</div>
                    </div>
                </div>
                <div class="row multiple-items">
                    <div class="item">
                        <div class="label">Medication:</div>
                        ${prescriptionSection(prescription)}
                    </div>
                    <div class="item">
                        <div class="label">Examination:</div>
                        <div class="value">${examination?.examinations?.takeIf { it.isNotEmpty() }?.joinToString(separator = "<br>") { it.display } ?: "--"}</div>
                    </div>
                    <div class="item">
                        <div class="label">Intervention:</div>
                        <div class="value">${intervention?.interventions?.takeIf { it.isNotEmpty() }?.joinToString(separator = "<br>") { it.display } ?: "--"}</div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    }

    private fun prescriptionSection(prescription: PrescriptionAndMedicineRelation?): String {
        return if (prescription?.prescriptionDirectionAndMedicineView?.isEmpty() == true) {
            """
                <div class="value">--</div>
            """.trimIndent()
        } else {
            buildString {
                prescription?.prescriptionDirectionAndMedicineView?.forEach { item ->
                    append(
                        """
                    <div style="padding-bottom: 6px;">
                        <div class="value">${item.medicationEntity.medName}</div>
                        <div class="sub-label">
                            ${item.prescriptionDirectionsEntity.qtyPerDose} each, ${item.prescriptionDirectionsEntity.frequency}/D for ${item.prescriptionDirectionsEntity.duration} days
                        </div>
                        <div class="label">${item.prescriptionDirectionsEntity.note ?: "" }</div>
                        <div class="prescribed-label">Prescribed</div>
                    </div>
                    """.trimIndent()
                    )
                }
            }
        }
    }
}