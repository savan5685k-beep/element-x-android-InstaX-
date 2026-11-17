/*
 * Copyright (c) 2026 InstaX contributors.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.designsystem.theme

import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors

/**
 * InstaX brand palette.
 *
 * Keep the generated Compound token files untouched: they are generated from upstream design tokens.
 * These semantic overrides provide the InstaX identity while preserving the existing component system.
 */
internal fun SemanticColors.withInstaXBrand(): SemanticColors = if (isLight) {
    copy(
        bgAccentHovered = Color(0xFF5635D5),
        bgAccentPressed = Color(0xFF4525B8),
        bgAccentRest = Color(0xFF6846E8),
        bgAccentSelected = Color(0x1F6846E8),
        bgAccentSubtle = Color(0xFFECE6FF),
        bgActionPrimaryHovered = Color(0xFF5635D5),
        bgActionPrimaryPressed = Color(0xFF4525B8),
        bgActionPrimaryRest = Color(0xFF6846E8),
        bgCanvasDefault = Color(0xFFFAF9FF),
        bgCanvasDefaultLevel1 = Color(0xFFFFFFFF),
        bgSubtlePrimary = Color(0xFFECE8F8),
        bgSubtleSecondary = Color(0xFFF2EFFA),
        bgSubtleSecondaryLevel0 = Color(0xFFF5F3FB),
        bgSubtleTertiary = Color(0xFFF7F5FC),
        borderAccentPrimary = Color(0xFF6846E8),
        borderAccentSubtle = Color(0xFFB7A5F5),
        borderFocused = Color(0xFF6846E8),
        gradientActionStop1 = Color(0xFF9A83F4),
        gradientActionStop2 = Color(0xFF8064EE),
        gradientActionStop3 = Color(0xFF6846E8),
        gradientActionStop4 = Color(0xFF5635D5),
        iconAccentPrimary = Color(0xFF6846E8),
        iconAccentTertiary = Color(0xFF8064D8),
        textActionAccent = Color(0xFF5635C5),
        textBadgeAccent = Color(0xFF5635C5),
    )
} else {
    copy(
        bgAccentHovered = Color(0xFFB9A4FF),
        bgAccentPressed = Color(0xFF8E70E8),
        bgAccentRest = Color(0xFFA78BFA),
        bgAccentSelected = Color(0x33A78BFA),
        bgAccentSubtle = Color(0xFF30264B),
        bgActionPrimaryHovered = Color(0xFFB9A4FF),
        bgActionPrimaryPressed = Color(0xFF8E70E8),
        bgActionPrimaryRest = Color(0xFFA78BFA),
        bgCanvasDefault = Color(0xFF10111A),
        bgCanvasDefaultLevel1 = Color(0xFF191827),
        bgSubtlePrimary = Color(0xFF29253A),
        bgSubtleSecondary = Color(0xFF211F30),
        bgSubtleSecondaryLevel0 = Color(0xFF171622),
        bgSubtleTertiary = Color(0xFF1D1B2B),
        borderAccentPrimary = Color(0xFFA78BFA),
        borderAccentSubtle = Color(0xFF6F5AA8),
        borderFocused = Color(0xFFB9A4FF),
        gradientActionStop1 = Color(0xFFC8B9FF),
        gradientActionStop2 = Color(0xFFB9A4FF),
        gradientActionStop3 = Color(0xFFA78BFA),
        gradientActionStop4 = Color(0xFF8E70E8),
        iconAccentPrimary = Color(0xFFB9A4FF),
        iconAccentTertiary = Color(0xFFA78BFA),
        textActionAccent = Color(0xFFC8B9FF),
        textBadgeAccent = Color(0xFFC8B9FF),
    )
}
