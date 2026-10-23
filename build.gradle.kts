
name: Build Android APK

on:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Extract Android project
        run: |
          unzip -q KidsWorldTV-Android-Starter.zip -d extracted
          find extracted -name settings.gradle.kts

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.9'

      - name: Build APK
        run: |
          PROJECT_DIR="$(dirname "$(find extracted -name settings.gradle.kts -print -quit)")"
          echo "Building project in $PROJECT_DIR"
          gradle -p "$PROJECT_DIR" assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: KidsWorldTV-debug-apk
          path: extracted/**/app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error

    buildFeatures { compose = true }
}kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
