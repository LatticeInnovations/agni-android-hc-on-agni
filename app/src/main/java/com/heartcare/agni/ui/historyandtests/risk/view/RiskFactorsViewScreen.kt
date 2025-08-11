package com.heartcare.agni.ui.historyandtests.risk.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.FatFrequency.Companion.fatFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.FatType
import com.heartcare.agni.data.local.enums.FatType.Companion.fatTypeDisplayFromCode
import com.heartcare.agni.data.local.enums.FruitJuiceFrequency.Companion.fruitJuiceFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.KnowEnum
import com.heartcare.agni.data.local.enums.SaltAmountEnum.Companion.saltAmountDisplayFromCode
import com.heartcare.agni.data.local.enums.SaltFrequencyEnum.Companion.saltFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.SoftDrinkFrequency.Companion.softDrinkFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.TobaccoProduct
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.risk.AlcoholResponse
import com.heartcare.agni.data.server.model.risk.FatAndOilResponse
import com.heartcare.agni.data.server.model.risk.FruitsVegetablesResponse
import com.heartcare.agni.data.server.model.risk.MealsOutsideHomeResponse
import com.heartcare.agni.data.server.model.risk.PhysicalActivityResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.risk.SaltResponse
import com.heartcare.agni.data.server.model.risk.SugarResponse
import com.heartcare.agni.data.server.model.risk.TobaccoResponse
import com.heartcare.agni.ui.common.Detail
import com.heartcare.agni.ui.common.Header
import com.heartcare.agni.utils.constants.NavControllerConstants.RISK_FACTOR
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskFactorsViewScreen(
    navController: NavController
) {
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<RiskFactorResponse>(RISK_FACTOR)
        ?.let { rf ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                rf.appUpdatedDate.toPrescriptionDate(),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TobaccoSection(rf.tobacco)
                    AlcoholSection(rf.alcohol)
                    FruitsAndVegetablesSection(rf.fruitsVegetables)
                    PhysicalActivitySection(rf.physicalActivity)
                    SaltSection(rf.salt)
                    FatsAndOilsSection(rf.fatAndOil)
                    SugarsSection(rf.sugar)
                    DiningOutSection(rf.mealsOutsideHome)
                }
            }
        }
}

@Composable
private fun RiskFactorSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(title)
            content()
        }
    }
}

@Composable
private fun TobaccoSection(tobacco: TobaccoResponse?) =
    RiskFactorSection(stringResource(R.string.tobacco)) {
        Detail(
            stringResource(R.string.tobacco_question_one),
            when (tobacco?.tobaccoUser) {
                true -> YesNoEnum.YES.display
                false -> YesNoEnum.NO.display
                else -> null
            }
        )
        Detail(
            stringResource(R.string.tobacco_question_two),
            tobacco?.tobaccoItemType?.let {
                TobaccoProduct.tobaccoTypeDisplayFromCode(it) +
                        if (it == TobaccoProduct.OTHER.code) ": ${tobacco.tobaccoOther}" else ""
            }
        )
        Detail(
            stringResource(R.string.tobacco_question_three),
            tobacco?.consumptionAmount?.let { "$it ${tobacco.consumptionUnit}" }
        )
        Detail(
            stringResource(R.string.tobacco_question_four),
            tobacco?.startAge?.let { "$it year" }
        )
        Detail(
            stringResource(R.string.tobacco_question_five),
            when (tobacco?.willingToQuit) {
                true -> YesNoEnum.YES.display
                false -> YesNoEnum.NO.display
                else -> null
            }
        )
    }

@Composable
private fun AlcoholSection(alcohol: AlcoholResponse?) =
    RiskFactorSection(stringResource(R.string.alcohol)) {
        Detail(
            stringResource(R.string.alcohol_question_one),
            when (alcohol?.consumedWithin30Days) {
                true -> YesNoEnum.YES.display
                false -> YesNoEnum.NO.display
                else -> null
            }
        )
        Detail(stringResource(R.string.alcohol_question_two), alcohol?.alcoholQ1?.toString())
        Detail(stringResource(R.string.alcohol_question_three), alcohol?.alcoholQ2?.toString())
        Detail(stringResource(R.string.alcohol_question_four), alcohol?.alcoholQ3?.toString())
    }

@Composable
private fun FruitsAndVegetablesSection(fv: FruitsVegetablesResponse?) =
    RiskFactorSection(stringResource(R.string.fruits_and_vegetable)) {
        Detail(
            stringResource(R.string.fruits_vegetable_question_one),
            when (fv?.consumptionInWeek) {
                true -> YesNoEnum.YES.display
                false -> YesNoEnum.NO.display
                else -> null
            }
        )
        Detail(stringResource(R.string.fruits_vegetable_question_two), fv?.fruitsDays?.toString())
        Detail(
            stringResource(R.string.fruits_vegetable_question_three),
            fv?.vegetableDays?.toString()
        )
        Detail(
            stringResource(R.string.fruits_vegetable_question_four),
            fv?.fruitServings?.toString()
        )
        Detail(
            stringResource(R.string.fruits_vegetable_question_five),
            fv?.vegetableServings?.toString()
        )
    }

@Composable
private fun PhysicalActivitySection(pa: PhysicalActivityResponse?) =
    RiskFactorSection(stringResource(R.string.physical_activity)) {
        Detail(
            stringResource(R.string.physical_activity_question_one),
            when (pa?.weeklyEngagement) {
                true -> YesNoEnum.YES.display
                false -> YesNoEnum.NO.display
                else -> null
            }
        )
        Detail(
            stringResource(R.string.physical_activity_question_two),
            pa?.vigorousDays?.toString()
        )
        Detail(
            stringResource(R.string.physical_activity_question_three),
            pa?.moderateDays?.toString()
        )
        Detail(
            stringResource(R.string.physical_activity_question_four),
            pa?.vigorousTime?.toString()
        )
        Detail(
            stringResource(R.string.physical_activity_question_five),
            pa?.moderateTime?.toString()
        )
    }

@Composable
private fun SaltSection(salt: SaltResponse?) = RiskFactorSection(stringResource(R.string.salt)) {
    Detail(
        stringResource(R.string.salt_question_one),
        saltAmountDisplayFromCode(salt?.saltAmount ?: -1)
    )
    Detail(
        stringResource(R.string.salt_question_two),
        saltFrequencyDisplayFromCode(salt?.saltAddCooking ?: -1)
    )
    Detail(
        stringResource(R.string.salt_question_three),
        saltFrequencyDisplayFromCode(salt?.saltAddMeal ?: -1)
    )
    Detail(
        stringResource(R.string.salt_question_four),
        saltFrequencyDisplayFromCode(salt?.saltProcessedFood ?: -1)
    )
}

@Composable
private fun FatsAndOilsSection(fat: FatAndOilResponse?) =
    RiskFactorSection(stringResource(R.string.fats_and_oils)) {
        Detail(
            stringResource(R.string.fats_and_oils_question_one),
            fatTypeDisplayFromCode(fat?.oilUsed ?: -1)?.let {
                it + if (fat?.oilUsed == FatType.OTHERS.code) ": ${fat.otherFatAndOils}" else ""
            }
        )
        Detail(
            stringResource(R.string.fats_and_oils_question_two),
            fatFrequencyDisplayFromCode(fat?.fatFoodFrequency ?: -1)
        )
    }

@Composable
private fun SugarsSection(sugar: SugarResponse?) =
    RiskFactorSection(stringResource(R.string.sugars)) {
        Detail(
            stringResource(R.string.sugars_question_one),
            softDrinkFrequencyDisplayFromCode(sugar?.softDrinkFrequency ?: -1)
        )
        Detail(
            stringResource(R.string.sugars_question_two),
            fruitJuiceFrequencyDisplayFromCode(sugar?.juiceFrequency ?: -1)
        )
    }

@Composable
private fun DiningOutSection(dine: MealsOutsideHomeResponse?) =
    RiskFactorSection(stringResource(R.string.dining_out)) {
        Detail(
            stringResource(R.string.dining_out_question),
            when (dine?.eatsOut) {
                null -> null
                true -> dine.mealsPerWeek?.toString()
                false -> KnowEnum.DO_NOT_KNOW.display
            }
        )
    }