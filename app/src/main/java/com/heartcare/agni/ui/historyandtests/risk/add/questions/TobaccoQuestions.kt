package com.heartcare.agni.ui.historyandtests.risk.add.questions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.TobaccoProduct
import com.heartcare.agni.data.local.enums.TobaccoProduct.Companion.listOfTobaccoProducts
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.common.RadioButtonRow
import com.heartcare.agni.ui.historyandtests.risk.add.AddRiskFactorViewModel
import com.heartcare.agni.ui.historyandtests.risk.add.Question
import com.heartcare.agni.ui.historyandtests.risk.view.Header
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

@Composable
fun TobaccoQuestions(
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
            Header(stringResource(R.string.tobacco))
            TobaccoQuestionOne(viewModel)
            AnimatedVisibility(
                visible = viewModel.useTobacco == YesNoEnum.YES.display
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TobaccoQuestionTwo(viewModel)
                    TobaccoQuestionThree(viewModel)
                    TobaccoQuestionFour(viewModel)
                    TobaccoQuestionFive(viewModel)
                }
            }
        }
    }
}

@Composable
private fun TobaccoQuestionOne(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.tobacco_question_one))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YesNoEnum.listOfDisplay().forEach { option ->
            RadioButtonRow(
                selected = viewModel.useTobacco,
                option = option,
                onClick = {
                    if (option == viewModel.useTobacco) viewModel.useTobacco = ""
                    else viewModel.useTobacco = option
                    if (viewModel.useTobacco == YesNoEnum.NO.display
                        || viewModel.useTobacco.isBlank()) viewModel.resetTobaccoValues()
                }
            )
        }
    }
}

@Composable
private fun TobaccoQuestionTwo(
    viewModel: AddRiskFactorViewModel
) {
    Column {
        Question(stringResource(R.string.tobacco_question_two))
        Spacer(Modifier.height(12.dp))
        DropdownComposable(
            value = viewModel.tobaccoType,
            updateValue = {
                viewModel.tobaccoType = it
                viewModel.otherTobaccoError = false
                viewModel.otherTobacco = ""
            },
            label = stringResource(R.string.tobacco_product),
            dropdownList = listOfTobaccoProducts(),
            errorText = "",
            isMandatory = true
        )
        AnimatedVisibility(
            visible = viewModel.tobaccoType == TobaccoProduct.OTHER.display
        ) {
            CustomTextField(
                value = viewModel.otherTobacco,
                label = stringResource(R.string.other_tobacco_product),
                weight = 1f,
                maxLength = 200,
                isError = viewModel.otherTobaccoError,
                error = stringResource(R.string.other_is_required),
                keyboardType = KeyboardType.Text,
                keyboardCapitalization = KeyboardCapitalization.Sentences,
                singleLine = false,
                updateValue = {
                    viewModel.otherTobacco = it
                    viewModel.otherTobaccoError = it.isBlank()
                }
            )
        }
    }
}

@Composable
private fun TobaccoQuestionThree(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.tobacco_question_three))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            value = viewModel.tobaccoQuantity,
            label = stringResource(R.string.quantity),
            weight = 1f,
            maxLength = 3,
            isError = viewModel.tobaccoQuantityError,
            error = stringResource(R.string.value_in_range, 0, 100),
            keyboardType = KeyboardType.Number,
            keyboardCapitalization = KeyboardCapitalization.None,
            singleLine = true,
            updateValue = {
                if (it.matches(onlyNumbers) || it.isBlank()) viewModel.tobaccoQuantity = it
                viewModel.tobaccoQuantityError =
                    viewModel.tobaccoQuantity.isBlank() || viewModel.tobaccoQuantity.toInt() !in 0..100
            },
            modifier = Modifier.weight(7f)
        )
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = viewModel.quantityOptions[viewModel.selectedQuantityOption],
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.swap),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (viewModel.selectedQuantityOption == 0) viewModel.selectedQuantityOption =
                        1
                    else viewModel.selectedQuantityOption = 0
                }
            )
        }
    }
}

@Composable
private fun TobaccoQuestionFour(
    viewModel: AddRiskFactorViewModel
) {
    val context = LocalContext.current
    Question(stringResource(R.string.tobacco_question_four))
    CustomTextField(
        value = viewModel.startAge,
        label = stringResource(R.string.enter_age),
        weight = 1f,
        maxLength = 3,
        isError = viewModel.startAgeError,
        error = viewModel.startAgeErrorMsg,
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.None,
        singleLine = true,
        updateValue = {
            if (it.matches(onlyNumbers) || it.isBlank()) viewModel.startAge = it
            viewModel.startAgeError =
                viewModel.startAge.isBlank() || viewModel.startAge.toInt() !in 0..viewModel.patient!!.birthDate.toTimeInMilli()
                    .toAge()
            viewModel.startAgeErrorMsg =
                if (viewModel.startAge.isNotBlank() && viewModel.startAge.toInt() > viewModel.patient!!.birthDate.toTimeInMilli()
                        .toAge()
                ) {
                    context.getString(R.string.age_cannot_be_greater)
                } else context.getString(R.string.value_in_range, 0, 150)
        }
    )
}

@Composable
private fun TobaccoQuestionFive(
    viewModel: AddRiskFactorViewModel
) {
    Question(stringResource(R.string.tobacco_question_five) + " *")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YesNoEnum.listOfDisplay().forEach { option ->
            RadioButtonRow(
                selected = viewModel.willingToQuit,
                option = option,
                onClick = { viewModel.willingToQuit = option }
            )
        }
    }
}