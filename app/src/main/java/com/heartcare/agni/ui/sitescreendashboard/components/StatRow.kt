package com.heartcare.agni.ui.sitescreendashboard.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heartcare.agni.data.local.model.report.StatRowData


@Composable
fun StatRow(stat: StatRowData, isLast: Boolean) {
    Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${stat.percentage}% (F ${stat.femaleCount}, M ${stat.maleCount}, O ${stat.otherCount})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { stat.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = stat.progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
