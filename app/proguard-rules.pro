# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ==================== PassMan Specific Rules ====================

# Keep all model classes (Room entities)
-keep class com.ravault.passwordmanager.data.models.** { *; }
-keep class com.ravault.passwordmanager.data.database.entities.** { *; }

# Keep Room database and DAO classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Entity class * { *; }

# Keep encryption and security related classes
-keep class com.ravault.passwordmanager.security.** { *; }

# Keep Android Keystore and Biometric classes
-keep class androidx.biometric.** { *; }
-keep class android.security.keystore.** { *; }

# Keep Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep annotation for Room
-keepattributes *Annotation*

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimize and obfuscate
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Security: Remove debugging information
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
