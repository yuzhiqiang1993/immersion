package com.yzq.immersion

/**
 * 沉浸式配置。
 */
data class ImmersionOptions(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val isStatusBarDarkFont: Boolean? = null,
    val isNavigationBarDarkIcon: Boolean? = null,
    val strategy: ImmersionStrategy = ImmersionStrategy.Transparent
)
