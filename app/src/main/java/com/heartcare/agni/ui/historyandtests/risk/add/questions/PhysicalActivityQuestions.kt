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
fun PhysicalActivityQuestions(
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
            Header(stringResource(R.string.physical_activity))
            PhysicalActivityQuestionOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.weeklyEngagement == YesNoEnum.YES.display
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PhysicalActivityQuestionTwo(viewModel)
                    PhysicalActivityQuestionThree(viewModel)
                    PhysicalActivityQuestionFour(viewModel)
                    PhysicalActivityQuestionFive(viewModel)
                }
            }
        }
    }
}

@Composable
private fun PhysicalActivityQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.physical_activity_question_one))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YesNoEnum.listOfDisplay().forEach { option ->
            RadioButtonRow(
                selected = viewModel.weeklyEngagement,
                option = option,
                onClick = {
                    if (option == viewModel.weeklyEngagement) viewModel.weeklyEngagement = ""
                    else viewModel.weeklyEngagement = option
                    if (viewModel.weeklyEngagement == YesNoEnum.NO.display
                        || viewModel.weeklyEngagement.isBlank()) viewModel.resetPhysicalActivityValues()
                }
            )
        }
    }
}

@Composable
private fun PhysicalActivityQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.physical_activity_question_two))
    CustomTextField(
        value = viewModel.vigorousDays,
        label = stringResource(R.string.number_of_days),
        weight = 1f,
        maxLength = 1,
        isError = viewModel.vigorousDaysError,
        error = stringResource(R.string.value_in_range, 0, 7),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.vigorousDays = it
            viewModel.vigorousDaysError =
                viewModel.vigorousDays.isBlank() || viewModel.vigorousDays.toInt() !in 0..7
        }
    )
}

@Composable
private fun PhysicalActivityQuestionThree(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.physical_activity_question_three))
    CustomTextField(
        value = viewModel.moderateDays,
        label = stringResource(R.string.number_of_days),
        weight = 1f,
        maxLength = 1,
        isError = viewModel.moderateDaysError,
        error = stringResource(R.string.value_in_range, 0, 7),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.moderateDays = it
            viewModel.moderateDaysError =
                viewModel.moderateDays.isBlank() || viewModel.moderateDays.toInt() !in 0..7
        }
    )
}

@Composable
private fun PhysicalActivityQuestionFour(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.physical_activity_question_four))
    CustomTextField(
        value = viewModel.vigorousTime,
        label = stringResource(R.string.duration_in_minutes),
        weight = 1f,
        maxLength = 4,
        isError = viewModel.vigorousTimeError,
        error = stringResource(R.string.value_in_range, 0, 1440),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.vigorousTime = it
            viewModel.vigorousTimeError =
                viewModel.vigorousTime.isBlank() || viewModel.vigorousTime.toInt() !in 0..1440
        }
    )
}

@Composable
private fun PhysicalActivityQuestionFive(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.physical_activity_question_five))
    CustomTextField(
        value = viewModel.moderateTime,
        label = stringResource(R.string.duration_in_minutes),
        weight = 1f,
        maxLength = 4,
        isError = viewModel.moderateTimeError,
        error = stringResource(R.string.value_in_range, 0, 1440),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.moderateTime = it
            viewModel.moderateTimeError =
                viewModel.moderateTime.isBlank() || viewModel.moderateTime.toInt() !in 0..1440
        }
    )
}