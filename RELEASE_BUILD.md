# PassMan Release Build Instructions

## Overview
This document explains how to create a release build of the PassMan password manager app.

## Prerequisites

### 1. Generate a Release Keystore
Before creating a release build, you need to generate a keystore for signing your app:

```bash
keytool -genkey -v -keystore passman-release-key.keystore -alias passman-key -keyalg RSA -keysize 2048 -validity 10000
```

**Important**: Store this keystore file securely and never commit it to version control!

### 2. Configure Signing
1. Place your keystore file in a secure location (NOT in the project directory)
2. Update `gradle.properties` with your signing configuration:
   ```properties
   KEYSTORE_PASSWORD=your_keystore_password
   KEY_ALIAS=passman-key
   KEY_PASSWORD=your_key_password
   ```
3. Update `app/build.gradle` signing config to point to your keystore:
   ```gradle
   signingConfigs {
       release {
           storeFile file("/path/to/your/passman-release-key.keystore")
           storePassword project.findProperty("KEYSTORE_PASSWORD")
           keyAlias project.findProperty("KEY_ALIAS")
           keyPassword project.findProperty("KEY_PASSWORD")
       }
   }
   ```
4. Uncomment the signing config line in the release build type:
   ```gradle
   release {
       // ... other config
       signingConfig signingConfigs.release
   }
   ```

## Building Release APK

### Method 1: Using VS Code Tasks
1. Open Command Palette (`Ctrl+Shift+P`)
2. Type "Tasks: Run Task"
3. Select "Build Release APK"

### Method 2: Using Terminal
```bash
# Clean previous builds
./gradlew clean

# Build release APK
./gradlew assembleRelease
```

### Method 3: Build Android App Bundle (Recommended for Google Play)
```bash
./gradlew bundleRelease
```

## Output Locations

After successful build, you'll find the files in:
- **APK**: `app/build/outputs/apk/release/app-release.apk`
- **AAB**: `app/build/outputs/bundle/release/app-release.aab`

## Release Build Features

The release build includes:
- ✅ **Code obfuscation** with ProGuard/R8
- ✅ **Resource shrinking** to reduce APK size
- ✅ **Debugging disabled** for security
- ✅ **Logging removed** in production
- ✅ **Optimized performance**
- ✅ **Signed with release certificate**

## Security Checklist

Before releasing, ensure:
- [ ] No hardcoded secrets or API keys
- [ ] ProGuard rules properly configured
- [ ] Screenshot prevention enabled
- [ ] Root detection implemented
- [ ] Network permissions removed
- [ ] Debug logging disabled
- [ ] App signing certificate secured

## Testing Release Build

1. **Install on device**:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

2. **Test key features**:
   - Authentication (biometric/PIN)
   - Password storage/retrieval
   - Encryption/decryption
   - App backgrounding protection
   - No debug information visible

3. **Performance testing**:
   - App startup time
   - Password generation speed
   - Database operations
   - Memory usage

## Common Issues

### Build Failures
- **Signing issues**: Verify keystore path and credentials
- **ProGuard errors**: Check proguard-rules.pro for missing keeps
- **Missing dependencies**: Run `./gradlew --refresh-dependencies`

### APK Size Optimization
- Enable resource shrinking: `shrinkResources true`
- Use vector drawables instead of multiple PNG sizes
- Remove unused resources and libraries

## App Store Preparation

### Google Play Store
1. Build AAB (Android App Bundle): `./gradlew bundleRelease`
2. Upload to Google Play Console
3. Complete store listing information
4. Set up privacy policy
5. Configure app signing

### Alternative Stores
1. Build APK: `./gradlew assembleRelease`
2. Follow store-specific requirements
3. Ensure compliance with store policies

## Version Management

Update version before release in `app/build.gradle`:
```gradle
defaultConfig {
    versionCode 2      // Increment for each release
    versionName "1.1"  // User-visible version
}
```

## Security Notes

- **Never commit signing keys** to version control
- **Use environment variables** for sensitive build configuration
- **Store keystores securely** with proper backup
- **Use different keys** for debug and release builds
- **Consider key rotation** policies for long-term maintenance
