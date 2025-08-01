package com.heartcare.agni.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toDayFullMonthYear
import java.util.Date

@Composable
fun ExpandableCard(
    createdOn: Date,
    practitionerName: String,
    listOfItems: List<String>,
    isBulleted: Boolean,
    extraInfoComposable: (@Composable () -> Unit)? = null
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp)) {
            Header(createdOn = createdOn, expanded = expanded) {
                expanded = !expanded
            }

            Text(
                text = practitionerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = expanded) {
                ExpandedContent(
                    listOfItems = listOfItems,
                    isBulleted = isBulleted,
                    extraInfoComposable = extraInfoComposable
                )
            }
        }
    }
}

@Composable
private fun Header(
    createdOn: Date,
    expanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = createdOn.toDayFullMonthYear(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggleExpand
            )
        )
    }
}

@Composable
private fun ExpandedContent(
    listOfItems: List<String>,
    isBulleted: Boolean,
    extraInfoComposable: (@Composable () -> Unit)?
) {
    Column(
        modifier = Modifier.padding(top = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (listOfItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.dash),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                listOfItems.forEach { item ->
                    ItemRow(item = item, isBulleted = isBulleted)
                }
            }

            extraInfoComposable?.invoke()
        }
    }
}

@Composable
private fun ItemRow(item: String, isBulleted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isBulleted) {
            BulletCircle(color = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = item,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}