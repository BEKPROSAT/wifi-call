# Add project specific ProGuard rules here.
-keep class com.wificall.** { *; }
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
