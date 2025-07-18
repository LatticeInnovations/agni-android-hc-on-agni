package com.heartcare.agni.ui.login.pin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.ButtonLoader
import com.heartcare.agni.utils.constants.NavControllerConstants.LOGGED_IN
import com.heartcare.agni.utils.constants.NavControllerConstants.PIN_SCREEN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/***
 * 0 - Login with m-pin
 * 1 - create m-pin
 * 2 - reset m-pin
 */
@Composable
fun PinScreen(
    navController: NavController,
    viewModel: PinViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.screenFlag =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>(PIN_SCREEN) ?: 0
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        topBar = {
            AppBarComposable(viewModel, navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                HeaderTexts(viewModel)

                Spacer(modifier = Modifier.height(60.dp))

                PinFields(viewModel)

                if (viewModel.screenFlag == 0) {
                    InvalidPinText(viewModel)
                    Spacer(modifier = Modifier.height(30.dp))
                    ForgotPinButton(navController)
                }

                Spacer(modifier = Modifier.height(30.dp))

                SubmitPinButton(viewModel, navController, coroutineScope)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBarComposable(
    viewModel: PinViewModel,
    navController: NavController
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            if (viewModel.screenFlag != 0) {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
private fun HeaderTexts(viewModel: PinViewModel) {
    Column {
        Text(
            text = when (viewModel.screenFlag) {
                1 -> stringResource(R.string.create_pin)
                2 -> stringResource(R.string.reset_pin)
                else -> stringResource(R.string.enter_pin)
            },
            style = MaterialTheme.typography.headlineSmall
        )
        if (viewModel.screenFlag == 1) {
            Text(
                text = stringResource(R.string.pin_info),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PinFields(viewModel: PinViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {

            repeat(4) { index ->
                MPinTextField(
                    value = viewModel.pinValues[index].value,
                    modifier = Modifier
                        .width(53.dp)
                        .height(56.dp)
                        .focusRequester(viewModel.focusRequesters[index])
                        .onKeyEvent { keyEvent ->
                            setKeyEvent(
                                keyEvent,
                                viewModel,
                                index
                            )
                        },
                    errorCondition = viewModel.isPinInvalid,
                    maxLength = viewModel.pinLength,
                    next = {
                        if (index < 3) {
                            viewModel.focusRequesters[index + 1].requestFocus()
                        }
                    }
                ) { value ->
                    if (viewModel.pinValues[index].value.isBlank()) {
                        viewModel.pinValues[index].value = value.trim().filter {
                            it.isDigit()
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun MPinTextField(
    value: String,
    modifier: Modifier,
    errorCondition: Boolean,
    maxLength: Int,
    callBack: (() -> Unit)? = null,
    next: (() -> Unit)? = null,
    updateValue: (String) -> Unit
) {
    OutlinedTextField(
        shape = RoundedCornerShape(4.dp),
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                updateValue(it)
                if (it.isNotEmpty()) next?.invoke()
            } else
                next?.invoke()
        },
        modifier = modifier,
        maxLines = maxLength,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(onSend = {
            callBack?.invoke()
        }),
        isError = errorCondition,
        textStyle = MaterialTheme.typography.titleLarge.copy(
            textAlign = TextAlign.Center,
            textGeometricTransform = TextGeometricTransform()
        )
    )
}

private fun setKeyEvent(keyEvent: KeyEvent, viewModel: PinViewModel, index: Int): Boolean {
    return if (keyEvent.key == Key.Backspace) {
        viewModel.isPinInvalid = false
        if (index > 0) {
            if (viewModel.pinValues[index].value.isBlank()) {
                viewModel.focusRequesters[index - 1].requestFocus()
                viewModel.pinValues[index - 1].value = ""
            } else viewModel.pinValues[index].value = ""
        } else {
            viewModel.pinValues[index].value = ""
        }
        true
    } else {
        false
    }
}

@Composable
private fun InvalidPinText(viewModel: PinViewModel) {
    AnimatedVisibility(
        visible = viewModel.isPinInvalid,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Text(
            text = stringResource(R.string.invalid_pin),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ForgotPinButton(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(PIN_SCREEN, 2)
                navController.navigate(Screen.UserIdPasswordScreen.route)
            }
        ) {
            Text(stringResource(R.string.forgot_pin))
        }
    }
}

@Composable
private fun SubmitPinButton(
    viewModel: PinViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    val pin = viewModel.pinValues.joinToString("") { it.value }
    val isPinComplete = pin.length == viewModel.pinLength

    Button(
        onClick = {
            viewModel.isLoading = true
            handlePinSubmission(pin, viewModel, navController, coroutineScope)
        },
        enabled = isPinComplete,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.isLoading) ButtonLoader()
        else {
            val textRes = when (viewModel.screenFlag) {
                1, 2 -> R.string.save_pin
                else -> R.string.login
            }
            Text(stringResource(textRes))
        }
    }
}

private fun handlePinSubmission(
    pin: String,
    viewModel: PinViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    when (viewModel.screenFlag) {
        0 -> {
            if (pin == viewModel.getPin()) {
                viewModel.isPinInvalid = false
                navigate(navController, coroutineScope)
            } else {
                viewModel.isPinInvalid = true
            }
        }
        1, 2 -> {
            viewModel.savePin(pin)
            navigate(navController, coroutineScope)
        }
    }
    viewModel.isLoading = false
}

private fun navigate(
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    // navigate to Landing screen
    coroutineScope.launch {
        navController.currentBackStackEntry?.savedStateHandle?.set(
            LOGGED_IN,
            true
        )
        navController.navigate(Screen.LandingScreen.route)
    }
}