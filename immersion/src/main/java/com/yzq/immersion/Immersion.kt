package com.yzq.immersion

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
 * Activity 沉浸式扩展
 * 基于 Android 15 强制 Edge-to-Edge 规范设计。
 * 只负责 Window 级别的全屏铺设和系统栏外观配置，
 * 绝不干涉任何 View 的 Padding/Margin。
 *
 * @author : yuzhiqiang
 */

/**
 * 针对 Activity 的 Window 开启 Edge-to-Edge 并配置系统栏外观。
 *
 * @param showStatusBar 是否显示状态栏
 * @param showNavigationBar 是否显示导航栏
 * @param isStatusBarDark 状态栏内容(文字/图标)是否为深色。null 表示根据背景色自动推断
 * @param isNavigationBarDark 导航栏内容图标是否为深色。null 表示不干预
 */
fun Activity.setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null
) {
    val window = this.window
    val decorView = window.decorView

    // 1. 核心：允许内容延伸到系统栏下方（Edge-to-Edge 的本质）
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // 适配刘海屏，允许内容延伸到刘海区域（否则全屏时顶部会有黑条）
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }

    // 2. 强制系统栏背景全透明
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    // 取消系统强制的对比度保护 (Android 10+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isStatusBarContrastEnforced = false
        window.isNavigationBarContrastEnforced = false
    }

    val controller = WindowInsetsControllerCompat(window, decorView)

    // 3. 配置可见性
    if (showStatusBar) {
        controller.show(WindowInsetsCompat.Type.statusBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.statusBars())
    }

    if (showNavigationBar) {
        controller.show(WindowInsetsCompat.Type.navigationBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    // 隐藏系统栏时的行为：手势滑动可临时唤出
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // 4. 状态栏文字深浅色：null 时自动根据背景推断
    val resolvedStatusBarDark = isStatusBarDark ?: resolveStatusBarAppearance(this)
    controller.isAppearanceLightStatusBars = resolvedStatusBarDark

    // 5. 导航栏文字深浅色：仅在手动指定时生效
    if (isNavigationBarDark != null) {
        controller.isAppearanceLightNavigationBars = isNavigationBarDark
    }
}

// ======================== 自动推断辅助函数 ========================

/**
 * 根据页面背景色自动推断状态栏文字应该是深色还是浅色
 * 亮色背景 → 返回 true (深色文字)
 * 暗色背景 → 返回 false (浅色文字)
 */
private fun resolveStatusBarAppearance(activity: Activity): Boolean {
    val bgColor = resolveBackgroundColor(activity)
    return isLightColor(bgColor)
}

/**
 * 获取页面的背景色，优先级：
 * 1. Content 根布局的背景色
 * 2. DecorView 的背景色
 * 3. 兜底白色
 */
private fun resolveBackgroundColor(activity: Activity): Int {
    val contentView =
        (activity.findViewById<View>(android.R.id.content) as? ViewGroup)?.getChildAt(0)
    return extractBgColor(contentView) ?: extractBgColor(activity.window.decorView) ?: Color.WHITE
}

/**
 * 从 View 的背景中提取纯色值
 * 仅支持 ColorDrawable（纯色背景），渐变/图片等会返回 null
 */
private fun extractBgColor(view: View?): Int? {
    val colorDrawable = view?.background as? ColorDrawable ?: return null
    return blendWithWhite(colorDrawable.color)
}

/**
 * 将半透明颜色与白色混合，得到最终感知亮度的等效不透明色
 * 用于准确判断半透明背景在白色底上的实际视觉亮度
 */
private fun blendWithWhite(color: Int): Int {
    val alpha = Color.alpha(color)
    if (alpha >= 255) return color
    val r = (Color.red(color) * alpha + 255 * (255 - alpha)) / 255
    val g = (Color.green(color) * alpha + 255 * (255 - alpha)) / 255
    val b = (Color.blue(color) * alpha + 255 * (255 - alpha)) / 255
    return Color.rgb(r, g, b)
}

/**
 * 基于 ITU-R BT.601 标准的亮度公式判断颜色是否为亮色
 */
private fun isLightColor(color: Int): Boolean {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)
    val brightness = (r * 299 + g * 587 + b * 114) / 1000
    return brightness > 128
}
