package com.yzq.immersion

import android.app.Dialog
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Dialog 沉浸式扩展
 *
 * @author : yuzhiqiang
 */

/**
 * 针对 Dialog 的 Window 开启 Edge-to-Edge。
 *
 * @param isStatusBarDark 状态栏文字图标深浅色。null 表示不干预
 * @param isNavigationBarDark 导航栏文字图标深浅色。null 表示不干预
 * @param strategy 沉浸式策略，默认 [ImmersionStrategy.Auto]
 */
@Suppress("DEPRECATION")
fun Dialog.setupImmersion(
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null,
    strategy: ImmersionStrategy = ImmersionStrategy.Auto
) {
    val window = this.window ?: return

    // 确保 Dialog 填满整个屏幕
    window.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )

    // 内容延伸到系统栏与刘海区域
    window.configureEdgeToEdgeLayout(applyCutoutMode = true)

    // 状态栏保持透明
    window.statusBarColor = Color.TRANSPARENT
    // 导航栏在老系统深色图标不可用时按策略回退 scrim 兜底
    val resolvedNavBarDark = isNavigationBarDark ?: false
    window.navigationBarColor = resolveNavigationBarColor(strategy, resolvedNavBarDark)

    // Android 10+：Auto 保持系统对比度保护；Transparent 关闭以追求纯视觉效果
    window.applyContrastEnforcement(strategy)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    if (isStatusBarDark != null) {
        controller.isAppearanceLightStatusBars = isStatusBarDark
    }
    if (isNavigationBarDark != null) {
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDark)
    }

    controller.useTransientBarsBySwipe()
}

/**
 * 专门用于 BottomSheetDialog 等底部弹窗的便捷方法。
 * 让导航栏变为透明，弹窗内容可以画到导航栏下面。
 */
@Suppress("DEPRECATION")
fun Dialog.setupBottomSheetImmersion(
    isNavigationBarDark: Boolean? = null,
    strategy: ImmersionStrategy = ImmersionStrategy.Transparent
) {
    val window = this.window ?: return

    window.configureEdgeToEdgeLayout(applyCutoutMode = false)

    val resolvedNavBarDark = isNavigationBarDark ?: false
    window.navigationBarColor = resolveNavigationBarColor(strategy, resolvedNavBarDark)

    window.applyContrastEnforcement(
        strategy = strategy,
        applyStatusBar = false,
        applyNavigationBar = true
    )

    if (isNavigationBarDark != null) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDark)
    }
}
