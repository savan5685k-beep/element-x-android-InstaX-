/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.R
import io.element.android.features.home.impl.contentType
import io.element.android.features.home.impl.filters.RoomListFilter
import io.element.android.features.home.impl.filters.RoomListFiltersEmptyStateResources
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.aRoomListFiltersState
import io.element.android.features.home.impl.filters.selection.FilterSelectionState
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.features.home.impl.roomlist.RoomListBanner
import io.element.android.features.home.impl.roomlist.RoomListContentState
import io.element.android.features.home.impl.roomlist.RoomListContentStatePreviewParam
import io.element.android.features.home.impl.roomlist.RoomListEvent
import io.element.android.features.home.impl.roomlist.toRoomListBanner
import io.element.android.features.home.impl.spacefilters.SpaceFiltersState
import io.element.android.features.home.impl.spacefilters.anUnselectedSpaceFiltersState
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.utils.OnVisibleRangeChangeEffect
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsEvent
import io.element.android.libraries.push.api.battery.BatteryOptimizationEvent
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

@Composable
fun RoomListContentView(
    contentState: RoomListContentState,
    filtersState: RoomListFiltersState,
    spaceFiltersState: SpaceFiltersState,
    lazyListState: LazyListState,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvent) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    when (contentState) {
        is RoomListContentState.Skeleton -> {
            SkeletonView(
                modifier = modifier,
                count = contentState.count,
                contentPadding = contentPadding,
            )
        }
        is RoomListContentState.Empty -> {
            EmptyView(
                modifier = modifier.padding(contentPadding),
                state = contentState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onCreateRoomClick = onCreateRoomClick,
            )
        }
        is RoomListContentState.Rooms -> {
            RoomsView(
                modifier = modifier,
                state = contentState,
                hideInvitesAvatars = hideInvitesAvatars,
                filtersState = filtersState,
                spaceFiltersState = spaceFiltersState,
                eventSink = eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = onRoomClick,
                lazyListState = lazyListState,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun SkeletonView(
    count: Int,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        repeat(count) { index ->
            item {
                RoomSummaryPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider(modifier = Modifier.padding(start = 76.dp, end = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyView(
    state: RoomListContentState.Empty,
    eventSink: (RoomListEvent) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        val text = buildString {
            append(stringResource(R.string.screen_roomlist_empty_title))
            append("\n")
            append(stringResource(R.string.screen_roomlist_empty_message))
        }
        EmptyScaffold(
            title = null,
            subtitle = text,
            showBrandIcon = true,
            bottomPart = {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    text = stringResource(CommonStrings.action_start_chat),
                    leadingIcon = IconSource.Vector(CompoundIcons.Chat()),
                    onClick = onCreateRoomClick,
                )
            },
            modifier = Modifier.align(Alignment.Center),
        )
        RoomListBannerContainer(
            banner = state.securityBannerState.toRoomListBanner(),
        ) { banner ->
            RoomListBannerView(
                banner = banner,
                onActionClick = {
                    when (banner) {
                        RoomListBanner.SetUpRecovery -> onSetUpRecoveryClick()
                        RoomListBanner.RecoveryKeyConfirmation -> onConfirmRecoveryKeyClick()
                        else -> Unit
                    }
                },
                onDismissClick = { eventSink(RoomListEvent.DismissBanner) },
            )
        }
    }
}

@Composable
private fun RoomsView(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    filtersState: RoomListFiltersState,
    spaceFiltersState: SpaceFiltersState,
    eventSink: (RoomListEvent) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val isSpaceFilterSelected = spaceFiltersState is SpaceFiltersState.Selected
    val hasAnyFilterSelected = filtersState.hasAnyFilterSelected || isSpaceFilterSelected
    if (state.summaries.isEmpty() && hasAnyFilterSelected) {
        EmptyViewForFilterStates(
            selectedFilters = filtersState.selectedFilters(),
            isSpaceFilterSelected = isSpaceFilterSelected,
            modifier = modifier.fillMaxSize()
        )
    } else {
        RoomsViewList(
            state = state,
            hideInvitesAvatars = hideInvitesAvatars,
            eventSink = eventSink,
            onSetUpRecoveryClick = onSetUpRecoveryClick,
            onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
            onRoomClick = onRoomClick,
            contentPadding = contentPadding,
            lazyListState = lazyListState,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun RoomsViewList(
    state: RoomListContentState.Rooms,
    hideInvitesAvatars: Boolean,
    eventSink: (RoomListEvent) -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val banner = state.toRoomListBanner()
    val bannerSlotMinHeight = with(LocalDensity.current) { 1.toDp() }
    OnVisibleRangeChangeEffect(lazyListState) { visibleRange ->
        val roomsRange = (visibleRange.first - 1).coerceAtLeast(0)..<visibleRange.last
        eventSink(RoomListEvent.UpdateVisibleRange(roomsRange))
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            RoomListBannerContainer(
                banner = banner,
                modifier = Modifier.heightIn(min = bannerSlotMinHeight),
            ) { displayedBanner ->
                RoomListBannerView(
                    banner = displayedBanner,
                    onActionClick = {
                        when (displayedBanner) {
                            RoomListBanner.SetUpRecovery -> onSetUpRecoveryClick()
                            RoomListBanner.RecoveryKeyConfirmation -> onConfirmRecoveryKeyClick()
                            RoomListBanner.FullScreenIntentPermission ->
                                state.fullScreenIntentPermissionsState.eventSink(FullScreenIntentPermissionsEvent.OpenSettings)
                            RoomListBanner.BatteryOptimization ->
                                state.batteryOptimizationState.eventSink(BatteryOptimizationEvent.RequestDisableOptimizations)
                            RoomListBanner.NewNotificationSound,
                            RoomListBanner.SwipeAvatar -> Unit
                        }
                    },
                    onDismissClick = {
                        when (displayedBanner) {
                            RoomListBanner.SetUpRecovery,
                            RoomListBanner.RecoveryKeyConfirmation -> eventSink(RoomListEvent.DismissBanner)
                            RoomListBanner.FullScreenIntentPermission ->
                                state.fullScreenIntentPermissionsState.eventSink(FullScreenIntentPermissionsEvent.Dismiss)
                            RoomListBanner.BatteryOptimization -> state.batteryOptimizationState.eventSink(BatteryOptimizationEvent.Dismiss)
                            RoomListBanner.NewNotificationSound -> eventSink(RoomListEvent.DismissNewNotificationSoundBanner)
                            RoomListBanner.SwipeAvatar -> eventSink(RoomListEvent.DismissSwipeAvatarBanner)
                        }
                    },
                )
            }
        }
        itemsIndexed(
            items = state.summaries,
            contentType = { _, room -> room.contentType() },
        ) { index, room ->
            RoomSummaryRow(
                room = room,
                hideInviteAvatars = hideInvitesAvatars,
                isInviteSeen = room.displayType == RoomSummaryDisplayType.INVITE &&
                    state.seenRoomInvites.contains(room.roomId),
                showUnreadCount = state.showUnreadCount,
                showAllActivity = state.showAllActivity,
                onClick = onRoomClick,
                eventSink = eventSink,
            )
            if (index != state.summaries.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 76.dp, end = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun RoomListBannerContainer(
    banner: RoomListBanner?,
    modifier: Modifier = Modifier,
    content: @Composable (RoomListBanner) -> Unit,
) {
    AnimatedContent(
        targetState = banner,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            fadeIn(tween(durationMillis = HALF_DURATION_MILLIS, delayMillis = HALF_DURATION_MILLIS)) togetherWith
                fadeOut(tween(durationMillis = HALF_DURATION_MILLIS)) using
                SizeTransform { _, _ -> tween(durationMillis = 2 * HALF_DURATION_MILLIS) }
        },
        contentAlignment = Alignment.Center,
        label = "RoomListBannerContainer",
    ) { targetBanner ->
        if (targetBanner != null) {
            content(targetBanner)
        }
    }
}

private const val HALF_DURATION_MILLIS = 150

@Composable
private fun RoomListBannerView(
    banner: RoomListBanner,
    onActionClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    when (banner) {
        RoomListBanner.SetUpRecovery -> SetUpRecoveryKeyBanner(
            onContinueClick = onActionClick,
            onDismissClick = onDismissClick,
        )
        RoomListBanner.RecoveryKeyConfirmation -> ConfirmRecoveryKeyBanner(
            onContinueClick = onActionClick,
            onDismissClick = onDismissClick,
        )
        RoomListBanner.FullScreenIntentPermission -> FullScreenIntentPermissionBanner(
            onActionClick = onActionClick,
            onDismissClick = onDismissClick,
        )
        RoomListBanner.BatteryOptimization -> BatteryOptimizationBanner(
            onActionClick = onActionClick,
            onDismissClick = onDismissClick,
        )
        RoomListBanner.NewNotificationSound -> NewNotificationSoundBanner(
            onDismissClick = onDismissClick,
        )
        RoomListBanner.SwipeAvatar -> SwipeAvatarBanner(
            onDismissClick = onDismissClick,
        )
    }
}

@Composable
private fun EmptyViewForFilterStates(
    selectedFilters: ImmutableList<RoomListFilter>,
    isSpaceFilterSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val emptyStateResources = RoomListFiltersEmptyStateResources.fromSelectedFilters(selectedFilters, isSpaceFilterSelected) ?: return
    EmptyScaffold(
        title = stringResource(emptyStateResources.title),
        subtitle = stringResource(emptyStateResources.subtitle),
        modifier = modifier,
        bottomPart = {
            Spacer(modifier = Modifier.height(32.dp))
        }
    )
}

@Composable
private fun EmptyScaffold(
    title: String?,
    subtitle: String,
    modifier: Modifier = Modifier,
    showBrandIcon: Boolean = false,
    bottomPart: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showBrandIcon) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = ElementTheme.colors.bgAccentSubtle,
                border = BorderStroke(1.dp, ElementTheme.colors.borderAccentSubtle),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = CompoundIcons.ChatSolid(),
                        contentDescription = null,
                        tint = ElementTheme.colors.iconAccentPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        title?.let {
            Text(
                text = title,
                style = ElementTheme.typography.fontHeadingMdBold,
                color = ElementTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Text(
            text = subtitle,
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        bottomPart()
    }
}

@PreviewsDayNight
@Composable
internal fun RoomListContentViewPreview(@PreviewParameter(RoomListContentStatePreviewParam::class) state: RoomListContentState) = ElementPreview {
    RoomListContentView(
        contentState = state,
        filtersState = aRoomListFiltersState(
            filterSelectionStates = RoomListFilter.entries.map {
                FilterSelectionState(
                    filter = it,
                    isSelected = true
                )
            }
        ),
        spaceFiltersState = anUnselectedSpaceFiltersState(),
        hideInvitesAvatars = false,
        eventSink = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onRoomClick = {},
        onCreateRoomClick = {},
        lazyListState = rememberLazyListState(),
        contentPadding = PaddingValues(0.dp),
    )
}
