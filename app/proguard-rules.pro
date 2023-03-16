# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

-keepattributes *Annotation*
-keepattributes SourceFile, LineNumberTable
-keep public class * extends java.lang.Exception

-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-optimizationpasses 5
-dontusemixedcaseclassnames
-ignorewarnings

# Add this global rule
-keepattributes Signature


#Retrofit proguard
-dontnote retrofit2.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class retrofit.** { *; }
-keepclassmembernames interface * {
    @retrofit.http.* <methods>;
}

-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep interface retrofit.** { *; }
-dontwarn rx.**
-dontwarn com.google.appengine.api.urlfetch.**

#OkHttp
-keepattributes Annotation
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontnote okhttp3.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Gson
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }

# These 2 methods are called with reflection.
-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}

# Proguard ends up removing this class even if it is used in AndroidManifest.xml so force keeping it.
-keep public class org.jsoup.** {
    public *;
}

# Gson specific classes
-keep class com.google.gson.stream.** { *; }

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

-keep class org.apache.commons.** { *; }

-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

# Time
-keep class java.time.** { *; }

-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**


# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models.
# Modify this rule to fit the structure of your app.

-keepclassmembers class com.google.firebase.database.GenericTypeIndicator { *; }
-keep class com.google.googlesignin.** { *; }
-keepnames class com.google.googlesignin.* { *; }

-keep class com.google.android.gms.auth.** { *; }

# keep androidx.annotation.Keep annotation
-keep class androidx.annotation.Keep { *; }

-keep class abm.co.data.model.** { *; }

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault