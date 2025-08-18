package com.heartcare.agni.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import kotlinx.coroutines.launch

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
                    selectedList = selectedList,
                    onClose = onExpandToggle,
                    onClearAll = onClearAll,
                    onRemove = onRemoveItem
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
    selectedList: List<String>,
    onClose: () -> Unit,
    onClearAll: () -> Unit,
    onRemove: (String) -> Unit
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
            ListComposable(selectedList, onRemove)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListComposable(
    selectedList: List<String>,
    onRemove: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val list = remember { mutableStateListOf(*selectedList.toTypedArray()) }

    LazyColumn(
        modifier = Modifier.heightIn(0.dp, 450.dp)
    ) {
        items(
            items = list,
            key = { it }
        ) { item ->

            val dismissState = rememberSwipeToDismissBoxState()

            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                LaunchedEffect(item) {
                    list.remove(item)
                    onRemove(item)
                }
            }

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.error)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.onError,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                content = {
                    Column(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        dismissState.dismiss(SwipeToDismissBoxValue.EndToStart)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            )
        }
    }
}