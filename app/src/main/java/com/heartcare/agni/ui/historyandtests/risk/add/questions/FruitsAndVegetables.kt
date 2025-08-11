package com.heartcare.agni.ui.historyandtests.risk.add.questions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.RadioButtonRow
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.common.Header
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun FruitsAndVegetables(
    viewModel: AddRiskFactorViewModel
) {
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
            FruitsAndVegetablesOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.consumptionInWeek == YesNoEnum.YES.display
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FruitsAndVegetablesTwo(viewModel)
                    FruitsAndVegetablesThree(viewModel)
                    FruitsAndVegetablesFour(viewModel)
                    FruitsAndVegetablesFive(viewModel)
                }
            }
        }
    }
}

@Composable
private fun FruitsAndVegetablesOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fruits_vegetable_question_one))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YesNoEnum.listOfDisplay().forEach { option ->
            RadioButtonRow(
                selected = viewModel.consumptionInWeek,
                option = option,
                onClick = {
                    if (option == viewModel.consumptionInWeek) viewModel.consumptionInWeek = ""
                    else viewModel.consumptionInWeek = option
                    if (viewModel.consumptionInWeek == YesNoEnum.NO.display
                        || viewModel.consumptionInWeek.isBlank()) viewModel.resetFruitsVegetablesValues()
                }
            )
        }
    }
}

@Composable
private fun FruitsAndVegetablesTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fruits_vegetable_question_two))
    CustomTextField(
        value = viewModel.fruitsDays,
        label = stringResource(R.string.days_in_week_fruits),
        weight = 1f,
        maxLength = 1,
        isError = viewModel.fruitsDaysError,
        error = stringResource(R.string.value_in_range, 0, 7),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isEmpty()) viewModel.fruitsDays = it
            viewModel.fruitsDaysError =
                viewModel.fruitsDays.isBlank() || viewModel.fruitsDays.toInt() !in 0..7
        }
    )
}

@Composable
private fun FruitsAndVegetablesThree(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fruits_vegetable_question_three))
    CustomTextField(
        value = viewModel.vegetablesDays,
        label = stringResource(R.string.days_in_week_vegetables),
        weight = 1f,
        maxLength = 1,
        isError = viewModel.vegetablesDaysError,
        error = stringResource(R.string.value_in_range, 0, 7),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isEmpty()) viewModel.vegetablesDays = it
            viewModel.vegetablesDaysError =
                viewModel.vegetablesDays.isBlank() || viewModel.vegetablesDays.toInt() !in 0..7
        }
    )
}

@Composable
private fun FruitsAndVegetablesFour(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fruits_vegetable_question_four))
    CustomTextField(
        value = viewModel.fruitServings,
        label = stringResource(R.string.fruits_serving),
        weight = 1f,
        maxLength = 2,
        isError = viewModel.fruitServingsError,
        error = stringResource(R.string.value_in_range, 0, 20),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isEmpty()) viewModel.fruitServings = it
            viewModel.fruitServingsError =
                viewModel.fruitServings.isBlank() || viewModel.fruitServings.toInt() !in 0..20
        }
    )
}

@Composable
private fun FruitsAndVegetablesFive(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fruits_vegetable_question_five))
    CustomTextField(
        value = viewModel.vegetableServings,
        label = stringResource(R.string.vegetable_serving),
        weight = 1f,
        maxLength = 2,
        isError = viewModel.vegetableServingsError,
        error = stringResource(R.string.value_in_range, 0, 20),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isEmpty()) viewModel.vegetableServings = it
            viewModel.vegetableServingsError =
                viewModel.vegetableServings.isBlank() || viewModel.vegetableServings.toInt() !in 0..20
        }
    )
}