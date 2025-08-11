package com.heartcare.agni.ui.historyandtests.risk.add.questions

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
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.SaltAmountEnum.Companion.listOfSaltAmount
import com.heartcare.agni.data.local.enums.SaltFrequencyEnum.Companion.listOfSaltFrequency
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.common.Header

@Composable
fun SaltQuestions(
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
            Header(stringResource(R.string.salt))
            SaltQuestionOne(viewModel)
            SaltQuestionTwo(viewModel)
            SaltQuestionThree(viewModel)
            SaltQuestionFour(viewModel)
        }
    }
}

@Composable
private fun SaltQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.salt_question_one))
    DropdownComposable(
        value = viewModel.saltAmount,
        updateValue = { viewModel.saltAmount = it },
        dropdownList = listOfSaltAmount(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}

@Composable
private fun SaltQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.salt_question_two))
    DropdownComposable(
        value = viewModel.saltAddCooking,
        updateValue = { viewModel.saltAddCooking = it },
        dropdownList = listOfSaltFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}

@Composable
private fun SaltQuestionThree(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.salt_question_three))
    DropdownComposable(
        value = viewModel.saltAddMeal,
        updateValue = { viewModel.saltAddMeal = it },
        dropdownList = listOfSaltFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}

@Composable
private fun SaltQuestionFour(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.salt_question_four))
    DropdownComposable(
        value = viewModel.saltProcessedFood,
        updateValue = { viewModel.saltProcessedFood = it },
        dropdownList = listOfSaltFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}