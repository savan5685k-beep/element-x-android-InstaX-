/*
 * Copyright (c) 2026 InstaX contributors.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.login.impl.screens.loginpassword

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.error.loginError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.OutlinedTextField
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun LoginPasswordView(
    state: LoginPasswordState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val eventSink = state.eventSink
    val focusManager = LocalFocusManager.current

    fun submit() {
        focusManager.clearFocus(force = true)
        eventSink(LoginPasswordEvent.Submit)
    }

    HeaderFooterPage(
        modifier = modifier,
        isScrollable = true,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        header = {
            IconTitleSubtitleMolecule(
                iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
                title = stringResource(id = R.string.screen_login_title_with_homeserver, state.homeserver.title),
                subTitle = stringResource(id = R.string.screen_login_subtitle),
            )
        },
        footer = {
            ButtonColumnMolecule {
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = state.loginState is AsyncData.Loading,
                    onClick = ::submit,
                    enabled = state.submitEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginContinue)
                )
            }
        }
    ) {
        Column {
            var loginFieldState by textFieldState(stateValue = state.login)
            var passwordFieldState by textFieldState(stateValue = state.password)
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ElementTheme.colors.bgCanvasDefaultLevel1,
                border = BorderStroke(1.dp, ElementTheme.colors.borderAccentSubtle.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = loginFieldState,
                        onValueChange = {
                            loginFieldState = it
                            eventSink(LoginPasswordEvent.UpdateLogin(it))
                        },
                        label = stringResource(id = R.string.screen_login_form_header),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.loginEmailOrUsername),
                        placeholder = stringResource(id = R.string.screen_login_username_hint),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                    )
                    Spacer(Modifier.height(18.dp))
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = passwordFieldState,
                        onValueChange = {
                            passwordFieldState = it
                            eventSink(LoginPasswordEvent.UpdatePassword(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.loginPassword),
                        label = stringResource(id = R.string.screen_login_password_form_header),
                        placeholder = stringResource(id = R.string.screen_login_password_hint),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        trailingIcon = {
                            val image = if (passwordVisible) CompoundIcons.VisibilityOff() else CompoundIcons.VisibilityOn()
                            val description = if (passwordVisible) {
                                stringResource(CommonStrings.a11y_hide_password)
                            } else {
                                stringResource(CommonStrings.a11y_show_password)
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submit() }
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(Modifier.fillMaxSize()) {
                when (val loginState = state.loginState) {
                    is AsyncData.Failure -> {
                        LoginErrorDialog(error = loginState.error, onDismiss = {
                            eventSink(LoginPasswordEvent.ClearError)
                        })
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun LoginErrorDialog(
    error: Throwable,
    onDismiss: () -> Unit,
) {
    ErrorDialog(
        content = stringResource(id = loginError(error)),
        onDismiss = onDismiss
    )
}

@PreviewsDayNight
@Composable
internal fun LoginPasswordViewPreview(
    @PreviewParameter(LoginPasswordStatePreviewParam::class) state: LoginPasswordState
) = ElementPreview {
    LoginPasswordView(
        state = state,
        onBackClick = {},
    )
}
