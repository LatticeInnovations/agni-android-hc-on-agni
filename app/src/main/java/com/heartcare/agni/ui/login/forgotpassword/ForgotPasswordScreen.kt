package com.heartcare.agni.ui.login.forgotpassword

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.heartcare.agni.ui.common.ButtonLoader
import com.heartcare.agni.utils.constants.NavControllerConstants.EMAIL
import com.heartcare.agni.utils.network.CheckNetwork.isInternetAvailable
import com.heartcare.agni.utils.regex.EmailRegex.emailPattern
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
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
                    text = stringResource(id = R.string.enter_registered_email),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(50.dp))
                OutlinedTextField(
                    value = viewModel.inputValue,
                    onValueChange = {
                        if (it.length <= 50) {
                            viewModel.inputValue = it.trim()
                        }
                        viewModel.isError = viewModel.inputValue.isBlank() || !viewModel.inputValue.matches(emailPattern)
                        viewModel.isServerError = false
                        viewModel.errorMsg = if (viewModel.inputValue.isBlank()) context.getString(R.string.email_is_required)
                        else context.getString(R.string.enter_valid_email)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text(text = if (viewModel.isError || viewModel.isServerError) viewModel.errorMsg else "")
                    },
                    isError = viewModel.isError || viewModel.isServerError,
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
                ContinueButton(viewModel, navController, coroutineScope, context, snackBarHostState)
            }
        }
    )
}

@Composable
private fun ContinueButton(
    viewModel: ForgotPasswordViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
    context: Context,
    snackBarHostState: SnackbarHostState
) {
    Button(
        onClick = {
            if (!viewModel.isLoading) {
                // Continue
                when (isInternetAvailable(context)) {
                    true -> {
                        viewModel.isLoading = true
                        viewModel.requestOtp {
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    EMAIL,
                                    viewModel.inputValue
                                )
                                navController.navigate(Screen.AuthenticateOtpScreen.route)
                            }
                        }
                    }

                    false -> {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                context.getString(R.string.no_internet_error_msg)
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = viewModel.isBtnEnabled()
    ) {
        if (viewModel.isLoading) ButtonLoader()
        else Text(stringResource(R.string.continue_text))
    }
}