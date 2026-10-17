# Kids World TV — Android starter

A beginner-friendly native Android starter app built with Kotlin and Jetpack Compose.

## Included in this prototype
- Colourful home screen and topic filters
- Search across sample lessons
- Lesson detail screen with a video placeholder
- Basic parent-area demo gate
- No tracking, network requests, or advertising SDK included

## Open and run
1. Install Android Studio (current stable release).
2. Open this folder as an existing project.
3. Allow Gradle to sync and install the required Android SDK.
4. Run the `app` configuration on an Android emulator or USB-connected phone.

## Before publishing
- Replace sample lesson data with reviewed, licensed content.
- Add a real video playback flow using a suitable player and trusted video hosting.
- Replace the demo parent gate with an appropriate parent verification design.
- Implement a privacy policy and child-appropriate data handling.
- If ads are added, use a Google Play Families-compliant ad setup and eligible ad SDK/network. Configure child-directed treatment and avoid behavioural advertising to children. Review current Google Play Families policies and applicable South African privacy requirements.
- Test accessibility, network failure, content moderation, screen-time options, and all Play Console declarations.

## Current limitations
This is a UI prototype, not a production-ready streaming or advertising app. The play button opens a placeholder screen; it does not stream video yet. Advertising is deliberately not integrated in this starter.

## Phone-only cloud build (GitHub Actions)
A workflow is included at `.github/workflows/build-apk.yml`.
After the project is uploaded to a GitHub repository:
1. Open the repository in a browser and select **Actions**.
2. Select **Build Android APK**.
3. Tap **Run workflow** (or push to the `main` branch).
4. When the run succeeds, open the run and download the `KidsWorldTV-debug-apk` artifact.
5. Extract the downloaded artifact ZIP to find `app-debug.apk`, then open it on your Android phone to install. You may need to allow installation from that browser/file manager. Only install APKs you built or trust.
This is a debug/testing APK, not a signed production release.

## Video playback starter
The app now includes AndroidX Media3 ExoPlayer support. Each `Lesson` has a `videoUrl` field defaulting to an empty string. To enable a lesson, add a direct URL to a video you own or are licensed to stream, for example in the relevant `Lesson(...)` declaration:
`videoUrl = "https://your-approved-host.example/lesson.mp4"`
That example URL is a placeholder, not a working video. Do not use a guessed URL. Hosting must allow direct playback and support HTTPS. The starter intentionally ships with no video URLs because licensing and hosting are not yet arranged.
