/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.chooseaccountprovider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.accountprovider.AccountProviderView
import io.element.android.features.login.impl.login.LoginModeEvent
import io.element.android.features.login.impl.login.LoginModeView
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.designsystem.utils.lazyColumnContentPadding
import io.element.android.libraries.designsystem.utils.scaffoldScrollableContentInsets
import io.element.android.libraries.matrix.api.auth.OAuthDetails
import io.element.android.libraries.permissions.api.localnetwork.LocalNetworkPermissionDialogView
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun ChooseAccountProviderView(
    state: ChooseAccountProviderState,
    onBackClick: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading by remember(state.loginModeState.loginMode) {
        derivedStateOf {
            state.loginModeState.loginMode is AsyncData.Loading
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        },
        contentWindowInsets = scaffoldScrollableContentInsets,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(lazyColumnContentPadding)
            ) {
                IconTitleSubtitleMolecule(
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                    iconStyle = BigIcon.Style.Default(CompoundIcons.HomeSolid()),
                    title = stringResource(id = R.string.screen_server_confirmation_title_picker_mode),
                    subTitle = null,
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ElementTheme.colors.bgCanvasDefaultLevel1,
                    border = BorderStroke(1.dp, ElementTheme.colors.borderAccentSubtle.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        state.accountProviders.forEach { item ->
                            AccountProviderView(
                                item = item,
                                selected = item == state.selectedAccountProvider,
                                onClick = {
                                    state.eventSink(ChooseAccountProviderEvent.SelectAccountProvider(item))
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = isLoading,
                    onClick = {
                        state.eventSink(ChooseAccountProviderEvent.Continue)
                    },
                    enabled = state.submitEnabled || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(36.dp))
            }
            LoginModeView(
                loginMode = state.loginModeState.loginMode,
                onClearError = {
                    state.eventSink(ChooseAccountProviderEvent.ClearError)
                },
                onLearnMoreClick = onLearnMoreClick,
                onOAuthDetails = onOAuthDetails,
                onNeedLoginPassword = onNeedLoginPassword,
            )
        }
    }
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

@PreviewsDayNight
@Composable
internal fun ChooseAccountProviderViewPreview(
    @PreviewParameter(ChooseAccountProviderStatePreviewParam::class) state: ChooseAccountProviderState
) = ElementPreview {
    ChooseAccountProviderView(
        state = state,
        onBackClick = {},
        onOAuthDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
    )
}
