# WiFi Call

A peer-to-peer voice calling Android app using WiFi Direct. Two phones on the same network can discover each other and make calls without internet connectivity.

## Features

- WiFi Direct peer discovery
- P2P voice calling without internet
- Material3 UI
- GitHub Actions CI/CD for APK builds

## Building

### Prerequisites
- JDK 17
- Android SDK (API 35)

### Build APK
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

APKs are output to `app/build/outputs/apk/`

## Permissions

The app requires the following permissions (requested at runtime):
- Location (for WiFi Direct discovery)
- Microphone (for voice calls)
- Audio settings (for call routing)

## Architecture

- `WiFiDirectService` - Manages WiFi Direct P2P connections
- `AudioStreamer` - Handles bidirectional audio streaming
- `MainActivity` - Main UI with device discovery and call controls
