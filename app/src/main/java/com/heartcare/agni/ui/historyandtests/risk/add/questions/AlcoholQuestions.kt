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
import com.heartcare.agni.ui.historyandtests.risk.view.Header
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun AlcoholQuestions(
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
            Header(stringResource(R.string.alcohol))
            AlcoholQuestionOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.consumedWithin30Days == YesNoEnum.YES.display
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AlcoholQuestionTwo(viewModel)
                    AlcoholQuestionThree(viewModel)
                    AlcoholQuestionFour(viewModel)
                }
            }
        }
    }
}

@Composable
private fun AlcoholQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.alcohol_question_one))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YesNoEnum.listOfDisplay().forEach { option ->
            RadioButtonRow(
                selected = viewModel.consumedWithin30Days,
                option = option,
                onClick = {
                    if (option == viewModel.consumedWithin30Days) viewModel.consumedWithin30Days = ""
                    else viewModel.consumedWithin30Days = option
                    if (viewModel.consumedWithin30Days == YesNoEnum.NO.display
                        || viewModel.consumedWithin30Days.isBlank()) viewModel.resetAlcoholValues()
                }
            )
        }
    }
}

@Composable
private fun AlcoholQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.alcohol_question_two))
    CustomTextField(
        value = viewModel.alcoholQ1,
        label = stringResource(R.string.number_of_times),
        weight = 1f,
        maxLength = 2,
        isError = viewModel.alcoholQ1Error,
        error = stringResource(R.string.value_in_range, 0, 30),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.alcoholQ1 = it
            viewModel.alcoholQ1Error =
                viewModel.alcoholQ1.isBlank() || viewModel.alcoholQ1.toInt() !in 0..30
        }
    )
}

@Composable
private fun AlcoholQuestionThree(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.alcohol_question_three))
    CustomTextField(
        value = viewModel.alcoholQ2,
        label = stringResource(R.string.number_of_times),
        weight = 1f,
        maxLength = 2,
        isError = viewModel.alcoholQ2Error,
        error = stringResource(R.string.value_in_range, 0, 20),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.alcoholQ2 = it
            viewModel.alcoholQ2Error =
                viewModel.alcoholQ2.isBlank() || viewModel.alcoholQ2.toInt() !in 0..20
        }
    )
}

@Composable
private fun AlcoholQuestionFour(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.alcohol_question_four))
    CustomTextField(
        value = viewModel.alcoholQ3,
        label = stringResource(R.string.number_of_standard_drinks),
        weight = 1f,
        maxLength = 2,
        isError = viewModel.alcoholQ3Error,
        error = stringResource(R.string.value_in_range, 0, 30),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.alcoholQ3 = it
            viewModel.alcoholQ3Error =
                viewModel.alcoholQ3.isBlank() || viewModel.alcoholQ3.toInt() !in 0..30
        }
    )
}