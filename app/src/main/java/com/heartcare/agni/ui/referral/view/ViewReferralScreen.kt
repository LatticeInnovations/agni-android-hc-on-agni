package com.heartcare.agni.ui.referral.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.ui.referral.ReferringDetailComposable
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.REFERRAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReferralScreen(
    navController: NavController,
    viewModel: ViewReferralViewModel = hiltViewModel()
) {
    navController.previousBackStackEntry?.savedStateHandle?.get<ReferralResponse>(
        REFERRAL
    )?.let { referral ->
        LaunchedEffect(viewModel.isLaunched) {
            if (!viewModel.isLaunched) {
                viewModel.getDetails(referral)
                viewModel.isLaunched = true
            }
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    title = {
                        Text(
                            text = stringResource(id = R.string.view_referral),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("HEADING_TAG")
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReferringDetailComposable(
                            physician = referral.practitionerName!!,
                            facility = viewModel.sourceFacilityName,
                            date = referral.appUpdatedDate
                        )
                        ReferralFacilityComposable(
                            facilityName = viewModel.referredFacilityName,
                            note = referral.note
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun ReferralFacilityComposable(
    facilityName: String,
    note: String?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        color = if (isSystemInDarkTheme()) Black else White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.referral_facility),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = facilityName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.notes_colon, note ?: "--"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}