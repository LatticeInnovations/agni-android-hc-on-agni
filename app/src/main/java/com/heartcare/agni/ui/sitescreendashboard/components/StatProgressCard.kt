package com.heartcare.agni.ui.sitescreendashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.data.local.model.report.StatSubGroup


@Composable
fun StatProgressCard(
    title: String,
    modifier: Modifier = Modifier,
    stats: List<StatRowData> = emptyList(),
    subGroups: List<StatSubGroup> = emptyList()
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (subGroups.isNotEmpty()) {
                subGroups.forEachIndexed { groupIndex, group ->
                    Text(
                        text = group.heading,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            group.stats.forEachIndexed { index, stat ->
                                StatRow(stat = stat, isLast = index == group.stats.lastIndex)
                            }
                        }
                    }
                    if (groupIndex != subGroups.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                stats.forEachIndexed { index, stat ->
                    StatRow(stat = stat, isLast = index == stats.lastIndex)
                }
            }
        }
    }
}