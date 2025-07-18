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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.utils.constants.NavControllerConstants.EMAIL
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun AuthenticateOtpScreen(
    navController: NavController,
    viewModel: AuthenticateOtpViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.email =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>(EMAIL).orEmpty()
            snackBarHostState.showSnackbar(
                context.getString(R.string.authentication_code_sent)
            )
            viewModel.isLaunched = true
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
                ContinueButton(viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                TwoMinuteTimer(viewModel)
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
        isError = viewModel.isBtnEnabled(),
        singleLine = true,
        placeholder = {
            Text(stringResource(R.string.authentication_code))
        }
    )
}

@Composable
private fun ContinueButton(
    viewModel: AuthenticateOtpViewModel
) {
    Button(
        onClick = {
            // Continue
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !viewModel.isError
    ) {
        Text(stringResource(R.string.submit))
    }
}

@Composable
private fun TwoMinuteTimer(
    viewModel: AuthenticateOtpViewModel
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
        FilledTonalButton(
            onClick = {
                // resend code
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.resend_code))
        }
    }
}