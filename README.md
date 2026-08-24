# SecureAuth

A small Android TOTP authenticator.

## What it does

- Accepts a Base32 TOTP secret.
- Generates standard 6-digit HMAC-SHA1 TOTP codes.
- Refreshes codes every 30 seconds.
- Runs generation locally without sending the secret to a server.

## Run it

Open the project in Android Studio, let Gradle sync, and run the `app` module on an emulator or Android device.

## Test secret

For a quick test, enter `JBSWY3DPEHPK3PXP`, a commonly used example Base32 secret. Do not use this example secret for a real account.
