package com.heartcare.agni.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownComposable(
    value: String,
    updateValue: (String) -> Unit,
    label: String,
    dropdownList: List<String>,
    errorText: String,
    isMandatory: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            expanded = !expanded
                        }
                    }
                }
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            supportingText = {
                if (isError)
                    Text(errorText)
            },
            isError = isError
        )

        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .heightIn(0.dp, 300.dp),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                isError = value.isBlank() && isMandatory
            },
        ) {
            dropdownList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                        isError = false
                        updateValue(label)
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}