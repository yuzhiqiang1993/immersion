package com.yzq.immersion

/**
 * 沉浸式配置项
 * - `isStatusBarDark/isNavigationBarDark = null` 时会基于页面背景自动推断。
 * - `strategy` 主要影响导航栏颜色回退和对比度保护策略。
 */
data class ImmersionOptions(
    val showStatusBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val isStatusBarDark: Boolean? = null,
    val isNavigationBarDark: Boolean? = null,
    val strategy: ImmersionStrategy = ImmersionStrategy.Transparent
)
