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
import com.heartcare.agni.ui.common.Detail
import com.heartcare.agni.ui.common.Header
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TobaccoCessationViewScreen(
    navController: NavController
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Date().toPrescriptionDate(),
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
                        "Yes, every day"
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Header(stringResource(R.string.brief_advice))
                        Detail(
                            stringResource(R.string.brief_advice_question),
                            "Yes"
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Header(stringResource(R.string.assessed_status))
                        Detail(
                            stringResource(R.string.assessed_status_question),
                            "Yes"
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Header(stringResource(R.string.assist_to_quit))
                        Detail(
                            stringResource(R.string.assist_to_quit_question),
                            "Yes, intensive quit plan"
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Header(stringResource(R.string.pharmacotherapy))
                        Detail(
                            stringResource(R.string.pharmacotherapy_provided_question),
                            "Yes, Nicotine Replacement Therapy"
                        )
                    }
                    Detail(
                        stringResource(R.string.start_date_of_plan),
                        "12 Jan 2025"
                    )
                    Detail(
                        stringResource(R.string.status_of_plan),
                        "Active"
                    )
                }
            }
        }
    }
}