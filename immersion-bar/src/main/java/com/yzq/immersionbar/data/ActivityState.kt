package com.yzq.immersionbar.data

/**
 * Activity 级沉浸式状态快照
 */
internal data class ActivityState(
    // 进入沉浸式前的状态栏颜色
    val originalStatusBarColor: Int,
    // 进入沉浸式前的导航栏颜色
    val originalNavigationBarColor: Int,
    // 进入沉浸式前导航栏图标深浅模式（true=深色图标）
    val originalLightNavigationBars: Boolean,
    // 是否已保存过进入沉浸式前的样式快照
    val snapshotCaptured: Boolean = false,
    // 当前 Activity 是否处于沉浸式模式
    val immersionEnabled: Boolean = false,
    // 当前用于 insets padding 的目标 View 信息
    val paddingInfo: PaddingInfo? = null
)
