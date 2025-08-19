package com.heartcare.agni.ui.diagnosis.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CheckBoxRow
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.ExpandableBottomNavLayout
import com.heartcare.agni.ui.common.Loader
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.DIAGNOSIS_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddDiagnosisScreen(
    navController: NavController,
    viewModel: AddDiagnosisViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    HandleLaunchedEffect(viewModel, navController)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = { SearchableTopAppBar(viewModel, navController, focusManager) },
        content = { paddingValues ->
            DiagnosisContent(
                viewModel = viewModel,
                paddingValues = paddingValues
            )
        }
    )

    DiagnosisBottomSection(
        viewModel = viewModel,
        navController = navController,
        coroutineScope = coroutineScope,
        focusManager = focusManager
    )

    if (viewModel.clearAllConfirmDialog) {
        ClearAllConfirmDialog(viewModel)
    }
}

@Composable
private fun HandleLaunchedEffect(viewModel: AddDiagnosisViewModel, navController: NavController) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(PATIENT)
                ?.let { viewModel.patient = it }
            viewModel.isLaunched = true
        }
    }
}

@Composable
private fun DiagnosisContent(
    viewModel: AddDiagnosisViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (viewModel.isSearching) {
                DiagnosisSearchList(viewModel)
            } else {
                FrequentDiagnosisList(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DiagnosisBottomSection(
    viewModel: AddDiagnosisViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
    focusManager: FocusManager
) {
    Box(
        modifier = if (viewModel.bottomNavExpanded) {
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                .clickable(enabled = false) {}
        } else {
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        },
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedContent(
            targetState = viewModel.lastDiagnosis != null && viewModel.selectedDiagnosis.isEmpty(),
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { showRetainButton ->
            if (showRetainButton) {
                RetainDiagnosisButton()
            } else {
                ExpandableBottomNavLayout(
                    selectedList = viewModel.selectedDiagnosis,
                    bottomNavExpanded = viewModel.bottomNavExpanded,
                    onExpandToggle = {
                        viewModel.bottomNavExpanded = !viewModel.bottomNavExpanded
                        focusManager.clearFocus()
                    },
                    onSave = {
                        viewModel.addDiagnosis {
                            coroutineScope.launch {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    DIAGNOSIS_SAVED,
                                    true
                                )
                                navController.navigateUp()
                            }
                        }
                    },
                    onClearAll = { viewModel.clearAllConfirmDialog = true },
                    onRemoveItem = { diagnosis ->
                        viewModel.selectedDiagnosis -= diagnosis
                        if (viewModel.selectedDiagnosis.isEmpty()) {
                            viewModel.bottomNavExpanded = false
                        }
                    },
                    saveBtnText = stringResource(R.string.save_diagnosis),
                    title = stringResource(R.string.diagnosis)
                )
            }
        }
    }
}

@Composable
private fun RetainDiagnosisButton() {
    Surface(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth(),
        color = if (isSystemInDarkTheme()) Black else White
    ) {
        FilledTonalButton(
            onClick = { /* retain diagnosis */ },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.retain_diagnosis))
        }
    }
}

@Composable
private fun ClearAllConfirmDialog(viewModel: AddDiagnosisViewModel) {
    CustomDialog(
        canBeDismissed = false,
        title = stringResource(R.string.discard_diagnosis_dialog_title),
        text = stringResource(R.string.discard_diagnosis_dialog_description),
        dismissBtnText = stringResource(R.string.no_go_back),
        confirmBtnText = stringResource(R.string.yes_discard),
        dismiss = { viewModel.clearAllConfirmDialog = false },
        confirm = {
            viewModel.selectedDiagnosis = emptyList()
            viewModel.bottomNavExpanded = false
            viewModel.clearAllConfirmDialog = false
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableTopAppBar(
    viewModel: AddDiagnosisViewModel,
    navController: NavController,
    focusManager: FocusManager
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            TextField(
                value = viewModel.searchQuery,
                onValueChange = {
                    if (it.length <= 100) viewModel.searchQuery = it
                },
                placeholder = {
                    Text(stringResource(R.string.diagnosis_search_text))
                },
                modifier = Modifier
                    .offset(x = (-12).dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // search function run
                        focusManager.clearFocus()
                        viewModel.searchDiagnosis()
                    }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.searchQuery = ""
                                viewModel.isSearching = false
                                viewModel.searchResults = emptyList()
                            }
                        ) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "BACK_ICON"
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun DiagnosisSearchList(
    viewModel: AddDiagnosisViewModel
) {
    if (viewModel.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Loader()
        }
    } else {
        if (viewModel.searchResults.isEmpty()) {
            Text(
                text = stringResource(R.string.no_results_found),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = stringResource(R.string.results_count, viewModel.searchResults.size),
                style = MaterialTheme.typography.labelLarge
            )
            viewModel.searchResults.forEach { diagnosis ->
                CheckBoxRow(
                    isChecked = viewModel.selectedDiagnosis.contains(diagnosis),
                    onCheckedChange = { checked ->
                        if (checked) viewModel.selectedDiagnosis += listOf(diagnosis)
                        else viewModel.selectedDiagnosis -= listOf(diagnosis)
                    },
                    label = diagnosis
                )
            }
        }
    }
}

@Composable
private fun FrequentDiagnosisList(
    viewModel: AddDiagnosisViewModel
) {
    if (viewModel.frequentlyDiagnosedList.isNotEmpty()) {
        Text(
            text = stringResource(R.string.frequently_diagnosed),
            style = MaterialTheme.typography.labelLarge
        )
        viewModel.frequentlyDiagnosedList.forEach { diagnosis ->
            CheckBoxRow(
                isChecked = viewModel.selectedDiagnosis.contains(diagnosis),
                onCheckedChange = { checked ->
                    if (checked) viewModel.selectedDiagnosis += listOf(diagnosis)
                    else viewModel.selectedDiagnosis -= listOf(diagnosis)
                },
                label = diagnosis
            )
        }
    }
}