# Dependency Migration Plan

This plan addresses the compilation errors and configuration issues following a major update of project dependencies, including AGP 9.3.1, Firebase 34.18.0, and various Jetpack libraries.

## User Review Required

> [!IMPORTANT]
> The build environment is currently using **Java 25**, which is incompatible with the existing **Gradle 8.10.2**. I will attempt to upgrade Gradle to a compatible version (e.g., 8.12 or 9.0) to support the environment and AGP 9.3.1.

> [!WARNING]
> Several dependency strings in `app/build.gradle` are currently malformed (missing artifact names for Firebase) and will be corrected to use the Firebase BoM correctly.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Users/SSS_Arpan/Desktop/my work/office work/SSSQuick_new/gradle/wrapper/gradle-wrapper.properties)
*   Upgrade Gradle version to 8.11.1 or 8.12 to support Java 23+ and AGP 9.3.1.

#### [MODIFY] [build.gradle](file:///C:/Users/SSS_Arpan/Desktop/my work/office work/SSSQuick_new/build.gradle) (Root)
*   Ensure Kotlin and AGP plugin versions match the user's request.

#### [MODIFY] [build.gradle](file:///C:/Users/SSS_Arpan/Desktop/my work/office work/SSSQuick_new/app/build.gradle)
*   Fix malformed Firebase dependencies:
    *   `com.google.firebase:20.1.0` -> `com.google.firebase:firebase-crashlytics`
    *   `com.google.firebase:23.2.0` -> `com.google.firebase:firebase-analytics`
    *   `com.google.firebase:22.1.2` -> `com.google.firebase:firebase-config`
*   Verify and sync other dependency versions (Room, Hilt, Navigation, etc.).

### Source Code Adaptation

#### [MODIFY] Multiple Files
*   **Retrofit 3.0.0 Migration**: Adapt to API changes in Retrofit 3.0 (e.g., package changes or new response handling).
*   **Glide 5.0 Migration**: Update Glide usage to the new version (checking for `GlideApp` vs `Glide` changes).
*   **Room 2.8.4 Migration**: Check for new annotation processing or schema requirements.
*   **Lifecycle/Navigation/Activity**: Fix any deprecated or removed APIs in the latest Jetpack releases (e.g., `ActivityResultLauncher` changes, `ViewModel` factory updates).

## Verification Plan

### Automated Tests
*   `gradle_assemble_all`: Run the full build to check for compilation errors.
*   `gradle_sync`: Synchronize the project after all fixes are applied.

### Manual Verification
*   Check for any runtime crashes related to dependency mismatches (if deployment is possible).
