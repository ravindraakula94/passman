# RA_Vault - Android Password Manager

A secure, offline Android password manager application that stores encrypted passwords locally without internet connectivity.

## Current Status

This is the **Stage 1 - Basic Implementation** with minimal functionality:

### Features Implemented
- **Authentication Screen**: User authentication using device biometrics or device credentials
- **Welcome Screen**: Simple welcome page displaying "Hello Ravindra Akula"
- **Basic Navigation**: Authentication flow to main screen with logout functionality

### Project Structure
```
RA_Vault/
├── app/
│   ├── src/main/
│   │   ├── java/com/ravault/passwordmanager/
│   │   │   ├── AuthenticationActivity.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_authentication.xml
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── drawable/
│   │   │       └── ic_lock.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Setup Instructions

### Prerequisites
1. **Android Studio**: Latest version with Android SDK
2. **Java**: JDK 8 or later
3. **Android SDK**: API levels 24-34

### Getting Started

1. **Open in Android Studio**:
   - Launch Android Studio
   - Choose "Open an existing Android Studio project"
   - Navigate to the `RA_Vault` folder and select it

2. **Configure SDK Path**:
   - Open `local.properties` file
   - Uncomment and set the `sdk.dir` path to your Android SDK location
   - Example: `sdk.dir=C\:\\Users\\YourUserName\\AppData\\Local\\Android\\Sdk`

3. **Sync Project**:
   - Android Studio will prompt to sync Gradle files
   - Click "Sync Now" or use menu: File → Sync Project with Gradle Files

4. **Build Project**:
   - Use menu: Build → Rebuild Project
   - Or press `Ctrl+Shift+F9`

### Running the App

#### On Emulator:
1. Create an Android Virtual Device (AVD) with API level 24 or higher
2. Start the emulator
3. Click the "Run" button in Android Studio or press `Shift+F10`

#### On Physical Device:
1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect device via USB
4. Click "Run" and select your device

### How It Works

1. **Launch**: App starts with the Authentication screen
2. **Authentication**: 
   - Click "Authenticate" button
   - If biometrics are available, it will prompt for biometric authentication
   - If no biometrics are available, it will proceed directly (for this basic version)
3. **Main Screen**: Shows "Hello Ravindra Akula" message
4. **Logout**: Click logout to return to authentication screen

## Technical Details

### Dependencies Used
- **androidx.core:core-ktx**: Kotlin extensions for Android
- **androidx.appcompat:appcompat**: Backward compatibility support
- **com.google.android.material:material**: Material Design components
- **androidx.constraintlayout:constraintlayout**: Layout manager
- **androidx.biometric:biometric**: Biometric authentication

### Key Components

#### AuthenticationActivity
- Handles biometric and device credential authentication
- Uses `BiometricPrompt` for secure authentication
- Gracefully handles devices without biometric hardware

#### MainActivity
- Simple welcome screen
- Logout functionality
- Clean Material Design 3 UI

### Build Configuration
- **Target SDK**: API 34 (Android 14)
- **Minimum SDK**: API 24 (Android 7.0)
- **Language**: Kotlin
- **Build System**: Gradle with Android Plugin

## Next Steps

This basic version provides the foundation for the full RA_Vault password manager. Future enhancements will include:

1. **Password Storage**: SQLite database with encryption
2. **Password Management**: Add, edit, delete, and view passwords
3. **Password Generator**: Customizable password generation
4. **Enhanced Security**: Advanced encryption and security measures
5. **UI Improvements**: Better user interface and user experience

## Troubleshooting

### Common Issues

1. **Build Errors**:
   - Ensure Android SDK is properly installed
   - Check that `local.properties` has correct SDK path
   - Try cleaning and rebuilding: Build → Clean Project, then Build → Rebuild Project

2. **Emulator Issues**:
   - Ensure AVD has sufficient RAM (2GB+)
   - Use AVD with Google APIs for biometric simulation

3. **Device Connection**:
   - Check USB debugging is enabled
   - Try different USB cable
   - Restart ADB: Run `adb kill-server` then `adb start-server` in terminal

### Getting Help

If you encounter issues:
1. Check Android Studio's Build Output panel for detailed error messages
2. Ensure all dependencies are properly downloaded
3. Verify that your Android SDK installation is complete

## Security Note

This basic version does not include production-level security features. It's designed for development and testing purposes only. Full security implementation will be added in subsequent development phases.
