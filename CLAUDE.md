# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WiFi Call is an Android peer-to-peer voice calling app that uses WiFi Direct (P2P) to enable two phones on the same network to discover each other and make calls without internet connectivity.

## Build Commands

### Build APK locally
```bash
./gradlew assembleDebug    # Build debug APK
./gradlew assembleRelease  # Build release APK
```

### Run tests
```bash
./gradlew test
```

### Clean build
```bash
./gradlew clean
```

## CI/CD

GitHub Actions automatically builds APKs on push to `main`/`develop` branches and on pull requests. Artifacts are uploaded as:
- `app-debug` - Debug APK
- `app-release` - Release APK (unsigned)

## Architecture

### Core Components

- **`WiFiDirectService`** (`service/`) - Manages WiFi Direct P2P discovery, connection, and group formation. Handles peer discovery callbacks and connection state changes.

- **`AudioStreamer`** (`audio/`) - Handles bidirectional audio streaming using `AudioRecord` (mic input) and `AudioTrack` (speaker output). Uses TCP sockets on port 8888 for audio data transmission. The group owner acts as server, peer acts as client.

- **`MainActivity`** (`ui/`) - Main UI with device list, discover button, and call controls. Handles runtime permissions (location, audio).

- **`DeviceAdapter`** (`ui/`) - RecyclerView adapter for displaying discovered WiFi Direct devices.

### Key Permissions

The app requires these permissions (requested at runtime):
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` - Required for WiFi Direct peer discovery
- `RECORD_AUDIO` - Microphone access for voice calls
- `MODIFY_AUDIO_SETTINGS` - Audio routing for calls
- `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` - WiFi Direct control

### WiFi Direct Flow

1. User taps "Discover" → `WiFiDirectService.startDiscovery()`
2. Service discovers peers → `onDeviceDiscovered` callback updates UI
3. User taps device → `WiFiDirectService.connectToDevice()`
4. Connection established → `onConnectionInfoAvailable` triggers audio stream
5. Group owner starts server socket, peer connects as client
6. `AudioStreamer` begins bidirectional PCM audio streaming

## Development Notes

- Target SDK 35, min SDK 26
- Kotlin with Material3 design
- No Android Studio required - all builds via Gradle CLI
- Release APK is unsigned (signing configuration not included)
