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

# Retrofit 2 基础规则
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes EnclosingMethod

# Retrofit 接口
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# 保持所有 Retrofit 接口不被混淆
-keep interface * extends retrofit2.Call

# 保持 Retrofit 注解类
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# 保持所有数据模型类（根据你的包结构调整）
-keep class com.par9uet.jm.retrofit.model.** { *; }
-keepclassmembers class com.par9uet.jm.retrofit.model.** {
    *;
}

# 或者使用更通用的规则（如果你使用 Gson/Jackson 等）
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}