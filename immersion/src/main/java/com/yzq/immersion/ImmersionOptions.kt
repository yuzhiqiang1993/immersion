package com.yzq.immersion

/**
 * 沉浸式高级配置。
 * 普通场景建议继续使用基础重载，只有在需要策略控制时再使用本配置。
 */
data class ImmersionOptions(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val isStatusBarDark: Boolean? = null,
    val isNavigationBarDark: Boolean? = null,
    val strategy: ImmersionStrategy = ImmersionStrategy.Transparent
)
