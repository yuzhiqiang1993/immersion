# ImmersionBar 混淆规则
# 此规则会自动应用到使用此库的项目

# 保留公开 API
-keep class com.yzq.immersionbar.ImmersionBar { *; }
-keep class com.yzq.immersionbar.BarUtils { *; }

# Kotlin 相关
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclasseswithmembers @kotlin.Metadata class * { *; }
-keepclassmembers class **.WhenMappings {
    <fields>;
}
