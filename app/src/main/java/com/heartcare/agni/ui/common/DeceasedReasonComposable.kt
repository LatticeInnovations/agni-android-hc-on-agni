package com.heartcare.agni.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.DeceasedReason
import com.heartcare.agni.data.local.enums.DeceasedReason.Companion.getDeceasedReasonList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeceasedReasonComposable(
    selectedReason: String,
    dismiss: () -> Unit,
    updatedReasons: (String) -> Unit
) {
    val selectedDeceasedReason = remember { mutableStateListOf<String>() }

    var otherReason by remember { mutableStateOf("") }
    var isOtherError by remember { mutableStateOf(false) }
    var isLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            selectedReason.split(",").forEach { reason ->
                if (getDeceasedReasonList().contains(reason)) selectedDeceasedReason.add(reason)
                else if (reason.isNotBlank()) {
                    if (otherReason.isBlank()) {
                        selectedDeceasedReason.add(DeceasedReason.OTHERS.reason)
                        otherReason = reason
                    } else {
                        otherReason += ",$reason"
                    }
                }
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = {
            dismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier
            .statusBarsPadding(),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.person_deceased_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.select_decease_reason),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            getDeceasedReasonList().forEach { reason ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedDeceasedReason.contains(reason),
                        onCheckedChange = { checked ->
                            if (checked) selectedDeceasedReason.add(reason)
                            else {
                                selectedDeceasedReason.remove(reason)
                                if (reason == DeceasedReason.OTHERS.reason) {
                                    otherReason = ""
                                    isOtherError = false
                                }
                            }
                        }
                    )
                    Text(
                        text = reason
                    )
                }
            }
            AnimatedVisibility(
                visible = selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)
            ) {
                CustomTextFieldWithLength(
                    value = otherReason,
                    label = null,
                    placeholder = stringResource(R.string.please_specify),
                    weight = 1f,
                    maxLength = 50,
                    isError = isOtherError,
                    error = stringResource(R.string.specify_reason),
                    keyboardType = KeyboardType.Text,
                    keyboardCapitalization = KeyboardCapitalization.Sentences
                ) {
                    otherReason = it
                    isOtherError = selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)
                            && otherReason.isBlank()
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = dismiss
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(Modifier.width(16.dp))
                TextButton(
                    onClick = {
                        if (selectedDeceasedReason.contains(DeceasedReason.OTHERS.reason)) {
                            selectedDeceasedReason.remove(DeceasedReason.OTHERS.reason)
                            selectedDeceasedReason.add(otherReason.trim())
                        }
                        updatedReasons(selectedDeceasedReason.joinToString(","))
                    },
                    enabled = selectedDeceasedReason.isNotEmpty() && !(selectedDeceasedReason.contains(
                        DeceasedReason.OTHERS.reason
                    )
                            && otherReason.isBlank())
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}