/*
 * Copyright (c) 2026 InstaX contributors.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.messages.impl.messagecomposer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.R
import io.element.android.libraries.androidutils.ui.hideKeyboard
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AttachmentsBottomSheet(
    state: MessageComposerState,
    onSendLocationClick: () -> Unit,
    onCreatePollClick: () -> Unit,
    enableTextFormatting: Boolean,
    modifier: Modifier = Modifier,
) {
    val localView = LocalView.current
    var isVisible by rememberSaveable { mutableStateOf(state.showAttachmentSourcePicker) }

    BackHandler(enabled = isVisible) {
        isVisible = false
    }

    LaunchedEffect(state.showAttachmentSourcePicker) {
        isVisible = if (state.showAttachmentSourcePicker) {
            localView.hideKeyboard()
            true
        } else {
            false
        }
    }
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            state.eventSink(MessageComposerEvent.DismissAttachmentMenu)
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = rememberBottomSheetState(
                initialValue = SheetValue.Hidden,
                enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
            ),
            onDismissRequest = { isVisible = false },
            scrollable = false,
        ) {
            AttachmentSourcePickerMenu(
                state = state,
                enableTextFormatting = enableTextFormatting,
                onSendLocationClick = onSendLocationClick,
                onCreatePollClick = onCreatePollClick,
            )
        }
    }
}

@Composable
private fun AttachmentSourcePickerMenu(
    state: MessageComposerState,
    onSendLocationClick: () -> Unit,
    onCreatePollClick: () -> Unit,
    enableTextFormatting: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ElementTheme.colors.bgCanvasDefaultLevel1,
            border = BorderStroke(1.dp, ElementTheme.colors.borderAccentSubtle.copy(alpha = 0.5f)),
        ) {
            Column {
                ListItem(
                    modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.PhotoFromCamera) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.TakePhoto())),
                    content = { Text(stringResource(R.string.screen_room_attachment_source_camera_photo)) },
                )
                ListItem(
                    modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.VideoFromCamera) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.VideoCall())),
                    content = { Text(stringResource(R.string.screen_room_attachment_source_camera_video)) },
                )
                ListItem(
                    modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.FromGallery) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Image())),
                    content = { Text(stringResource(R.string.screen_room_attachment_source_gallery)) },
                )
                ListItem(
                    modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.FromFiles) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Attachment())),
                    content = { Text(stringResource(R.string.screen_room_attachment_source_files)) },
                )
                if (state.canShareLocation) {
                    ListItem(
                        modifier = Modifier.clickable {
                            state.eventSink(MessageComposerEvent.PickAttachmentSource.Location)
                            onSendLocationClick()
                        },
                        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.LocationPin())),
                        content = { Text(stringResource(R.string.screen_room_attachment_source_location)) },
                    )
                }
                ListItem(
                    modifier = Modifier.clickable {
                        state.eventSink(MessageComposerEvent.PickAttachmentSource.Poll)
                        onCreatePollClick()
                    },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Polls())),
                    content = { Text(stringResource(R.string.screen_room_attachment_source_poll)) },
                )
                if (enableTextFormatting) {
                    ListItem(
                        modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.ToggleTextFormatting(enabled = true)) },
                        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.TextFormatting())),
                        content = { Text(stringResource(R.string.screen_room_attachment_text_formatting)) },
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AttachmentsBottomSheetPreview() = ElementPreview(fillMaxSize = true) {
    AttachmentsBottomSheet(
        state = aMessageComposerState(
            canShareLocation = true,
            showAttachmentSourcePicker = true,
        ),
        onSendLocationClick = {},
        onCreatePollClick = {},
        enableTextFormatting = true,
    )
}
