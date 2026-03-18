# Proguard rules for Project Astrum Android

-keep class com.novusforge.astrum.** { *; }
-keep class io.github.auburn.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

-dontwarn org.lwjgl.**
-dontwarn com.novusforge.**
