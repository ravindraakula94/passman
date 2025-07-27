# PassMan - Password Manager App - Design Document

## Overview
PassMan is a secure, offline Android password manager application that stores encrypted passwords locally without internet connectivity. The app focuses on simplicity, speed, and security.

## Architecture

### Technology Stack
- **Platform**: Android (Java/Kotlin)
- **Database**: SQLite
- **Encryption**: SHA-512 for password hashing, AES-256 for data encryption
- **Authentication**: Android Biometric API + Device PIN/Pattern
- **UI Framework**: Android Native (Material Design)

### Security Architecture
```
User Authentication → Master Key Derivation → Database Encryption/Decryption
```

## Stage 1 Implementation

### 1. Authentication System

#### Components:
- **BiometricManager**: Handle fingerprint/face unlock
- **DeviceAuthManager**: Handle device PIN/pattern authentication
- **AuthenticationActivity**: Landing page for user authentication

#### Security Flow:
1. App launches → AuthenticationActivity
2. User chooses biometric or device password
3. Upon successful authentication:
   - Generate/retrieve master key from Android Keystore
   - Derive encryption key using PBKDF2
   - Grant access to main application

#### Implementation Details:
```kotlin
class AuthenticationManager {
    fun authenticateWithBiometric(): Boolean
    fun authenticateWithDeviceCredential(): Boolean
    fun generateMasterKey(): SecretKey
    fun derivateEncryptionKey(masterKey: SecretKey, salt: ByteArray): SecretKey
}
```

### 2. Database Schema

#### Tables:
```sql
-- Passwords table
CREATE TABLE passwords (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    username TEXT,
    encrypted_password BLOB NOT NULL,
    website_url TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    salt BLOB NOT NULL
);

-- App settings
CREATE TABLE app_settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
```

### 3. Encryption System

#### Encryption Strategy:
- **Master Password**: Never stored, derived from device authentication
- **Individual Passwords**: Encrypted using AES-256-GCM with unique salt per entry
- **Key Derivation**: PBKDF2 with high iteration count (100,000+)

#### Implementation:
```kotlin
class EncryptionManager {
    fun encryptPassword(plaintext: String, key: SecretKey): EncryptedData
    fun decryptPassword(encryptedData: EncryptedData, key: SecretKey): String
    fun generateSalt(): ByteArray
    fun deriveKey(masterKey: SecretKey, salt: ByteArray): SecretKey
}

data class EncryptedData(
    val ciphertext: ByteArray,
    val iv: ByteArray,
    val salt: ByteArray
)
```

### 4. Core Activities

#### MainActivity:
- Password list view (RecyclerView)
- Add/Edit password functionality
- Search functionality
- Settings access

#### AddEditPasswordActivity:
- Form for password entry
- Password visibility toggle
- Validation and encryption before saving

#### PasswordDetailActivity:
- View password details
- Copy to clipboard functionality
- Edit/Delete options

## Stage 2 Implementation

### Password Generator System

#### Features:
- Customizable length (4-128 characters)
- Character set options:
  - Lowercase letters (a-z)
  - Uppercase letters (A-Z)
  - Numbers (0-9)
  - Special symbols (configurable set)
- Exclude similar characters option (0, O, l, 1, etc.)
- Password strength indicator

#### Implementation:
```kotlin
data class PasswordGeneratorSettings(
    val length: Int = 12,
    val includeLowercase: Boolean = true,
    val includeUppercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeSimilar: Boolean = true,
    val customSymbols: String = "!@#$%^&*()_+-=[]{}|;:,.<>?"
)

class PasswordGenerator {
    fun generatePassword(settings: PasswordGeneratorSettings): String
    fun calculateStrength(password: String): PasswordStrength
}
```

#### Password Generator UI:
- Slider for length selection
- Checkboxes for character types
- Real-time password preview
- Strength meter
- Generate button
- Copy/Use buttons

## Data Models

### Core Models:
```kotlin
data class PasswordEntry(
    val id: Long = 0,
    val title: String,
    val username: String?,
    val password: String, // Plain text (only in memory)
    val websiteUrl: String?,
    val notes: String?,
    val createdAt: Date,
    val updatedAt: Date
)

data class PasswordEntryDb(
    val id: Long = 0,
    val title: String,
    val username: String?,
    val encryptedPassword: ByteArray,
    val websiteUrl: String?,
    val notes: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val salt: ByteArray
)
```

## User Experience Design

### Navigation Flow:
```
Splash Screen → Authentication → Main List → Add/View/Edit Password
                    ↓
              Password Generator (accessible from Add/Edit)
```

### UI Components:
- **Material Design 3** components
- **Dark/Light theme** support
- **Accessibility** compliance
- **Responsive design** for different screen sizes

## Security Considerations

### Data Protection:
1. **No network permissions** in AndroidManifest.xml
2. **Root detection** and app termination
3. **Screenshot prevention** for sensitive screens
4. **App backgrounding protection** (blur overlay)
5. **Clipboard clearing** after password copy
6. **Session timeout** after inactivity

### Code Security:
1. **Code obfuscation** using ProGuard/R8
2. **Certificate pinning** (not applicable for offline app)
3. **Anti-debugging** measures
4. **Secure key storage** in Android Keystore

### Database Security:
1. **SQLCipher** for database encryption
2. **No plaintext storage** of sensitive data
3. **Secure deletion** of temporary data
4. **Database integrity checks**

## Performance Considerations

### Optimization Strategies:
1. **Lazy loading** of password entries
2. **Database indexing** on frequently queried fields
3. **Background encryption/decryption** operations
4. **Memory management** for sensitive data
5. **Efficient search algorithms**

### Memory Management:
```kotlin
class SecureString {
    private var data: CharArray
    
    fun clear() {
        Arrays.fill(data, '\u0000')
    }
    
    fun finalize() {
        clear()
    }
}
```

## Testing Strategy

### Unit Tests:
- Encryption/Decryption algorithms
- Password generation logic
- Database operations
- Authentication flows

### Integration Tests:
- End-to-end password storage/retrieval
- Authentication with biometrics
- Password generator functionality

### Security Tests:
- Penetration testing
- Code analysis for vulnerabilities
- Encryption strength validation

## File Structure
```
PassMan/
├── app/
│   ├── src/main/java/com/ravault/passwordmanager/
│   │   ├── activities/
│   │   │   ├── MainActivity.kt
│   │   │   ├── AuthenticationActivity.kt
│   │   │   ├── AddEditPasswordActivity.kt
│   │   │   └── PasswordDetailActivity.kt
│   │   ├── fragments/
│   │   │   ├── PasswordListFragment.kt
│   │   │   └── PasswordGeneratorFragment.kt
│   │   ├── data/
│   │   │   ├── database/
│   │   │   │   ├── PasswordDatabase.kt
│   │   │   │   ├── PasswordDao.kt
│   │   │   │   └── entities/
│   │   │   ├── repository/
│   │   │   │   └── PasswordRepository.kt
│   │   │   └── models/
│   │   ├── security/
│   │   │   ├── EncryptionManager.kt
│   │   │   ├── AuthenticationManager.kt
│   │   │   └── SecurityUtils.kt
│   │   ├── utils/
│   │   │   ├── PasswordGenerator.kt
│   │   │   ├── ClipboardManager.kt
│   │   │   └── Constants.kt
│   │   └── ui/
│   │       ├── adapters/
│   │       ├── viewmodels/
│   │       └── dialogs/
│   ├── src/main/res/
│   │   ├── layout/
│   │   ├── values/
│   │   ├── drawable/
│   │   └── menu/
│   └── AndroidManifest.xml
├── build.gradle
└── settings.gradle
```

## Development Phases

### Phase 1 (Stage 1): Core Functionality
1. Set up project structure
2. Implement authentication system
3. Create database schema and DAOs
4. Implement encryption system
5. Build main UI (list, add, edit, view)
6. Testing and security validation

### Phase 2 (Stage 2): Advanced Features
1. Implement password generator
2. Add generator UI
3. Integrate generator with add/edit flows
4. Enhanced search and filtering
5. Settings and preferences
6. Final testing and optimization

## Deployment Considerations

### Build Configuration:
- **Release builds** with ProGuard enabled
- **Signing configuration** for app store
- **Version management** strategy
- **Backup and restore** functionality (optional)

### App Store Preparation:
- Privacy policy creation
- Security audit documentation
- User guide and documentation
- Beta testing with limited users

This design provides a comprehensive roadmap for building a secure, efficient, and user-friendly password manager application that meets all the specified requirements while maintaining high security standards.
