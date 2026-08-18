# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Moshi models and JSON annotations
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class com.example.data.model.** { *; }
-dontwarn com.squareup.moshi.**

# Keep Room DB entities & DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class com.example.data.local.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# Networking & Image Loading
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn coil.**

