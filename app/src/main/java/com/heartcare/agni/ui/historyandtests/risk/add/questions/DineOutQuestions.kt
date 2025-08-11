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
import com.heartcare.agni.data.local.enums.KnowEnum
import com.heartcare.agni.data.local.enums.KnowEnum.Companion.knowOptions
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.RadioButtonRow
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.historyandtests.risk.view.Header
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun DineOutQuestions(
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
            Header(stringResource(R.string.dining_out))
            DineOutQuestionOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.eatsOut == KnowEnum.KNOW.display
            ) {
                CustomTextField(
                    value = viewModel.mealsPerWeek,
                    label = stringResource(R.string.number_of_meals_per_week),
                    weight = 1f,
                    maxLength = 2,
                    isError = viewModel.mealsPerWeekError,
                    error = stringResource(R.string.value_in_range, 0, 50),
                    keyboardType = KeyboardType.Number,
                    keyboardCapitalization = KeyboardCapitalization.None,
                    singleLine = true,
                    updateValue = {
                        if (it.matches(onlyNumbers) || it.isEmpty()) viewModel.mealsPerWeek = it
                        viewModel.mealsPerWeekError =
                            viewModel.mealsPerWeek.isBlank() || viewModel.mealsPerWeek.toInt() !in 0..50
                    }
                )
            }
        }
    }
}

@Composable
private fun DineOutQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.dining_out_question))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        knowOptions().forEach { option ->
            RadioButtonRow(
                selected = viewModel.eatsOut,
                option = option,
                onClick = {
                    viewModel.eatsOut = option
                    if (viewModel.eatsOut == YesNoEnum.NO.display
                        || viewModel.eatsOut.isBlank()) {
                        viewModel.mealsPerWeek = ""
                        viewModel.mealsPerWeekError = false
                    }
                }
            )
        }
    }
}