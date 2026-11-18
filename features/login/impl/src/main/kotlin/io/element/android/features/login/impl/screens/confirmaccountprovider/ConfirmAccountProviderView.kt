/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.changeserver.ChangeServerView
import io.element.android.features.login.impl.login.LoginModeEvent
import io.element.android.features.login.impl.login.LoginModeView
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.auth.OAuthDetails
import io.element.android.libraries.permissions.api.localnetwork.LocalNetworkPermissionDialogView
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun ConfirmAccountProviderView(
    state: ConfirmAccountProviderState,
    onBackClick: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val eventSink = state.eventSink
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var input by textFieldState(stateValue = state.accountProviderInput)

    val suggestionSuffix = state.accountProviderSuggestion
        ?.takeIf { it.length > input.length && it.startsWith(input, ignoreCase = true) }
        ?.substring(input.length)
        .orEmpty()

    val accountProviderToSubmit by rememberUpdatedState(input + suggestionSuffix)

    fun submit() {
        focusManager.clearFocus(force = true)
        eventSink(ConfirmAccountProviderEvent.Continue(accountProviderToSubmit))
    }

    var wasShowingError by remember { mutableStateOf(false) }
    LaunchedEffect(state.isShowingError) {
        if (wasShowingError && !state.isShowingError) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        wasShowingError = state.isShowingError
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
                title = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_change_server_title_register
                    } else {
                        R.string.screen_change_server_title_login
                    }
                ),
                subTitle = null,
            )
        },
        footer = {
            ButtonColumnMolecule {
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = state.isLoading,
                    onClick = ::submit,
                    enabled = state.submitEnabled && !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginContinue)
                )
            }
        }
    ) {
        val ghostColor = ElementTheme.colors.textSecondary
        val ghostTransformation = remember(suggestionSuffix, ghostColor) {
            GhostSuffixVisualTransformation(suggestionSuffix, ghostColor)
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ElementTheme.colors.bgCanvasDefaultLevel1,
            border = BorderStroke(1.dp, ElementTheme.colors.borderAccentSubtle.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
        ) {
            TextField(
                value = input,
                onValueChange = {
                    input = it
                    eventSink(ConfirmAccountProviderEvent.UserInputChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .testTag(TestTags.changeServerServer),
                label = stringResource(id = R.string.screen_change_server_textfield_header),
                placeholder = stringResource(id = R.string.screen_change_server_textfield_placeholder),
                supportingText = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_change_server_textfield_footer_register
                    } else {
                        R.string.screen_change_server_textfield_footer_login
                    }
                ),
                visualTransformation = ghostTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (suggestionSuffix.isNotEmpty()) {
                            val accepted = input + suggestionSuffix
                            input = accepted
                            eventSink(ConfirmAccountProviderEvent.UserInputChanged(accepted))
                            focusManager.clearFocus(force = true)
                        } else {
                            submit()
                        }
                    }
                ),
                singleLine = true,
                trailingIcon = if (input.isNotEmpty()) {
                    {
                        Box(
                            Modifier.clickable(
                                onClickLabel = stringResource(CommonStrings.action_clear),
                                role = Role.Button,
                            ) {
                                input = ""
                                eventSink(ConfirmAccountProviderEvent.UserInputChanged(""))
                            }
                        ) {
                            Icon(
                                imageVector = CompoundIcons.Close(),
                                contentDescription = stringResource(CommonStrings.action_clear)
                            )
                        }
                    }
                } else {
                    null
                },
            )
        }
    }

    ChangeServerView(
        state = state.changeServerState,
        onLearnMoreClick = onLearnMoreClick,
        onSuccess = {},
    )

    LoginModeView(
        loginMode = state.loginModeState.loginMode,
        onClearError = { eventSink(ConfirmAccountProviderEvent.ClearError) },
        onLearnMoreClick = onLearnMoreClick,
        onOAuthDetails = onOAuthDetails,
        onNeedLoginPassword = onNeedLoginPassword,
    )

    LocalNetworkPermissionDialogView(
        dialog = state.loginModeState.localNetworkPermissionDialog,
        onSubmit = {
            state.loginModeState.eventSink(LoginModeEvent.RequestLocalNetworkPermission)
        },
        onDismiss = {
            state.loginModeState.eventSink(LoginModeEvent.DismissLocalNetworkPermission)
        }
    )
}

private class GhostSuffixVisualTransformation(
    private val suffix: String,
    private val color: Color,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (suffix.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val transformed = buildAnnotatedString {
            append(text)
            withStyle(SpanStyle(color = color)) { append(suffix) }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(text.length)
        }
        return TransformedText(transformed, offsetMapping)
    }
}

@PreviewsDayNight
@Composable
internal fun ConfirmAccountProviderViewPreview(
    @PreviewParameter(ConfirmAccountProviderStatePreviewParam::class) state: ConfirmAccountProviderState
) = ElementPreview {
    ConfirmAccountProviderView(
        state = state,
        onBackClick = {},
        onOAuthDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
    )
}
