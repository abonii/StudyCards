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

# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models.
# Modify this rule to fit the structure of your app.
# keep androidx.annotation.Keep annotation
-keep class abm.co.domain.model.**{ *; }
-keep class abm.co.data.model.**{ *; }

-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}

-keep class androidx.annotation.Keep { *; }

-keepattributes *Annotation*
-keepattributes Annotation
-keepattributes Exceptions
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontnote okhttp3.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

#Retrofit proguard
-dontnote retrofit2.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** {*;}
# Gson specific classes
-dontwarn sun.misc.**

-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.internal.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.auth.** { *; }

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class * extends com.google.firebase.database.GenericTypeIndicator { *; }

-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}
