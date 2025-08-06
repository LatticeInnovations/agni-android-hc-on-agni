package com.heartcare.agni.ui.historyandtests.risk.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskFactorsViewScreen(
    navController: NavController
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Date().toPrescriptionDate(),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TobaccoComposable()
                    AlcoholComposable()
                    FruitsAndVegetablesComposable()
                    PhysicalActivityComposable()
                    SaltComposable()
                    FatsAndOilsComposable()
                    SugarsComposable()
                    DiningOutComposable()
                }
            }
        }
    )
}

@Composable
private fun TobaccoComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.tobacco))
            DetailsComposable(
                label = stringResource(R.string.tobacco_question_one),
                answer = "Yes"
            )
            DetailsComposable(
                label = stringResource(R.string.tobacco_question_two),
                answer = "Cigarettes"
            )
            DetailsComposable(
                label = stringResource(R.string.tobacco_question_three),
                answer = "2"
            )
            DetailsComposable(
                label = stringResource(R.string.tobacco_question_four),
                answer = "24 year"
            )
            DetailsComposable(
                label = stringResource(R.string.tobacco_question_five),
                answer = "Yes"
            )
        }
    }
}

@Composable
private fun AlcoholComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.alcohol))
            DetailsComposable(
                label = stringResource(R.string.alcohol_question_one),
                answer = "Yes"
            )
            DetailsComposable(
                label = stringResource(R.string.alcohol_question_two),
                answer = "4"
            )
            DetailsComposable(
                label = stringResource(R.string.alcohol_question_three),
                answer = "2"
            )
            DetailsComposable(
                label = stringResource(R.string.alcohol_question_four),
                answer = "3"
            )
        }
    }
}

@Composable
private fun FruitsAndVegetablesComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.fruits_and_vegetable))
            DetailsComposable(
                label = stringResource(R.string.fruits_vegetable_question_one),
                answer = "Yes"
            )
            DetailsComposable(
                label = stringResource(R.string.fruits_vegetable_question_two),
                answer = "4"
            )
            DetailsComposable(
                label = stringResource(R.string.fruits_vegetable_question_three),
                answer = "5"
            )
            DetailsComposable(
                label = stringResource(R.string.fruits_vegetable_question_four),
                answer = "3"
            )
            DetailsComposable(
                label = stringResource(R.string.fruits_vegetable_question_five),
                answer = "2"
            )
        }
    }
}

@Composable
private fun PhysicalActivityComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.physical_activity))
            DetailsComposable(
                label = stringResource(R.string.physical_activity_question_one),
                answer = "Yes"
            )
            DetailsComposable(
                label = stringResource(R.string.physical_activity_question_two),
                answer = "3"
            )
            DetailsComposable(
                label = stringResource(R.string.physical_activity_question_three),
                answer = "4"
            )
            DetailsComposable(
                label = stringResource(R.string.physical_activity_question_four),
                answer = "4"
            )
            DetailsComposable(
                label = stringResource(R.string.physical_activity_question_five),
                answer = "4"
            )
        }
    }
}

@Composable
private fun SaltComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.salt))
            DetailsComposable(
                label = stringResource(R.string.salt_question_one),
                answer = "Too little"
            )
            DetailsComposable(
                label = stringResource(R.string.salt_question_two),
                answer = "--"
            )
            DetailsComposable(
                label = stringResource(R.string.salt_question_three),
                answer = "--"
            )
            DetailsComposable(
                label = stringResource(R.string.salt_question_four),
                answer = "--"
            )
        }
    }
}

@Composable
private fun FatsAndOilsComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.fats_and_oils))
            DetailsComposable(
                label = stringResource(R.string.fats_and_oils_question_one),
                answer = "--"
            )
            DetailsComposable(
                label = stringResource(R.string.fats_and_oils_question_two),
                answer = "--"
            )
        }
    }
}

@Composable
private fun SugarsComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.sugars))
            DetailsComposable(
                label = stringResource(R.string.sugars_question_one),
                answer = "--"
            )
            DetailsComposable(
                label = stringResource(R.string.sugars_question_two),
                answer = "--"
            )
        }
    }
}

@Composable
private fun DiningOutComposable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(stringResource(R.string.dining_out))
            DetailsComposable(
                label = stringResource(R.string.dining_out_question),
                answer = "--"
            )
        }
    }
}

@Composable
private fun Header(
    heading: String
) {
    Text(
        text = heading,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DetailsComposable(
    label: String,
    answer: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}