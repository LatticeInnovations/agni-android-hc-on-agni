package com.heartcare.agni.ui.examination.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CheckBoxRow
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.ExpandableBottomNavLayout
import com.heartcare.agni.ui.common.SearchLayout
import com.heartcare.agni.ui.common.SearchResults
import com.heartcare.agni.utils.constants.NavControllerConstants.CAMPAIGN_ID
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.TEST_EXAMINATION_SAVED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTestExaminationScreen(
    navController: NavController,
    viewModel: AddTestExaminationViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.selectedCampaignId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(CAMPAIGN_ID)
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                    viewModel.getTodayExamination(it.id)
                }
            viewModel.isLaunched = true
        }
    }

    BackHandler {
        when {
            viewModel.isSearching -> viewModel.isSearching = false
            viewModel.bottomNavExpanded -> viewModel.bottomNavExpanded = false
            viewModel.isSearchResult -> viewModel.isSearchResult = false
            else -> navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.add_test_and_examinations))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.searchQuery = ""
                            viewModel.isSearching = true
                            viewModel.getPreviousSearch()
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 64.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    viewModel.testExaminationMasterList.forEach { testExamination ->
                        CheckBoxRow(
                            isChecked = viewModel.selectedTestExaminationList.contains(
                                testExamination
                            ),
                            onCheckedChange = { checked ->
                                if (checked) viewModel.selectedTestExaminationList += listOf(
                                    testExamination
                                )
                                else viewModel.selectedTestExaminationList -= listOf(testExamination)
                            },
                            label = "${testExamination.code} ${testExamination.name}"
                        )
                    }
                }
            }
        }
    )

    SearchResults(
        visible = viewModel.isSearchResult,
        resultCount = viewModel.testExaminationSearchList.size,
        query = viewModel.searchQuery,
        onClearClick = {
            viewModel.isSearchResult = false
        },
        onSearchClick = {
            viewModel.tempSearchQuery = viewModel.searchQuery
            viewModel.searchQuery = ""
            viewModel.isSearching = true
            viewModel.getPreviousSearch()
        },
        modifier = Modifier
            .padding(bottom = 64.dp),
        content = {
            viewModel.testExaminationSearchList.forEach { testExamination ->
                CheckBoxRow(
                    isChecked = viewModel.selectedTestExaminationList.contains(testExamination),
                    onCheckedChange = { checked ->
                        if (checked) viewModel.selectedTestExaminationList += listOf(
                            testExamination
                        )
                        else viewModel.selectedTestExaminationList -= listOf(testExamination)
                    },
                    label = "${testExamination.code} ${testExamination.name}"
                )
            }
        }
    )

    TestExaminationBottomBar(navController, viewModel, coroutineScope, focusManager)

    SearchLayout(
        isSearching = viewModel.isSearching,
        searchQuery = viewModel.searchQuery,
        previousSearchList = viewModel.previousSearchList,
        onQueryChange = { viewModel.searchQuery = it },
        onBack = {
            viewModel.searchQuery = viewModel.tempSearchQuery
            viewModel.isSearching = false
        },
        onClear = { viewModel.searchQuery = "" },
        onSearch = { query ->
            viewModel.isSearching = false
            viewModel.isSearchResult = true
            viewModel.insertRecentSearch(query)
            viewModel.getTestExaminationSearchList(query)
        },
        onPreviousSearchClick = { query ->
            viewModel.getTestExaminationSearchList(query)
            viewModel.isSearching = false
            viewModel.isSearchResult = true
            viewModel.searchQuery = query
        }
    )

    if (viewModel.clearAllConfirmDialog) {
        ClearAllConfirmDialog(viewModel)
    }
}


@Composable
private fun TestExaminationBottomBar(
    navController: NavController,
    viewModel: AddTestExaminationViewModel,
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
        ExpandableBottomNavLayout(
            selectedList = viewModel.selectedTestExaminationList.map { testExamination -> "${testExamination.code} ${testExamination.name}" },
            bottomNavExpanded = viewModel.bottomNavExpanded,
            onExpandToggle = {
                viewModel.bottomNavExpanded = !viewModel.bottomNavExpanded
                focusManager.clearFocus()
            },
            onSave = {
                viewModel.saveExamination {
                    coroutineScope.launch {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            TEST_EXAMINATION_SAVED,
                            true
                        )
                        navController.navigateUp()
                    }
                }
            },
            onClearAll = { viewModel.clearAllConfirmDialog = true },
            onRemoveItem = { testExamination ->
                viewModel.selectedTestExaminationList -= viewModel.testExaminationMasterList.first {
                    it.code == testExamination.substringBefore(
                        " "
                    )
                }
                if (viewModel.selectedTestExaminationList.isEmpty()) {
                    viewModel.bottomNavExpanded = false
                }
            },
            saveBtnText = stringResource(R.string.save),
            title = stringResource(R.string.test_and_examinations),
        )
    }
}

@Composable
private fun ClearAllConfirmDialog(viewModel: AddTestExaminationViewModel) {
    CustomDialog(
        canBeDismissed = false,
        title = stringResource(R.string.discard_test_examinations_dialog_title),
        text = stringResource(R.string.discard_test_examinations_dialog_description),
        dismissBtnText = stringResource(R.string.no_go_back),
        confirmBtnText = stringResource(R.string.yes_discard),
        dismiss = { viewModel.clearAllConfirmDialog = false },
        confirm = {
            viewModel.selectedTestExaminationList = emptyList()
            viewModel.bottomNavExpanded = false
            viewModel.clearAllConfirmDialog = false
        }
    )
}