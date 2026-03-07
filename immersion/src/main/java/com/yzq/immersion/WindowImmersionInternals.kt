package com.yzq.immersion

import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

internal fun Window.configureEdgeToEdgeLayout(applyCutoutMode: Boolean = true) {
    // 关闭 decor 级自动适配，改为由业务在内容层消费 Insets。
    WindowCompat.setDecorFitsSystemWindows(this, false)
    if (!applyCutoutMode || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

    val layoutParams = attributes
    layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    attributes = layoutParams
}

@Suppress("DEPRECATION")
internal fun Window.applyContrastEnforcement(
    strategy: ImmersionStrategy, applyStatusBar: Boolean = true, applyNavigationBar: Boolean = true
) {
    // 仅 Android 10+ 支持对比度保护开关。
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

    val enforceContrast = !shouldDisableContrastEnforcement(strategy)
    if (applyStatusBar) {
        isStatusBarContrastEnforced = enforceContrast
    }
    if (applyNavigationBar) {
        isNavigationBarContrastEnforced = enforceContrast
    }
}

internal fun WindowInsetsControllerCompat.useTransientBarsBySwipe() {
    // 隐藏系统栏后允许手势临时唤出，避免不可恢复的沉浸状态。
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}
