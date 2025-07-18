package com.heartcare.agni.ui.login.userpassword

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.ButtonLoader
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.utils.constants.NavControllerConstants.PASSWORD
import com.heartcare.agni.utils.constants.NavControllerConstants.PASSWORD_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PIN_SCREEN
import com.heartcare.agni.utils.network.CheckNetwork.isInternetAvailable
import com.heartcare.agni.utils.regex.RegexPatterns.atLeastOneAlphaAndNumber
import com.heartcare.agni.utils.regex.RegexPatterns.passwordRegex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UserPasswordScreen(
    navController: NavController,
    viewModel: UserPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    BackHandler {
        if (viewModel.pinScreen == 2) navController.popBackStack()
        else activity?.finish()
    }

    LaunchedEffect(viewModel.snackBarError) {
        if (viewModel.snackBarError.isNotBlank()) {
            snackBarHostState.showSnackbar(viewModel.snackBarError)
            viewModel.snackBarError = ""
        }
    }
    LaunchedEffect(Unit) {
        viewModel.pinScreen =
            navController.previousBackStackEntry?.savedStateHandle?.get<Int>(
                PIN_SCREEN
            ) ?: 1
        if (navController.previousBackStackEntry?.savedStateHandle?.contains(
                PIN_SCREEN
            ) == true
        ) {
            navController.previousBackStackEntry?.savedStateHandle?.remove<Int>(
                PIN_SCREEN
            )
        }
        val isPasswordSaved =
            navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(PASSWORD_SAVED)
                ?: false
        if (isPasswordSaved) {
            viewModel.userId = ""
            viewModel.password = ""
            viewModel.snackBarError = context.getString(R.string.password_updated_successfully)
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(PASSWORD_SAVED)
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
                    text = stringResource(id = R.string.enter_user_id_and_password),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(50.dp))
                UserIdField(viewModel, context)
                Spacer(Modifier.height(16.dp))
                PasswordField(viewModel, context)
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        // forgot password
                        navController.navigate(Screen.ForgotPasswordScreen.route)
                    }
                ) {
                    Text(stringResource(R.string.forgot_password))
                }
                Spacer(Modifier.height(20.dp))
                ContinueButton(viewModel, context, navController, coroutineScope)
            }
        }
    )
    if (viewModel.showDifferentUserLoginDialog) {
        CustomDialog(
            canBeDismissed = true,
            title = stringResource(id = R.string.different_user_login_dialog_title),
            text = stringResource(id = R.string.different_user_login_dialog_description),
            dismissBtnText = stringResource(id = R.string.no_go_back),
            confirmBtnText = stringResource(id = R.string.yes_proceed),
            dismiss = { viewModel.showDifferentUserLoginDialog = false },
            confirm = {
                viewModel.showDifferentUserLoginDialog = false
                when (isInternetAvailable(context)) {
                    true -> {
                        viewModel.isLoading = true
                        viewModel.clearAllAppData()
                        loginAndNavigate(viewModel, navController, coroutineScope)
                    }

                    false -> {
                        viewModel.snackBarError = context.getString(R.string.no_internet_error_msg)
                    }
                }
            }
        )
    }
}

@Composable
private fun UserIdField(
    viewModel: UserPasswordViewModel,
    context: Context
) {
    CustomTextField(
        value = viewModel.userId,
        label = stringResource(R.string.user_id),
        weight = 1f,
        maxLength = viewModel.maxUserIdLength,
        isError = viewModel.isUserIdError,
        error = viewModel.userIdError,
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.None,
        updateValue = {
            viewModel.userId = it
            viewModel.isUserIdError =
                viewModel.userId.length !in viewModel.minUserIdLength..viewModel.maxUserIdLength
                        || !viewModel.userId.matches(atLeastOneAlphaAndNumber)
            viewModel.userIdError =
                if (viewModel.userId.isBlank()) context.getString(R.string.user_id_required_error_msg)
                else context.getString(R.string.enter_valid_user_id)
        }
    )
}

@Composable
private fun PasswordField(
    viewModel: UserPasswordViewModel,
    context: Context
) {
    CustomTextField(
        value = viewModel.password,
        label = stringResource(R.string.password),
        weight = 1f,
        maxLength = viewModel.maxPasswordLength,
        isError = viewModel.isPasswordError,
        error = viewModel.passwordError,
        keyboardType = KeyboardType.Password,
        keyboardCapitalization = KeyboardCapitalization.None,
        updateValue = {
            viewModel.password = it
            viewModel.isPasswordError = !viewModel.password.matches(passwordRegex)
            viewModel.passwordError =
                if (viewModel.password.isBlank()) context.getString(R.string.password_required_error_msg)
                else context.getString(R.string.password_validation_error_msg)
        },
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
private fun ContinueButton(
    viewModel: UserPasswordViewModel,
    context: Context,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    Button(
        onClick = {
            // continue button handle
            if (!viewModel.isLoading) {
                when (isInternetAvailable(context)) {
                    true -> {
                        if (viewModel.isDifferentUserLogin()) {
                            viewModel.showDifferentUserLoginDialog = true
                        } else {
                            viewModel.isLoading = true
                            loginAndNavigate(viewModel, navController, coroutineScope)
                        }
                    }

                    false -> {
                        viewModel.snackBarError =
                            context.getString(R.string.no_internet_error_msg)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = viewModel.isValid()
    ) {
        if (viewModel.isLoading) ButtonLoader()
        else Text(stringResource(R.string.continue_text))
    }
}

private fun loginAndNavigate(
    viewModel: UserPasswordViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    viewModel.login {
        if (viewModel.isPasswordCreated) {
            coroutineScope.launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PIN_SCREEN,
                    viewModel.pinScreen
                )
                navController.navigate(Screen.PinScreen.route)
            }
        } else {
            coroutineScope.launch {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PASSWORD,
                    viewModel.password
                )
                navController.navigate(Screen.CreatePasswordScreen.route)
            }
        }
    }
}