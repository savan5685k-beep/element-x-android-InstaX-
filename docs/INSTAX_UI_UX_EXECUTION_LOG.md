# InstaX UI/UX Execution Log

This log tracks implementation work on `ui/instax-redesign`. A screen group is not complete until its implementation, day/night preview review, relevant tests, and CI verification are complete.

## Current baseline

- Base branch: `develop`
- Working branch: `ui/instax-redesign`
- Architecture: Jetpack Compose + Compound design system
- Brand direction: InstaX purple/indigo with deep-slate neutrals; preserve light and dark themes
- Homeserver: retain Matrix AccountProvider flow and Matrix.org public provider; do not require a self-hosted server
- Packaging: APK/AAB remains deferred until UI/UX work and verification are complete

## Screen-group status

| Area | Status | Next implementation focus |
|---|---|---|
| Onboarding and login | Audit started | Verify InstaX identity, Matrix.org choice clarity, sign-in hierarchy, loading/error and small-screen states |
| Home / Chats / Spaces | Audit started | Top-bar hierarchy, room-list density, filters/search discoverability, empty/loading states, bottom navigation and FAB |
| Conversation / composer | Audit started | Timeline hierarchy, composer ergonomics, attachment/voice states, failed-send and encrypted-message states |
| Settings / account | Audit started | Profile entry, section grouping, appearance controls, privacy and destructive-action clarity |
| Room / Space details | Not started | Member and room information hierarchy, invite/settings actions |
| Calls | Not started | Call status, primary controls, touch targets and safe insets |
| Secondary flows | Not started | QR login, recovery, moderation, labs, media and attachment states |
| Accessibility and visual verification | Not started | TalkBack labels, touch targets, day/night previews and targeted tests |

## First source audit

The following implementation files have been inspected on the working branch:

- `features/login/impl/.../screens/onboarding/OnBoardingView.kt`
- `features/home/impl/HomeView.kt`
- `features/home/impl/components/HomeTopBar.kt`
- `features/home/impl/components/RoomListContentView.kt`
- `features/messages/impl/MessagesView.kt`
- `features/messages/impl/messagecomposer/MessageComposerView.kt`
- `features/preferences/impl/root/PreferencesRootView.kt`
- `plugins/src/main/kotlin/config/BuildTimeConfig.kt`

The existing screens already use Compound components and semantic theme tokens. Redesigns must be targeted; do not replace the app's design system or alter Matrix state/event ownership. No Kotlin implementation change is included in this initial tracking commit.

## Completion gate

A screen group may be marked complete only after:
1. The screen implementation has been changed and reviewed.
2. Existing navigation, Matrix behavior, test tags, localization and accessibility semantics remain valid.
3. Light and dark previews have been reviewed.
4. Relevant compile and unit/UI tests pass.
5. The corresponding GitHub Actions run succeeds.

Do not create a release APK/AAB before all prioritized UI/UX groups pass this gate.
