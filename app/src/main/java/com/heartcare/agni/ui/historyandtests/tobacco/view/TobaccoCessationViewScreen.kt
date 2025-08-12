package com.heartcare.agni.ui.historyandtests.tobacco.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.heartcare.agni.data.local.enums.Pharmacotherapy.Companion.pharmacotherapyDisplayFromCode
import com.heartcare.agni.data.local.enums.QuitPlan.Companion.quitPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.StatusOfPlan.Companion.statusOfPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.TobaccoUsage.Companion.tobaccoUsageDisplayFromCode
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.ui.common.Detail
import com.heartcare.agni.ui.common.Header
import com.heartcare.agni.utils.constants.NavControllerConstants.TOBACCO_CESSATION
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TobaccoCessationViewScreen(
    navController: NavController
) {
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<TobaccoCessationResponse>(TOBACCO_CESSATION)
        ?.let { tc ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                tc.appUpdatedDate.toPrescriptionDate(),
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
                        .padding(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Detail(
                                stringResource(R.string.tobacco_cessation_question_one),
                                tobaccoUsageDisplayFromCode(tc.tobaccoUse ?: "")
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Header(stringResource(R.string.brief_advice))
                                Detail(
                                    stringResource(R.string.brief_advice_question),
                                    when (tc.briefAdvice) {
                                        true -> YesNoEnum.YES.display
                                        false -> YesNoEnum.NO.display
                                        else -> null
                                    }
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Header(stringResource(R.string.assessed_status))
                                Detail(
                                    stringResource(R.string.assessed_status_question),
                                    when (tc.assessedStatus) {
                                        true -> YesNoEnum.YES.display
                                        false -> YesNoEnum.NO.display
                                        else -> null
                                    }
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Header(stringResource(R.string.assist_to_quit))
                                Detail(
                                    stringResource(R.string.assist_to_quit_question),
                                    quitPlanDisplayFromCode(tc.assistQuit ?: "")
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Header(stringResource(R.string.pharmacotherapy))
                                Detail(
                                    stringResource(R.string.pharmacotherapy_provided_question),
                                    pharmacotherapyDisplayFromCode(tc.pharmacotherapy ?: "")
                                )
                            }
                            Detail(
                                stringResource(R.string.start_date_of_plan).substringBefore("*"),
                                tc.dateOfPlan?.toPrescriptionDate()
                            )
                            Detail(
                                stringResource(R.string.status_of_plan),
                                statusOfPlanDisplayFromCode(tc.planStatus ?: "")
                            )
                        }
                    }
                }
            }
        }
}