package com.yzq.immersionbar

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private const val TAG = "ImmersionDelegate"
private const val DEBUG = true

private fun log(message: String) {
    if (DEBUG) Log.d(TAG, message)
}


/**
 * @description:沉浸式逻辑委托类
 *  - Android 15+ (API 35): 系统强制 Edge-to-Edge，使用 WindowCompat
 *  - Android 5.0+ (API 21): 使用 WindowCompat + WindowInsetsControllerCompat
 * @author : yuzhiqiang
 */

internal object ImmersionDelegate {

    /**
     * 启用沉浸式模式 (Edge-to-Edge)
     */
    fun enableEdgeToEdge(activity: Activity) {
        log("enableEdgeToEdge() called, API=${Build.VERSION.SDK_INT}")
        val window = activity.window

        // 使用 WindowCompat 设置内容延伸到系统栏下方（替代过时的 systemUiVisibility）
        WindowCompat.setDecorFitsSystemWindows(window, false)
        log("  setDecorFitsSystemWindows(false)")

        // 适配刘海屏：允许内容延伸到刘海区域
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
            log("  layoutInDisplayCutoutMode=SHORT_EDGES")
        }

        // 设置系统栏透明
        // statusBarColor/navigationBarColor 在 Android 15 中过时，但是老版本还是要设置为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            log("  statusBarColor=TRANSPARENT, navigationBarColor=TRANSPARENT")
        }

        // Android 10+ 禁用系统强制对比度蒙层
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
            log("  isStatusBarContrastEnforced=false, isNavigationBarContrastEnforced=false")
        }

        // 设置系统栏行为：滑动时临时显示
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        log("  systemBarsBehavior=BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE")
    }

    /**
     * 更新系统栏状态（显示/隐藏、深浅色）
     */
    fun updateSystemBars(
        activity: Activity,
        showStatusBar: Boolean,
        showNavigationBar: Boolean,
        darkStatusBarText: Boolean
    ) {
        log("updateSystemBars() called: showStatusBar=$showStatusBar, showNavigationBar=$showNavigationBar, darkStatusBarText=$darkStatusBarText")
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // 确保内容延伸到系统栏下方（防止隐藏状态栏时出现黑色区域）
        WindowCompat.setDecorFitsSystemWindows(window, false)
        log("  setDecorFitsSystemWindows(false)")

        // 确保系统栏背景透明，且允许绘制背景（防止被系统重置）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            log("  ensure: FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS + Transparent Colors")
        }

        // 设置隐藏时的行为：临时显示（滑动可唤出）
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        log("  systemBarsBehavior=BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE")

        // 设置状态栏文字颜色
        controller.isAppearanceLightStatusBars = darkStatusBarText
        log("  isAppearanceLightStatusBars=$darkStatusBarText")

        //  控制系统栏显示/隐藏
        when {
            showStatusBar && showNavigationBar -> {
                log("  action: show(systemBars)")
                controller.show(WindowInsetsCompat.Type.systemBars())
            }

            showStatusBar && !showNavigationBar -> {
                log("  action: show(statusBars), hide(navigationBars)")
                controller.show(WindowInsetsCompat.Type.statusBars())
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }

            !showStatusBar && showNavigationBar -> {
                log("  action: hide(statusBars), show(navigationBars)")
                controller.hide(WindowInsetsCompat.Type.statusBars())
                controller.show(WindowInsetsCompat.Type.navigationBars())
            }

            else -> {
                log("  action: hide(systemBars)")
                controller.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
        log("updateSystemBars() done")
    }

    /**
     * 设置状态栏文字颜色
     *
     * @param isDark true=深色文字（适合浅色背景），false=浅色文字（适合深色背景）
     */
    fun setStatusBarLightMode(activity: Activity, isDark: Boolean) {
        log("setStatusBarLightMode() called: isDark=$isDark")
        val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        controller.isAppearanceLightStatusBars = isDark
    }
}
