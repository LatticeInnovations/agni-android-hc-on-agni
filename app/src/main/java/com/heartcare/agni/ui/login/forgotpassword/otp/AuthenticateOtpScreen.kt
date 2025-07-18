package com.heartcare.agni.ui.login.forgotpassword.otp

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.utils.constants.NavControllerConstants.EMAIL
import com.heartcare.agni.utils.constants.NavControllerConstants.PASSWORD_SCREEN
import com.heartcare.agni.utils.network.CheckNetwork.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AuthenticateOtpScreen(
    navController: NavController,
    viewModel: AuthenticateOtpViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.email =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(EMAIL).orEmpty()
            viewModel.snackBarMsg = context.getString(R.string.authentication_code_sent)
            viewModel.isLaunched = true
        }
    }

    LaunchedEffect(viewModel.snackBarMsg) {
        if (viewModel.snackBarMsg.isNotBlank()) {
            snackBarHostState.showSnackbar(viewModel.snackBarMsg)
            viewModel.snackBarMsg = ""
        }
    }

    LaunchedEffect(viewModel.twoMinuteTimer > 0) {
        while (viewModel.twoMinuteTimer > 0) {
            delay(1000)
            viewModel.twoMinuteTimer -= 1
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(15.dp),
            ) {
                Spacer(Modifier.height(60.dp))
                Text(
                    text = stringResource(id = R.string.enter_authentication_code, viewModel.email),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(60.dp))
                CodeField(viewModel, context)
                Spacer(Modifier.height(20.dp))
                SubmitButton(viewModel, navController, context, coroutineScope)
                Spacer(modifier = Modifier.height(16.dp))
                TwoMinuteTimer(viewModel, context)
            }
        }
    )
}

@Composable
private fun CodeField(
    viewModel: AuthenticateOtpViewModel,
    context: Context
) {
    OutlinedTextField(
        value = viewModel.otp,
        onValueChange = { value ->
            if (value.length <= 6) {
                viewModel.otp = value.trim().filter { it.isDigit() }
            }
            viewModel.isError = viewModel.otp.isBlank()
            viewModel.errorMsg =
                if (viewModel.otp.isBlank()) context.getString(R.string.email_is_required)
                else context.getString(R.string.enter_valid_email)
        },
        modifier = Modifier
            .fillMaxWidth(),
        supportingText = {
            Text(text = if (viewModel.isError) viewModel.errorMsg else "")
        },
        isError = viewModel.isError,
        singleLine = true,
        placeholder = {
            Text(stringResource(R.string.authentication_code))
        }
    )
}

@Composable
private fun SubmitButton(
    viewModel: AuthenticateOtpViewModel,
    navController: NavController,
    context: Context,
    coroutineScope: CoroutineScope
) {
    Button(
        onClick = {
            // Submit
            when (isInternetAvailable(context)) {
                true -> {
                    viewModel.validateOtp {
                        coroutineScope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(PASSWORD_SCREEN, 1)
                            navController.navigate(Screen.CreatePasswordScreen.route)
                        }
                    }
                }

                false -> {
                    viewModel.snackBarMsg = context.getString(R.string.no_internet_error_msg)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = viewModel.isBtnEnabled()
    ) {
        Text(stringResource(R.string.submit))
    }
}

@Composable
private fun TwoMinuteTimer(
    viewModel: AuthenticateOtpViewModel,
    context: Context
) {
    val timer = "${
        String.format(
            Locale.ROOT,
            "%02d", viewModel.twoMinuteTimer / 60
        )
    }:${
        String.format(
            Locale.ROOT,
            "%02d",
            viewModel.twoMinuteTimer % 60
        )
    }"
    if (viewModel.twoMinuteTimer > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.request_new_code_info),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = timer,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        ResendCodeButton(viewModel, context)
    }
}

@Composable
private fun ResendCodeButton(
    viewModel: AuthenticateOtpViewModel,
    context: Context
) {
    FilledTonalButton(
        onClick = {
            // resend code
            when (isInternetAvailable(context)) {
                true -> {
                    viewModel.requestOtp {
                        viewModel.snackBarMsg = context.getString(R.string.authentication_code_sent)
                        viewModel.twoMinuteTimer = 120
                        viewModel.otp = ""
                        viewModel.isError = false
                    }
                }

                false -> {
                    viewModel.snackBarMsg = context.getString(R.string.no_internet_error_msg)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.resend_code))
    }
}