# InstaX Parallel Work and Non-UI Integration Audit

## Parallel ownership

- UI implementation owner: Gemini / user on `gemini/instax-ui`.
- Non-UI hardening and integration owner: ChatGPT on `work/non-ui-hardening`.
- Stable integration base: `develop`.
- Do not push UI source changes onto the non-UI branch, and do not cherry-pick either branch into `develop` until reviewed.
- The user reviews debug builds on a physical Android device before accepting visual changes.

## UI handoff

The implementation contract is in [GEMINI_UI_IMPLEMENTATION_BRIEF.md](GEMINI_UI_IMPLEMENTATION_BRIEF.md). It requires existing Jetpack Compose + Compound architecture, semantic colors, Light/Dark support, localization, accessibility, Matrix behavior, and no self-hosted homeserver requirement.

## Confirmed repository baseline

- Application ID: `com.savan5685k.instax`.
- Application name: `InstaX`.
- Default homeserver/account-provider behavior is Matrix.org; preserve the existing AccountProvider flow.
- Production release signing uses the `production` signing config and expects environment/Gradle values for the keystore path, alias, key password, and store password.
- The release workflow requires GitHub Actions secrets:
  - `INSTAX_RELEASE_KEYSTORE_BASE64`
  - `INSTAX_RELEASE_KEY_ALIAS`
  - `INSTAX_RELEASE_KEY_PASSWORD`
  - `INSTAX_RELEASE_STORE_PASSWORD`
- The workflow explicitly fails if required signing secrets are missing; it decodes the keystore into runner temp storage and applies restrictive file permissions.
- The successful verification run `44086085638` on commit `f7392cfcafcb47a9befb84ec8d8b236bfd8824ec` completed app-variant compilation and app unit tests. It was a no-APK verification workflow.
- The release workflow triggers on `main` and `instax-v*` tags (or manual dispatch). Do not expect a push to `develop` to publish a release.
- The nightly workflow is guarded by `github.repository == 'element-hq/element-x-android'`; it intentionally does not run for this fork.

## Non-UI audit findings / open verification

1. **Production signing:** workflow wiring and fail-fast checks exist. Whether the four secrets are configured in this repository cannot be verified from source access; secret values must never be printed or committed.
2. **OAuth redirect:** `METADATA_HOST_REVERSED` is currently `instax.savan5685k.com`, which feeds the app's OAuth redirect scheme. This must be tested against the actual Matrix.org login/SSO flow before changing it. A custom URI scheme does not itself mean the user must operate a homeserver.
3. **Push notifications:** Firebase and UnifiedPush are enabled in build-time configuration, while release-specific Pusher App IDs are null. End-to-end push delivery needs a real Matrix account/device and a configured push gateway; compilation alone does not verify delivery.
4. **Optional services:** map, analytics, Sentry, and bug-report service values are null. These are not compile blockers, but related features may be unavailable or have reduced diagnostics.
5. **APK testing:** the successful verification workflow deliberately produces no APK. A separate debug-package/install workflow or a local Android SDK build is needed for device visual review.

## Debug preview workflow added

A separate manual/PR/parallel-branch workflow is defined at `.github/workflows/instax-debug-preview.yml`. It assembles Gplay and F-Droid **debug** variants with the repository's debug signing configuration and uploads universal debug APKs as a seven-day GitHub Actions artifact. It does not read production signing secrets, publish a release, or change the compile-only verification workflow.

The workflow triggers for pushes to `gemini/instax-ui` and `work/non-ui-hardening`, pull requests targeting `develop`, and manual dispatch. Its first observed run on PR #2 completed successfully: both Gplay and F-Droid debug APK assembly and artifact upload passed. The uploaded artifact is retained for seven days. A subsequent run is validating the hardened workflow reference.

## Correct device-preview model

This is a native Jetpack Compose Android app, not a web frontend. Starting a localhost web server in Termux will not render the native Compose screens in a browser. Preview should use a debug APK installed on the Android device (or an Android emulator). Termux can be used for Git operations and, if the Android SDK/JDK/NDK environment is correctly available, local build commands; otherwise use GitHub Actions to build a debug APK and download/install that artifact. Never use a production-signed APK for routine UI preview.

## Integration gate

Before merging either branch into `develop`:
- inspect changed files and ensure branch ownership boundaries were respected;
- resolve conflicts against the latest `develop`;
- run compile tasks for Gplay and F-Droid debug/release variants as applicable;
- run app unit tests and relevant module/UI screenshot tests;
- manually inspect Light and Dark screens on a physical device;
- verify Matrix.org login, room list, encrypted messaging, send/retry, attachments, notifications, and call flows as supported;
- confirm signing secrets exist without revealing their values before attempting a signed release;
- do not create a production release until the user explicitly requests it.


## Latest CI findings (2026-11-17)

- The debug-preview workflow successfully assembled both Gplay and F-Droid universal debug APKs and uploaded artifact `instax-debug-preview-c3760a54821f4fb8704306dc00dad113fb5943ee` (seven-day retention). This is a debug artifact, not a production release.
- The dedicated compile/unit-test verification workflow passed app-variant compilation and app unit tests.
- The broader code-quality workflow passed documentation checks, invalid-dependency checks, forbidden-pattern checks, ShellCheck, zizmor, Ktlint, Detekt, and Compose tests. Android lint identified one error: `app/src/main/res/drawable/transparent.xml` was unused. That unused resource has been removed on this non-UI hardening branch; the resulting push should rerun CI.
- The same code-quality run reported two failures in the repository-wide `KonsistLicenseTest` (FOSS license-header assertion and duplicate Element copyright assertion). These checks inspect the whole repository, while this branch changes only workflow/documentation plus the unused drawable. The failure is not attributed to the debug-preview workflow; the exact offending source files still need to be isolated before any license rule or header is changed.
- The repository Maestro emulator job reached its test runner but stopped because the required `MAESTRO_PASSWORD` environment secret was missing. No login or end-to-end UI result can be claimed from that run. Configure the intended test-account secret in repository Actions secrets, or explicitly keep authenticated Maestro flows disabled until a test account is provisioned. Never put credentials in source or logs.
- OAuth/SSO, Matrix messaging, push delivery, calls, and physical-device visual review remain unverified.

## Current status

The debug-preview pipeline has produced a successful artifact once. The workflow's local action reference was updated to GitHub's dedicated `$/.github/actions/setup-build` syntax after the security scanner's recommendation; the updated commit is undergoing CI. PR #2 has the `PR-Other` category label. This file records an audit and collaboration plan; it does not claim that OAuth, push delivery, production signing, or on-device UI behavior has been end-to-end tested.
