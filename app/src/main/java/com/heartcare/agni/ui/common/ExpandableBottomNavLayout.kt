package com.heartcare.agni.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableBottomNavLayout(
    selectedList: List<String>,
    bottomNavExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onSave: () -> Unit,
    onClearAll: () -> Unit,
    onRemoveItem: (String) -> Unit,
    saveBtnText: String,
    title: String
) {
    val rotationState by animateFloatAsState(
        targetValue = if (bottomNavExpanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )

    AnimatedVisibility(
        visible = selectedList.isNotEmpty(),
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            AnimatedVisibility(bottomNavExpanded) {
                ExpandedBottomNavCard(
                    title = title,
                    selectedDiagnosis = selectedList,
                    onClose = onExpandToggle,
                    onClearAll = onClearAll,
                    onRemoveDiagnosis = onRemoveItem
                )
            }

            Surface(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shadowElevation = 15.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onExpandToggle()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "${selectedList.size} selected",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "ARROW_UP",
                            modifier = Modifier.rotate(rotationState)
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = saveBtnText)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedBottomNavCard(
    title: String,
    selectedDiagnosis: List<String>,
    onClose: () -> Unit,
    onClearAll: () -> Unit,
    onRemoveDiagnosis: (String) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "CLEAR_ICON"
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onClearAll) {
                    Text(text = stringResource(id = R.string.clear_all))
                }
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.heightIn(0.dp, 450.dp)
            ) {
                items(selectedDiagnosis) { diagnosis ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = diagnosis,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onRemoveDiagnosis(diagnosis) }) {
                                Icon(
                                    painterResource(R.drawable.delete),
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
