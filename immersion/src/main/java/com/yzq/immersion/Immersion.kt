package com.yzq.immersion

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Activity 沉浸式扩展
 * 基于 Android 15 强制 Edge-to-Edge 规范设计。
 * 只负责 Window 级别的全屏铺设和系统栏外观配置，
 * @author : yuzhiqiang
 */

/**
 * 针对 Activity 的 Window 开启 Edge-to-Edge 并配置系统栏外观。
 *
 * @param showStatusBar 是否显示状态栏
 * @param showNavigationBar 是否显示导航栏
 * @param isStatusBarDark 状态栏内容(文字/图标)是否为深色。null 表示根据背景色自动推断
 * @param isNavigationBarDark 导航栏内容图标是否为深色。null 表示根据背景色自动推断
 */
fun Activity.setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null
) {
    setupImmersion(
        ImmersionOptions(
            showStatusBar = showStatusBar,
            showNavigationBar = showNavigationBar,
            isStatusBarDark = isStatusBarDark,
            isNavigationBarDark = isNavigationBarDark
        )
    )
}

/**
 * Activity 沉浸式高级重载。
 * 可在保持默认易用性的同时，通过 [ImmersionOptions.strategy] 控制视觉与兼容策略。
 */
@Suppress("DEPRECATION")
fun Activity.setupImmersion(options: ImmersionOptions) {
    val window = this.window
    val decorView = window.decorView

    // 启用 Edge-to-Edge，允许内容延伸到系统栏与刘海区域
    window.configureEdgeToEdgeLayout(applyCutoutMode = true)

    // 智能推断：根据页面背景判断是否应使用深色文字/图标
    // 使用 lazy 延迟计算，如果用户显式指定了双方颜色，则不执行推断逻辑
    val autoDarkAppearance by lazy { resolveSystemBarAppearance(this) }

    // 状态栏文字深浅色：null 时自动根据背景推断
    val resolvedStatusBarDark = options.isStatusBarDark ?: autoDarkAppearance

    // 导航栏文字深浅色：null 时自动根据背景推断
    val resolvedNavBarDark = options.isNavigationBarDark ?: autoDarkAppearance

    // 状态栏保持透明，导航栏根据策略按需回退可读性兜底
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = resolveNavigationBarColor(options.strategy, resolvedNavBarDark)

    // Android 10+：Auto 保持系统对比度保护；Transparent 关闭以追求纯视觉效果
    window.applyContrastEnforcement(options.strategy)

    val controller = WindowInsetsControllerCompat(window, decorView)

    // 状态栏的可见性
    if (options.showStatusBar) {
        controller.show(WindowInsetsCompat.Type.statusBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.statusBars())
    }

    //导航栏的可见性
    if (options.showNavigationBar) {
        controller.show(WindowInsetsCompat.Type.navigationBars())
    } else {
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }

    // 隐藏系统栏时的行为：手势滑动可临时唤出
    controller.useTransientBarsBySwipe()
    controller.isAppearanceLightStatusBars = resolvedStatusBarDark
    controller.isAppearanceLightNavigationBars =
        resolveNavigationBarAppearance(resolvedNavBarDark)
}

// ======================== 自动推断辅助函数 ========================

private val NAVIGATION_BAR_FALLBACK_SCRIM = Color.argb(102, 0, 0, 0)

internal fun resolveNavigationBarColor(
    strategy: ImmersionStrategy,
    isNavigationBarDark: Boolean,
    sdkInt: Int = Build.VERSION.SDK_INT
): Int {
    if (strategy == ImmersionStrategy.Transparent) return Color.TRANSPARENT
    return if (sdkInt in Build.VERSION_CODES.M until Build.VERSION_CODES.O && isNavigationBarDark) {
        NAVIGATION_BAR_FALLBACK_SCRIM
    } else {
        Color.TRANSPARENT
    }
}

internal fun resolveNavigationBarAppearance(
    requestedDark: Boolean,
    sdkInt: Int = Build.VERSION.SDK_INT
): Boolean {
    return sdkInt >= Build.VERSION_CODES.O && requestedDark
}

internal fun shouldDisableContrastEnforcement(strategy: ImmersionStrategy): Boolean {
    return strategy == ImmersionStrategy.Transparent
}

/**
 * 根据页面背景色自动推断系统栏内容（文字/图标）应该是深色还是浅色
 * 亮色背景 → 返回 true (需要深色内容)
 * 暗色背景 → 返回 false (需要浅色内容)
 */
private fun resolveSystemBarAppearance(activity: Activity): Boolean {
    val bgColor = resolveBackgroundColor(activity)
    return bgColor.isLightColor()
}

/**
 * 获取页面的背景色，优先级：
 * 1. 递归查找 Content 根布局中第一个有效的背景色
 * 2. 如果没找到，尝试 DecorView 的背景色
 * 3. 兜底白色
 */
private fun resolveBackgroundColor(activity: Activity): Int {
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
    return findBackgroundColor(contentView)
        ?: extractBgColor(activity.window.decorView)
        ?: Color.WHITE
}

/**
 * 递归查找视图树中第一个有效的背景颜色 (深度优先)。
 */
private fun findBackgroundColor(view: View?): Int? {
    if (view == null || view.visibility != View.VISIBLE) return null

    // 尝试提取当前 View 的背景
    val color = extractBgColor(view)
    if (color != null) return color

    // 如果当前 View 没有背景且是 ViewGroup，递归查找其子视图
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val childColor = findBackgroundColor(view.getChildAt(i))
            if (childColor != null) return childColor
        }
    }
    return null
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
 * 判断当前颜色是否为亮色（基于 AndroidX ColorUtils 的光度计算）
 */
fun Int.isLightColor(): Boolean {
    // calculateLuminance 返回 0.0 (黑) ~ 1.0 (白) 的亮度值
    // 通常约定 0.5 作为亮/暗的临界点
    return androidx.core.graphics.ColorUtils.calculateLuminance(this) > 0.5
}

/**
 * 将当前颜色按比例加深。
 * @param factor 变暗系数，默认 0.7f
 */
fun Int.darkenColor(factor: Float = 0.7f): Int {
    return Color.rgb(
        (Color.red(this) * factor).toInt().coerceIn(0, 255),
        (Color.green(this) * factor).toInt().coerceIn(0, 255),
        (Color.blue(this) * factor).toInt().coerceIn(0, 255)
    )
}

/**
 * 获取当前主题配置的 ActionBar 高度像素值。
 * 无法获取时默认返回 0。
 */
val android.content.Context.actionBarHeight: Int
    get() {
        val typedValue = android.util.TypedValue()
        return if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            android.util.TypedValue.complexToDimensionPixelSize(
                typedValue.data, resources.displayMetrics
            )
        } else 0
    }
