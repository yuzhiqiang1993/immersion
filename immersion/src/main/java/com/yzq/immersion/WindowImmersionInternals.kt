package com.yzq.immersion

import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

internal fun Window.configureEdgeToEdgeLayout(applyCutoutMode: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    if (!applyCutoutMode || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

    val layoutParams = attributes
    layoutParams.layoutInDisplayCutoutMode =
        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    attributes = layoutParams
}

@Suppress("DEPRECATION")
internal fun Window.applyContrastEnforcement(
    strategy: ImmersionStrategy,
    applyStatusBar: Boolean = true,
    applyNavigationBar: Boolean = true
) {
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
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}
