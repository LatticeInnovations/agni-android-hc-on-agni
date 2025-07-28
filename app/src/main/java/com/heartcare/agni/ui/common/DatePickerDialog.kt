package com.heartcare.agni.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectableDates: SelectableDates,
    initialSelectedDate: Date,
    dismissBtnText: String,
    confirmBtnText: String,
    dismiss: () -> Unit,
    confirm: (Date) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.time,
        selectableDates = selectableDates
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = { dismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm(
                        datePickerState.selectedDateMillis?.let { dateInLong ->
                            Date(dateInLong)
                        } ?: Date()
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text(confirmBtnText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dismiss() }
            ) {
                Text(dismissBtnText)
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}