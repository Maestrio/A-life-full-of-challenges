# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.challengetracker.data.model.** { *; }

# Glance
-keep class androidx.glance.** { *; }
