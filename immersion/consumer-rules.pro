# Immersion consumer ProGuard/R8 rules
#
# 本库不依赖反射、JNI、或需要运行时保留注解信息的序列化框架，
# 因此默认不需要任何强制 keep 规则。
#
# 这样可以让接入方应用在开启 R8 时继续获得最佳的体积优化。
#
# 如果你的业务代码会通过“字符串反射”调用本库 API，
# 可按需取消下面规则的注释（默认不建议开启）。

# -keep class com.yzq.immersion.ImmersionKt { public static *; }
# -keep class com.yzq.immersion.DialogImmersionKt { public static *; }
# -keep class com.yzq.immersion.InsetsKt { public static *; }
# -keep class com.yzq.immersion.ImmersionOptions { *; }
# -keep class com.yzq.immersion.ImmersionStrategy { *; }
