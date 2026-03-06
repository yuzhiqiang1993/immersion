package com.yzq.immersion

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import androidx.core.view.WindowCompat
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

    // 内容延伸到系统栏下方
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // 适配刘海屏，允许 Dialog 内容延伸到刘海区域
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }

    // 状态栏保持透明
    window.statusBarColor = Color.TRANSPARENT
    // 导航栏在老系统深色图标不可用时按策略回退 scrim 兜底
    val resolvedNavBarDark = isNavigationBarDark ?: false
    window.navigationBarColor = resolveNavigationBarColor(strategy, resolvedNavBarDark)

    // Android 10+：Auto 保持系统对比度保护；Transparent 关闭以追求纯视觉效果
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val disableContrast = shouldDisableContrastEnforcement(strategy)
        window.isStatusBarContrastEnforced = !disableContrast
        window.isNavigationBarContrastEnforced = !disableContrast
    }

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    if (isStatusBarDark != null) {
        controller.isAppearanceLightStatusBars = isStatusBarDark
    }
    if (isNavigationBarDark != null) {
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDark)
    }

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

/**
 * 专门用于 BottomSheetDialog 等底部弹窗的便捷方法。
 * 让导航栏变为透明，弹窗内容可以画到导航栏下面。
 */
fun Dialog.setupBottomSheetImmersion(
    isNavigationBarDark: Boolean? = null,
    strategy: ImmersionStrategy = ImmersionStrategy.Transparent
) {
    val window = this.window ?: return

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val resolvedNavBarDark = isNavigationBarDark ?: false
    window.navigationBarColor = resolveNavigationBarColor(strategy, resolvedNavBarDark)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isNavigationBarContrastEnforced = !shouldDisableContrastEnforcement(strategy)
    }

    if (isNavigationBarDark != null) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDark)
    }
}
