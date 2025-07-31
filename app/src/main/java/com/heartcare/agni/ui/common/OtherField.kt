package com.heartcare.agni.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R

@Composable
fun OtherField(
    isVisible: Boolean,
    value: String,
    isError: Boolean,
    errorMessage: String = "",
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        CustomTextFieldWithLength(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = value,
            placeholder = stringResource(R.string.please_specify_mandatory),
            weight = 1f,
            maxLength = maxLength,
            isError = isError,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            updateValue = onValueChange,
            error = errorMessage
        )
    }
}