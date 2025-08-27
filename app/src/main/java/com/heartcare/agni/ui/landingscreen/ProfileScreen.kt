package com.heartcare.agni.ui.landingscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.BuildConfig
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.SyncStatusMessageEnum
import com.heartcare.agni.data.local.enums.WorkerStatus
import com.heartcare.agni.ui.theme.Primary10
import com.heartcare.agni.ui.theme.SyncFailedColor
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .border(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        width = 1.5.dp,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.userName[0].uppercase(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = viewModel.userName.replaceFirstChar {
                        it.titlecase(Locale.ROOT)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = viewModel.userRole,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        PhoneNumberRow(viewModel)
        EmailRow(viewModel)
        SyncStatusView(viewModel)
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        AppVersionInfoCard()
    }
}

@Composable
private fun EmailRow(viewModel: LandingScreenViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.mail),
            contentDescription = "MAIL_ICON",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(12.dp)
                .size(26.dp)
        )
        Text(
            text = viewModel.userEmail.ifBlank { stringResource(R.string.dash) },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PhoneNumberRow(viewModel: LandingScreenViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.call),
            contentDescription = "CALL_ICON",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(12.dp)
                .size(26.dp)
        )
        Text(
            text = viewModel.userPhoneNo.ifBlank { stringResource(R.string.dash) },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SyncStatusView(viewModel: LandingScreenViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.sync_status),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.synced),
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.lastSyncDate,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                if (viewModel.syncIconDisplay != 0) SyncStatusChip(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun SyncStatusChip(viewModel: LandingScreenViewModel) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (viewModel.syncStatusDisplay) {
                SyncStatusMessageEnum.SYNCING_FAILED.display -> {
                    SyncFailedColor
                }

                SyncStatusMessageEnum.SYNCING_COMPLETED.display -> {
                    MaterialTheme.colorScheme.primaryContainer
                }

                else -> {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            }
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 10.dp, end = 12.dp, bottom = 5.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = viewModel.syncIconDisplay),
                contentDescription = "",
                tint = setTextAndIconColor(viewModel = viewModel)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = viewModel.syncStatusDisplay,
                color = setTextAndIconColor(viewModel = viewModel),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

}

@Composable
private fun setTextAndIconColor(viewModel: LandingScreenViewModel): Color {
    return when (viewModel.syncStatus) {
        WorkerStatus.FAILED -> {
            Primary10
        }

        else -> {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

}

@Composable
private fun AppVersionInfoCard() {
    Text(
        text = stringResource(R.string.agni_app_version, BuildConfig.VERSION_NAME),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}