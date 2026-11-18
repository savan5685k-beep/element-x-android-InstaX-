# InstaX UI/UX Implementation Brief for Gemini

This document is the implementation contract for the UI contributor working on branch `gemini/instax-ui`.

## Product direction
- Product: InstaX, Android-only Matrix-based messaging client.
- Visual direction: premium, clean, modern messaging UI; InstaX purple/indigo accents, deep-slate neutrals, restrained surfaces, clear typography.
- Both Light and Dark themes are required. Keep the same visual language across onboarding, chat list, conversation, settings, profile, spaces, and calls.
- The user will run the app locally from Termux and inspect screens before pushing changes.

## Architecture: mandatory
- Preserve the existing Jetpack Compose + Compound design-system architecture.
- Reuse existing Compound components, semantic theme colors, typography, spacing, icons, and app navigation. Do not create a parallel design system.
- Keep screen state and business logic in the existing presenters/state holders. UI composables must not invent Matrix state, network behavior, event ownership, or navigation routes.
- Preserve Matrix protocol behavior, login/account-provider flows, encryption indicators, pagination, room ordering, unread counts, send/retry behavior, permissions, and call behavior.
- Matrix.org remains the default public homeserver provider. Do not require or assume a user-operated/self-hosted homeserver. Do not hardcode a homeserver URL or bypass server validation.
- Preserve accessibility semantics, TalkBack labels, test tags, localization/resource usage, previews, and existing tests.
- Do not modify generated Compound token sources or unrelated build/release files.

## UI implementation order
1. P0 onboarding and login.
2. P0 Home / Chats / Spaces, including top bar, filters, search, room list, empty/loading/error states and navigation.
3. P0 Conversation / timeline / composer, including attachments, voice-message states, reactions, receipts, encrypted and failed-send states.
4. P1 Settings, account and profile.
5. P1 Room/Space details and calls.
6. P2 secondary flows: QR login, recovery, moderation, labs, media viewer and attachment flows.
7. Accessibility, small-screen, keyboard/IME, gesture-inset and Light/Dark visual pass.

## Design quality bar
- Every primary screen has clear information hierarchy and one obvious primary action.
- Avoid excessive gradients, oversized cards, decorative clutter, and inconsistent corner radii.
- Ensure readable contrast in both themes and adequate touch targets.
- Consider loading, empty, offline, error, permission-denied and destructive-confirmation states, not only the happy path.
- Avoid hardcoded user-facing strings; use the repository's localization approach.
- Use semantic theme tokens rather than raw color literals in feature screens.
- Maintain responsive behavior on narrow and short Android screens and with display/font scaling.

## Collaboration and Git rules
- Work only on branch `gemini/instax-ui`; do not commit UI changes to `develop` or `ui/instax-redesign`.
- Do not modify files outside UI/UX scope unless a necessary dependency is documented first.
- Commit in small logical groups with descriptive messages.
- Before pushing, run the relevant Gradle compile and tests available in the local environment; report exact commands and outcomes.
- The user will review the app locally through Termux. Do not claim a screen is approved until the user has visually checked it.
- Push the branch and provide commit SHA plus a concise changed-file summary. Do not merge it.
- Do not generate or upload APK/AAB release artifacts as part of this UI branch. Local debug installation for visual review is acceptable.

## Completion report required
For each screen group, report:
1. Screens and files changed.
2. Design changes made.
3. Light and Dark behavior checked.
4. Accessibility/localization considerations.
5. Gradle commands run and exact pass/fail results.
6. Known gaps or unverified states.

## Important boundary
This branch is UI/UX implementation only. A separate contributor is handling non-UI repository hardening, CI/release configuration review, and integration review. Avoid overlapping those files.
