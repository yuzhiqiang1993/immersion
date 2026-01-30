package com.yzq.immersionbar

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


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
        val window = activity.window

        // 标记沉浸式已启用
        InsetsDelegate.setImmersionEnabled(activity, true)

        // 内容延伸到系统栏下方
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }

        // 设置系统栏透明
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // 禁用系统强制对比度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        // 设置系统栏行为
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * 更新系统栏状态（显示/隐藏）
     * 注意：显示/隐藏始终生效，沉浸式相关配置只在启用时生效
     */
    fun updateSystemBars(
        activity: Activity,
        showStatusBar: Boolean,
        showNavigationBar: Boolean
    ) {
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // 状态栏文字颜色自动根据页面背景亮度决定（仅状态栏可见时应用）
        applyAutoStatusBarTextAppearance(activity, controller, showStatusBar)

        // 控制系统栏显示/隐藏（始终生效）
        when {
            showStatusBar && showNavigationBar -> {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }

            showStatusBar && !showNavigationBar -> {
                controller.show(WindowInsetsCompat.Type.statusBars())
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }

            !showStatusBar && showNavigationBar -> {
                controller.hide(WindowInsetsCompat.Type.statusBars())
                controller.show(WindowInsetsCompat.Type.navigationBars())
            }

            else -> {
                controller.hide(WindowInsetsCompat.Type.systemBars())
            }
        }

        // 以下沉浸式相关配置只在启用时生效
        if (!InsetsDelegate.isImmersionEnabled(activity)) {
            return
        }

        // 确保内容延伸到系统栏下方
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 确保系统栏背景透明
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // 设置隐藏时的行为
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * 设置状态栏文字颜色
     *
     * @param isDark true=深色文字（适合浅色背景），false=浅色文字（适合深色背景）
     */
    fun setStatusBarLightMode(activity: Activity, isDark: Boolean) {
        val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        controller.isAppearanceLightStatusBars = isDark
    }

    private fun applyAutoStatusBarTextAppearance(
        activity: Activity,
        controller: WindowInsetsControllerCompat,
        showStatusBar: Boolean
    ) {
        if (!showStatusBar) {
            return
        }
        val statusBarBgColor = resolveStatusBarBackgroundColor(activity)
        controller.isAppearanceLightStatusBars = isLightColor(statusBarBgColor)
    }

    private fun resolveStatusBarBackgroundColor(activity: Activity): Int {
        val rawStatusBarColor = activity.window.statusBarColor
        if (!InsetsDelegate.isImmersionEnabled(activity) && Color.alpha(rawStatusBarColor) > 0) {
            return normalizeColorForBrightness(rawStatusBarColor)
        }

        val contentRoot = (activity.findViewById<View>(android.R.id.content) as? ViewGroup)?.getChildAt(0)
        return extractBackgroundColor(contentRoot)
            ?: extractBackgroundColor(activity.window.decorView)
            ?: Color.WHITE
    }

    private fun extractBackgroundColor(view: View?): Int? {
        val colorDrawable = view?.background as? ColorDrawable ?: return null
        return normalizeColorForBrightness(colorDrawable.color)
    }

    private fun normalizeColorForBrightness(color: Int): Int {
        val alpha = Color.alpha(color)
        if (alpha >= 255) {
            return color
        }
        // Blend translucent colors onto white to approximate perceived brightness.
        val red = (Color.red(color) * alpha + 255 * (255 - alpha)) / 255
        val green = (Color.green(color) * alpha + 255 * (255 - alpha)) / 255
        val blue = (Color.blue(color) * alpha + 255 * (255 - alpha)) / 255
        return Color.rgb(red, green, blue)
    }

    private fun isLightColor(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000
        return brightness > 128
    }

    /**
     * 禁用沉浸式模式，恢复到默认状态
     */
    fun disableEdgeToEdge(activity: Activity) {
        val window = activity.window

        // 清除之前设置的 insets 监听器和 padding
        InsetsDelegate.clearInsets(activity)

        // 恢复默认状态：内容不延伸到系统栏
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // 恢复进入沉浸式前的系统栏样式快照
        InsetsDelegate.restoreOriginalSystemBarStyle(activity)

        // 标记沉浸式已禁用
        InsetsDelegate.setImmersionEnabled(activity, false)

        // 重新应用自动状态栏文字颜色，避免恢复快照后与当前背景对比不足
        updateSystemBars(
            activity = activity,
            showStatusBar = true,
            showNavigationBar = true
        )
    }
}
