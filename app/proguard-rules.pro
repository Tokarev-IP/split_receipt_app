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

############## KOTLIN ##############
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

-keepattributes *Annotation*
-keepclassmembers class ** {
    @kotlin.Metadata *;
}
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

############## COMPOSE UI ##############
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

############## ROOM ##############
-keep class androidx.room.Entity
-keep class androidx.room.Dao
-keep class androidx.room.Database
-keep class androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.**

############## FIREBASE ##############
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firestore serialization (PropertyName annotations)
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <methods>;
}
-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault

# Firebase Auth / OneTap / AppCheck
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }

############## ML KIT ##############
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

############## COIL (image loading) ##############
-keep class coil.** { *; }
-dontwarn coil.**

############## KOIN (DI) ##############
-keep class org.koin.** { *; }
-dontwarn org.koin.**

############## ANDROIDX CREDENTIALS (OneTap / Passwords) ##############
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**

-keep class com.google.android.libraries.identity.googleid.** { *; }
-dontwarn com.google.android.libraries.identity.googleid.**

############## PLAY INTEGRITY ##############
-keep class com.google.android.play.core.integrity.** { *; }
-dontwarn com.google.android.play.core.integrity.**

############## OTHER SAFETY ##############
-keepclassmembers class * {
    public <init>(...);
}
-keepclassmembers class * {
    public void *(...);
}