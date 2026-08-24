# SecureAuth

A small Android TOTP authenticator that generates codes locally.

## Features

- Standard 6-digit HMAC-SHA1 TOTP with 30-second steps.
- TOTP secret encrypted at rest with an AES-GCM key held by Android Keystore.
- Account restore after app restart without placing the secret back into the UI.
- Explicit account deletion.
- Local RFC 6238-compatible unit tests.
- No backend and no GitHub Actions dependency.

## Build locally

Use the included Gradle wrapper:

```text
./gradlew test
./gradlew assembleDebug
./gradlew assembleRelease
```

On Windows PowerShell use `./gradlew.bat` instead.

The debug APK is produced under `app/build/outputs/apk/debug/` and the release APK under `app/build/outputs/apk/release/`.

## Release / deployment

SecureAuth deliberately does **not** use GitHub Actions for routine builds. This keeps GitHub Actions usage at zero unless automation becomes necessary later.

For a distributable build:

1. Create a release keystore outside the repository.
2. Configure Gradle signing locally or through your private build environment.
3. Run `./gradlew assembleRelease` for an APK or `./gradlew bundleRelease` for an AAB.
4. Verify the artifact before publishing.
5. Create a GitHub Release manually and attach the signed APK/AAB, or upload the AAB to Google Play Console.

Never commit a keystore, passwords, signing properties, or real TOTP secrets.

## Security notes

SecureAuth is still a personal/open-source project and should be reviewed before being trusted with critical accounts. Android Keystore protects the stored secret on supported devices, but the app does not provide cloud backup or recovery of TOTP secrets. Keep account recovery codes separately.

## Test secret

For a quick test, enter `JBSWY3DPEHPK3PXP`. Do not use this example secret for a real account.
