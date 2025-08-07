package com.heartcare.agni.ui.historyandtests.risk.add.questions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.heartcare.agni.data.local.enums.FatFrequency.Companion.listOfFatFrequency
import com.heartcare.agni.data.local.enums.FatType
import com.heartcare.agni.data.local.enums.FatType.Companion.listOfFatType
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.historyandtests.risk.view.Header

@Composable
fun FatAndOilQuestions(
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
            Header(stringResource(R.string.fats_and_oils))
            FatAndOilQuestionOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.oilUsed == FatType.OTHERS.display
            ) {
                CustomTextField(
                    value = viewModel.otherFatAndOils,
                    label = stringResource(R.string.other_fat_and_oil),
                    weight = 1f,
                    maxLength = 200,
                    isError = viewModel.otherFatAndOilsError,
                    error = stringResource(R.string.other_is_required),
                    keyboardType = KeyboardType.Text,
                    keyboardCapitalization = KeyboardCapitalization.Sentences,
                    singleLine = false,
                    updateValue = {
                        viewModel.otherFatAndOils = it
                        viewModel.otherFatAndOilsError = it.isBlank()
                    }
                )
            }
            FatAndOilQuestionTwo(viewModel)
        }
    }
}

@Composable
private fun FatAndOilQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fats_and_oils_question_one))
    DropdownComposable(
        value = viewModel.oilUsed,
        updateValue = {
            viewModel.oilUsed = it
            viewModel.otherFatAndOils = ""
            viewModel.otherFatAndOilsError = false
        },
        dropdownList = listOfFatType(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}

@Composable
private fun FatAndOilQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.fats_and_oils_question_two))
    DropdownComposable(
        value = viewModel.fatFoodFrequency,
        updateValue = { viewModel.fatFoodFrequency = it },
        dropdownList = listOfFatFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}