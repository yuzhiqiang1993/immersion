# Immersion 混淆规则
# 此规则会自动应用到使用此库的项目

# 保留公开 API（纯 Kotlin 扩展函数，顶层函数编译为 xxxKt 类）
-keep class com.yzq.immersion.ImmersionKt { *; }
-keep class com.yzq.immersion.DialogImmersionKt { *; }
-keep class com.yzq.immersion.InsetsKt { *; }

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
