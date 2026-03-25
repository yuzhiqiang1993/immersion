package com.yzq.immersion

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Activity 沉浸式扩展
 * 基于 Android 15 强制 Edge-to-Edge 规范设计。
 * 只负责 Window 级别的全屏铺设和系统栏外观配置。
 * @author : yuzhiqiang
 */

/**
 * 针对 Activity 的 Window 开启 Edge-to-Edge 并配置系统栏外观。
 *
 * 在 `setContentView` 之后调用，避免背景自动推断基于未完成的视图树得到错误结果。
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
 * Activity 沉浸式重载。
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
    setSystemBarDarkMode(
        isStatusBarDark = resolvedStatusBarDark,
        isNavigationBarDark = resolvedNavBarDark
    )
}

/**
 * 动态设置状态栏内容（文字/图标）深浅色。
 *
 * @param isDark true 表示深色内容（适配浅色背景）；false 表示浅色内容
 */
fun Activity.setStatusBarDark(isDark: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightStatusBars = isDark
}

/**
 * 动态设置导航栏内容（图标）深浅色。
 *
 * 注意：Android 8.0(API 26) 以下不支持深色导航栏图标，
 * 在低版本上该设置会自动回退为浅色图标。
 *
 * @param isDark true 表示深色图标（仅 API 26+ 生效）；false 表示浅色图标
 */
fun Activity.setNavigationBarDark(isDark: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightNavigationBars = resolveNavigationBarAppearance(isDark)
}

/**
 * 动态设置系统栏内容深浅色。
 *
 * @param isStatusBarDark null 表示保持当前状态栏外观不变
 * @param isNavigationBarDark null 表示保持当前导航栏外观不变
 */
fun Activity.setSystemBarDarkMode(
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null
) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    if (isStatusBarDark != null) {
        controller.isAppearanceLightStatusBars = isStatusBarDark
    }
    if (isNavigationBarDark != null) {
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDark)
    }
}

/**
 * 根据当前窗口在状态栏区域下方实际显示的背景，自动刷新状态栏内容深浅色。
 *
 * 适用于 Fragment 切换、折叠 AppBar、主题背景动态变化等场景，
 */
fun Activity.updateStatusBarDarkModeByBackground(
    fallbackColor: Int = Color.WHITE
) {
    setStatusBarDark(resolveStatusBarDarkModeByBackground(fallbackColor))
}

/**
 * 根据当前窗口在状态栏区域下方实际显示的背景，推断状态栏内容是否应使用深色。
 *
 * 内部会对状态栏区域做多点采样，并按 View 层级对可解析的纯色背景做合成。
 * 这仍然是启发式推断；对于图片、视频或复杂渐变背景，建议显式指定。
 */
fun Activity.resolveStatusBarDarkModeByBackground(
    fallbackColor: Int = Color.WHITE
): Boolean {
    return resolveStatusBarBackgroundColor(this, fallbackColor).isLightColor()
}

// ======================== 自动推断辅助函数 ========================

private val NAVIGATION_BAR_FALLBACK_SCRIM = Color.argb(102, 0, 0, 0)
private val STATUS_BAR_SAMPLE_X_FRACTIONS = floatArrayOf(0.25f, 0.5f, 0.75f)

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
 *
 * 这是启发式推断：只识别可提取纯色的背景（ColorDrawable），复杂背景可能无法准确反映视觉亮度。
 */
private fun resolveSystemBarAppearance(activity: Activity): Boolean {
    return activity.resolveStatusBarDarkModeByBackground()
}

private fun resolveStatusBarBackgroundColor(
    activity: Activity,
    fallbackColor: Int
): Int {
    val baseColor = resolveFallbackBackgroundColor(activity) ?: fallbackColor
    val sampledColors = sampleStatusBarBackgroundColors(activity, baseColor)
    return averageColors(sampledColors) ?: baseColor
}

/**
 * 获取页面的背景色，优先级：
 * 1. 递归查找 Content 根布局中第一个有效的背景色
 * 2. 如果没找到，尝试 DecorView 的背景色
 * 3. 兜底白色
 */
private fun resolveFallbackBackgroundColor(activity: Activity): Int? {
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
    return findBackgroundColor(contentView)
        ?: extractBgColor(activity.window.decorView)
}

private fun sampleStatusBarBackgroundColors(
    activity: Activity,
    baseColor: Int
): List<Int> {
    val decorView = activity.window.decorView as? ViewGroup ?: return emptyList()
    val insets = ViewCompat.getRootWindowInsets(decorView) ?: return emptyList()
    val statusBarInsetTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
    if (statusBarInsetTop <= 0 || decorView.width <= 0 || decorView.height <= 0) {
        return emptyList()
    }

    val decorLocation = IntArray(2)
    decorView.getLocationOnScreen(decorLocation)
    val minX = decorLocation[0]
    val maxX = decorLocation[0] + decorView.width - 1
    val sampleY = decorLocation[1] + maxOf(1, statusBarInsetTop / 2)
    val contentView = activity.findViewById<ViewGroup>(android.R.id.content)

    val sampledColors = mutableListOf<Int>()
    for (fraction in STATUS_BAR_SAMPLE_X_FRACTIONS) {
        val sampleX = (decorLocation[0] + decorView.width * fraction).toInt().coerceIn(minX, maxX)
        val sampledColor = findCompositedBackgroundColorAtPoint(contentView, sampleX, sampleY)
            ?: findCompositedBackgroundColorAtPoint(decorView, sampleX, sampleY)
        if (sampledColor != null) {
            sampledColors += compositeWithBackground(sampledColor, baseColor)
        }
    }
    return sampledColors
}

private fun findCompositedBackgroundColorAtPoint(
    view: View?,
    sampleX: Int,
    sampleY: Int
): Int? {
    if (view == null || view.visibility != View.VISIBLE || view.alpha <= 0f) return null
    if (!isPointInsideView(view, sampleX, sampleY)) return null

    var color = extractRawBgColor(view)
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val childColor = findCompositedBackgroundColorAtPoint(
                view = view.getChildAt(i),
                sampleX = sampleX,
                sampleY = sampleY
            )
            if (childColor != null) {
                color = if (color != null) {
                    androidx.core.graphics.ColorUtils.compositeColors(childColor, color)
                } else {
                    childColor
                }
            }
        }
    }
    return color
}

private fun isPointInsideView(
    view: View,
    sampleX: Int,
    sampleY: Int
): Boolean {
    val visibleRect = Rect()
    return view.getGlobalVisibleRect(visibleRect) && visibleRect.contains(sampleX, sampleY)
}

private fun averageColors(colors: List<Int>): Int? {
    if (colors.isEmpty()) return null
    val averageRed = colors.sumOf { Color.red(it) } / colors.size
    val averageGreen = colors.sumOf { Color.green(it) } / colors.size
    val averageBlue = colors.sumOf { Color.blue(it) } / colors.size
    return Color.rgb(averageRed, averageGreen, averageBlue)
}

/**
 * 递归查找视图树中第一个有效的背景颜色 (深度优先)。
 * 返回首个命中的可解析纯色背景，不保证一定代表整页主背景。
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
    val rawColor = extractRawBgColor(view) ?: return null
    return compositeWithBackground(rawColor, Color.WHITE)
}

private fun extractRawBgColor(view: View?): Int? {
    val colorDrawable = view?.background as? ColorDrawable ?: return null
    return colorDrawable.color
}

/**
 * 将半透明颜色与白色混合，得到最终感知亮度的等效不透明色
 * 用于准确判断半透明背景在白色底上的实际视觉亮度
 */
private fun compositeWithBackground(
    foregroundColor: Int,
    backgroundColor: Int
): Int {
    return androidx.core.graphics.ColorUtils.compositeColors(foregroundColor, backgroundColor)
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
 * @param factor 变暗系数，建议范围 0f~1f，默认 0.7f
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
 * 无法获取时返回 0。常用于自定义 Toolbar 高度 = actionBarSize + statusBarInsetTop。
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
