package com.heartcare.agni.ui.historyandtests.family

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.FamilyHistoryEnum.Companion.getFamilyHistoryConditionList
import com.heartcare.agni.data.local.enums.YesNoEnum.Companion.listOfDisplay
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CheckBoxRow
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.FAMILY_HISTORY_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyHistoryScreen(
    navController: NavController,
    viewModel: AddFamilyHistoryViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.add_family_history),
                        style = MaterialTheme.typography.titleLarge
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
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    text = stringResource(R.string.family_history_header_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = if (viewModel.showAgeQuestionCard) 140.dp else 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    getFamilyHistoryConditionList().forEach { familyHistory ->
                        val isChecked = familyHistory in viewModel.selectedFamilyHistory

                        CheckBoxRow(
                            isChecked = isChecked,
                            onCheckedChange = { checked ->
                                updateList(checked, familyHistory, viewModel)
                            },
                            label = familyHistory
                        )
                    }
                }
            }
        }
    )
    AgeQuestionOverlay(viewModel, coroutineScope, navController)
}

private fun updateList(
    checked: Boolean,
    familyHistory: String,
    viewModel: AddFamilyHistoryViewModel
) {
    if (checked) {
        viewModel.selectedFamilyHistory += familyHistory
    } else {
        viewModel.selectedFamilyHistory -= familyHistory
    }
    if (viewModel.selectedFamilyHistory.isNotEmpty()) {
        if (!viewModel.showAgeQuestionCard) {
            viewModel.showAgeQuestionCard = true
            viewModel.isAgeQuestionExpanded = true
        }
    } else {
        viewModel.showAgeQuestionCard = false
        viewModel.ageAnswer = ""
    }
}

@Composable
private fun AgeQuestionOverlay(
    viewModel: AddFamilyHistoryViewModel,
    coroutineScope: CoroutineScope,
    navController: NavController
) {
    Box(
        modifier = if (!viewModel.isAgeQuestionExpanded) Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        else Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.imePadding()
        ) {
            AnimatedVisibility(
                visible = viewModel.showAgeQuestionCard
            ) {
                AgeQuestionComposable(viewModel)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {
                Button(
                    onClick = {
                        // save family history
                        coroutineScope.launch {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                FAMILY_HISTORY_SAVED,
                                true
                            )
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun AgeQuestionComposable(
    viewModel: AddFamilyHistoryViewModel
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                color = if (isSystemInDarkTheme()) Black else White
            )
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 12.dp, end = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.occurence_age_question),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.isAgeQuestionExpanded = !viewModel.isAgeQuestionExpanded
                }
            ) {
                Icon(
                    imageVector = if (viewModel.isAgeQuestionExpanded) Icons.Default.KeyboardArrowDown
                    else Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = viewModel.isAgeQuestionExpanded
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOfDisplay().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    viewModel.ageAnswer = option
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == viewModel.ageAnswer,
                            onClick = {
                                viewModel.ageAnswer = option
                            }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}