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
import com.heartcare.agni.data.local.enums.FruitJuiceFrequency.Companion.listOfFruitJuiceFrequency
import com.heartcare.agni.data.local.enums.SoftDrinkFrequency.Companion.listOfSoftDrinkFrequency
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.historyandtests.risk.view.Header

@Composable
fun SugarQuestions(
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
            Header(stringResource(R.string.sugars))
            SugarQuestionOne(viewModel)
            SugarQuestionTwo(viewModel)
        }
    }
}

@Composable
private fun SugarQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.sugars_question_one))
    DropdownComposable(
        value = viewModel.softDrinkFrequency,
        updateValue = { viewModel.softDrinkFrequency = it },
        dropdownList = listOfSoftDrinkFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}

@Composable
private fun SugarQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.sugars_question_two))
    DropdownComposable(
        value = viewModel.juiceFrequency,
        updateValue = { viewModel.juiceFrequency = it },
        dropdownList = listOfFruitJuiceFrequency(),
        isMandatory = false,
        label = stringResource(R.string.select),
        errorText = ""
    )
}