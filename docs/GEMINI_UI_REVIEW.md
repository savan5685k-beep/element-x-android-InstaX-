# Gemini UI Branch Audit — InstaX

Audit date: 2026-11-18
Reviewed branch: `gemini/instax-ui`
Base: `develop` (commit `f7392cfcafcb47a9befb84ec8d8b236bfd8824ec`)

## Summary

The Gemini UI branch is **not ready to merge**. It contains 9 commits and changes across 18 files (roughly 18 UI/Compose implementation files plus a brief). The changes are real, but the work does not meet the completion criteria documented in the implementation brief. No branch-specific passing compile, tests, day/night screenshot verification, or device review evidence was found during this audit.

## What appears implemented

- Login/onboarding visual layout adjustments.
- Home navigation and room-list spacing/layout changes.
- Message bubble radius and accent-border styling.
- Attachment chooser surface and spacing changes.
- Settings/profile card styling.
- Room details, space, call screen and composer-related visual adjustments.

These are mostly targeted surface/hierarchy changes, not a complete screen-by-screen redesign.

## Findings / risks

### P0 — License headers changed on UI-owned files
Several Gemini-edited Kotlin files replace upstream Element/New Vector copyright and dual-license SPDX headers with an InstaX-only AGPL header. This is the same class of issue that previously triggered Konsist license assertions. Restore the original headers on every changed file; do not rewrite copyright ownership. Any additional attribution must be approved and legally appropriate.

### P0 — Login behavior regression risk
The Gemini version of `LoginPasswordView.kt` removes existing autofill integration and explicit BackHandler/autofill cancellation/commit behavior, as well as IME/keyboard navigation and accessibility/focus helpers. It adds a new card-style login form, but those functional affordances must be restored or tested and justified before acceptance.

### P1 — Scope is incomplete
The branch does not provide evidence of complete redesign for all P0/P1/P2 screen groups. Several primary screens are only lightly changed, while calls and secondary flows have not been verified.

### P1 — No verification evidence
The branch brief requires exact compile/test results, day/night review, accessibility/localization checks, and screen-by-screen completion reports. These results were not available in the reviewed branch.

### P2 — UI consistency and usability review
Review the new surface/border/radius treatment against Compound semantic tokens and existing conventions. Confirm small-height layouts, large font scaling, dark theme contrast, touch targets, keyboard/IME behavior, TalkBack, and screenshot baselines.

## Required rework checklist for Gemini

1. Restore upstream copyright and SPDX headers on all changed Kotlin files.
2. Preserve login autofill, focus/IME, BackHandler and accessibility behavior unless a safe replacement is proven.
3. Review every change for unnecessary API/import/logic alterations; keep the diff UI-only.
4. Build and run the relevant module tests and Compose screenshot tests; report exact commands and outcomes.
5. Review both Light and Dark previews for each changed screen.
6. Provide a per-screen completion table listing implementation, tests, preview results, accessibility, and remaining gaps.
7. Do not merge the UI branch. Keep it isolated until the review is clean.

## Verified scope counts

Compare endpoint reported: 9 commits ahead of `develop`, 0 behind, 18 changed files. File-level stats include onboarding/login, home/chats, message composer/timeline, settings/profile, room details, spaces, and calls. This does not equal completed screen coverage.

## Verdict

**Partially completed — visual work exists, but quality/verification and functional-preservation requirements are not satisfied yet.** Do not merge as-is.
