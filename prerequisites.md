# PassMan - Prerequisites and Setup Guide

This document outlines everything you need to successfully build, develop, and test the PassMan password manager app on your Android device.

## Development Environment Setup

### 1. Android Studio Installation

**Download and Install:**
- Download Android Studio from: https://developer.android.com/studio
- Minimum system requirements:
  - Windows 10/11 (64-bit)
  - 8 GB RAM minimum (16 GB recommended)
  - 8 GB available disk space (additional space for Android SDK)
  - 1280 x 800 minimum screen resolution

**Installation Steps:**
1. Run the Android Studio installer
2. Follow the setup wizard
3. Install the Android SDK (API levels 24-34 recommended)
4. Install Android SDK Build-Tools
5. Install Android Emulator (optional, for testing without physical device)

### 2. Java Development Kit (JDK)

**JDK Requirements:**
- JDK 11 or JDK 17 (recommended)
- Android Studio typically includes a bundled JDK, but you may need to install separately

**Installation:**
- Download from: https://adoptopenjdk.net/ or https://www.oracle.com/java/technologies/downloads/
- Set JAVA_HOME environment variable
- Add JDK bin directory to PATH

### 3. Android SDK Components

**Required SDK Components:**
```
Android SDK Platform-Tools
Android SDK Build-Tools 34.0.0 (latest)
Android 14 (API level 34) - Target SDK
Android 7.0 (API level 24) - Minimum SDK
Google Play Services
Android Support Repository
Google Repository
```

**Installation via SDK Manager:**
1. Open Android Studio → Tools → SDK Manager
2. Install the components listed above
3. Accept license agreements

## Project Setup

### 1. Create New Android Project

**Project Configuration:**
```
Project Name: PassMan
Package Name: com.ravault.passwordmanager
Language: Kotlin
Minimum SDK: API 24 (Android 7.0)
Target SDK: API 34 (Android 14)
Project Template: Empty Activity
```
