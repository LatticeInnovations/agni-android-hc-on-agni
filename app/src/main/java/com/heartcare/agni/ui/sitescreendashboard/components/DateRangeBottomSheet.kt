package com.heartcare.agni.ui.sitescreendashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.DateRangeEnum
import com.heartcare.agni.data.local.enums.DateRangeEnum.Companion.getDateRangeOptions
import com.heartcare.agni.ui.common.DatePickerDialog
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeBottomSheet(
    selected: String,
    dateRangeStart: Date,
    dateRangeEnd: Date,
    onDismissRequest: () -> Unit,
    onSaveClick: (rangeType: String, startDate: Date?, endDate: Date?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedRange by remember { mutableStateOf(selected) }
    var startDate by remember { mutableStateOf(dateRangeStart) }
    var endDate by remember { mutableStateOf(dateRangeEnd) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.date_range),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            getDateRangeOptions().forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRange = option }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedRange == option,
                        onClick = { selectedRange = option }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Custom Range Selection
            if (selectedRange == DateRangeEnum.CUSTOM_RANGE.label) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = dateFormatter.format(startDate),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            label = { Text(stringResource(R.string.start_date), style = MaterialTheme.typography.bodySmall) },
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.today_calendar), contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent)
                                .clickable { showStartDatePicker = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = dateFormatter.format(endDate),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            label = { Text(stringResource(R.string.end_date), style = MaterialTheme.typography.bodySmall) },
                            trailingIcon = {
                                Icon(painter = painterResource(id = R.drawable.today_calendar), contentDescription = null, modifier = Modifier.size(24.dp))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Transparent)
                                .clickable { showEndDatePicker = true }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val customRangeTxt =  stringResource(R.string.custom_range)
            Button(
                onClick = {
                    onSaveClick(
                        selectedRange,
                        if (selectedRange == customRangeTxt) startDate else null,
                        if (selectedRange == customRangeTxt) endDate else null
                    )
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val endMillis = endDate.time
                    // Allow start date to be earlier than or exactly the same as end date
                    return utcTimeMillis <= endMillis
                }
            },
            initialSelectedDate = startDate,
            dismissBtnText = stringResource(R.string.cancel),
            confirmBtnText = stringResource(R.string.ok),
            dismiss = { showStartDatePicker = false },
            confirm = { selected ->
                startDate = Date(selected.toTodayStartDate())
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val startMillis = startDate.time
                    // Allow end date to be later than or exactly the same as start date
                    return utcTimeMillis >= startMillis
                }
            },
            initialSelectedDate = endDate,
            dismissBtnText = stringResource(R.string.cancel),
            confirmBtnText = stringResource(R.string.ok),
            dismiss = { showEndDatePicker = false },
            confirm = { selected ->
                endDate = Date(selected.toEndOfDay())
                showEndDatePicker = false
            }
        )
    }
}