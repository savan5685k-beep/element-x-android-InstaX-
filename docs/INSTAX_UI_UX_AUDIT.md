# InstaX UI/UX Audit and Redesign Plan

## Scope and guardrails

- Preserve the existing Jetpack Compose + Compound design-system architecture.
- Keep the semantic InstaX purple palette and both light/dark variants in `InstaXBrandColors.kt`.
- Do not modify generated Compound token sources.
- Preserve Matrix protocol behavior, navigation contracts, accessibility semantics, localization, and existing test tags.
- Do not build or upload APK/AAB artifacts during UI work. Verification should use compile, unit tests, and screenshot/preview tests only.

The repository contains approximately 296 Kotlin `*View.kt` files across feature implementation modules. This is a staged audit, not a claim that every screen has already been redesigned.

## Screen inventory and priorities

| Priority | Screen group | Key files / areas | Audit focus |
|---|---|---|---|
| P0 | First-run onboarding and login | `features/login/impl/.../screens/onboarding`, `loginpassword`, `chooseaccountprovider`, `confirmaccountprovider`, `changeserver` | InstaX identity, clear Matrix.org choice, readable sign-in hierarchy, loading/error states, keyboard and small-screen behavior |
| P0 | Home / Chats / Spaces | `features/home/impl/HomeView.kt`, `components/HomeTopBar.kt`, `components/RoomListContentView.kt`, filters and search | navigation clarity, room-list density, empty/loading states, search/filter discoverability, FAB and bottom inset behavior |
| P0 | Conversation / timeline | `features/messages/impl/MessagesView.kt`, `timeline`, `messagecomposer/MessageComposerView.kt` | message hierarchy, composer ergonomics, attachment/voice states, reactions, receipts, encrypted and failed-send states |
| P1 | Settings and account | `features/preferences/impl/root/PreferencesRootView.kt`, account, edit-profile, notifications, media, location | grouping, labels, profile actions, privacy and destructive-action clarity |
| P1 | Room and space details | `features/roomdetails/impl`, `features/space/impl` | information hierarchy, member lists, invite/settings actions, confirmation flows |
| P1 | Calls | `features/call/impl/ui/CallScreenView.kt`, PiP | control priority, call status, touch targets, safe insets |
| P2 | Secondary / advanced flows | QR login, recovery, moderation, labs, developer settings, media viewer, link and attachment flows | consistent surfaces, error recovery, accessibility and edge states |

## Initial findings

1. The global semantic palette already has InstaX purple overrides for light and dark themes. Keep this as the single brand-color source.
2. The app launcher already contains an InstaX speech-bubble mark, while the default first-run onboarding content still rendered the generic Element logo. The onboarding content now uses a dedicated purple InstaX mark from the login feature's drawable resources.
3. Home and conversation screens already use Compound components and semantic theme colors. Prefer targeted hierarchy, spacing, state, and accessibility improvements over replacing those components with a parallel design system.
4. Homeserver selection is already based on Matrix `AccountProvider`; the default public provider is Matrix.org. UI changes must not hardcode a homeserver URL or bypass server validation.
5. The repository has preview/screenshot infrastructure. Any intentional visual change must be followed by updated day/night screenshot baselines and verification; do not silently accept unrelated snapshot churn.

## Redesign acceptance criteria

- Every primary screen has a deliberate hierarchy: one clear title, primary action, and secondary actions.
- Interactive controls have accessible labels and adequate touch targets; decorative artwork is excluded from accessibility traversal.
- Light and dark themes maintain contrast and do not introduce hard-coded screen-level brand colors.
- Loading, empty, error, offline, permission, and destructive-confirmation states are reviewed alongside the happy path.
- Small-height devices, IME/keyboard, gesture navigation, display scaling, and long localized text are considered.
- Existing test tags and event-sink/state ownership remain intact; UI remains stateless where the existing architecture expects state from presenters.
- Screenshot previews cover day and night for changed screens.

## Execution sequence

1. Onboarding/login brand alignment (started: InstaX mark replaces generic logo in the default onboarding hero).
2. Home, Chats, Spaces, filters, and search.
3. Conversation timeline and message composer.
4. Settings, account/profile, room and space details.
5. Calls and secondary flows.
6. Accessibility pass, day/night screenshot baseline updates, compile and unit-test verification.

## Verification policy

- Run targeted Compose screenshot/preview tests for changed screens and update only intentional baselines.
- Run relevant module unit tests and compile tasks.
- Inspect GitHub Actions results before considering a screen group complete.
- APK/AAB packaging remains deferred until the explicitly requested final release stage.
