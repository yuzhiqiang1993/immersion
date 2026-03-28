package com.yzq.immersion

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

/**
 * Activity 沉浸式布局与系统栏配置。
 * 建议在 `setContentView` 之后调用。
 *
 * @param showStatusBar 是否显示状态栏
 * @param showNavigationBar 是否显示导航栏
 * @param isStatusBarDarkFont 状态栏内容是否为深色。null 表示自动推断。
 * @param isNavigationBarDarkIcon 导航栏图标是否为深色。null 表示自动推断。
 */
fun Activity.setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDarkFont: Boolean? = null,
    isNavigationBarDarkIcon: Boolean? = null
) {
    setupImmersion(
        ImmersionOptions(
            showStatusBar = showStatusBar,
            showNavigationBar = showNavigationBar,
            isStatusBarDarkFont = isStatusBarDarkFont,
            isNavigationBarDarkIcon = isNavigationBarDarkIcon
        )
    )
}

/**
 * Fragment 沉浸式配置扩展。
 */
fun Fragment.setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDarkFont: Boolean? = null,
    isNavigationBarDarkIcon: Boolean? = null
) {
    setupImmersion(
        ImmersionOptions(
            showStatusBar = showStatusBar,
            showNavigationBar = showNavigationBar,
            isStatusBarDarkFont = isStatusBarDarkFont,
            isNavigationBarDarkIcon = isNavigationBarDarkIcon
        )
    )
}

/**
 * Fragment 沉浸式重载。
 * 可在保持默认易用性的同时，通过 [ImmersionOptions.strategy] 控制视觉与兼容策略。
 */
fun Fragment.setupImmersion(options: ImmersionOptions) {
    requireActivity().setupImmersion(options)
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
    val autoStatusBarDarkAppearance by lazy { shouldUseDarkStatusBarFont() }
    val autoNavigationBarDarkAppearance by lazy { shouldUseDarkNavBarIcon() }

    // 状态栏文字深浅色：null 时自动根据背景推断
    val resolvedStatusBarDark = options.isStatusBarDarkFont ?: autoStatusBarDarkAppearance

    val resolvedNavBarDark = options.isNavigationBarDarkIcon ?: autoNavigationBarDarkAppearance

    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = resolveNavigationBarColor(options.strategy, resolvedNavBarDark)
    window.applyContrastEnforcement(options.strategy)

    val controller = WindowInsetsControllerCompat(window, decorView)
    
    val statusBars = WindowInsetsCompat.Type.statusBars()
    val navBars = WindowInsetsCompat.Type.navigationBars()
    
    if (options.showStatusBar) controller.show(statusBars) else controller.hide(statusBars)
    if (options.showNavigationBar) controller.show(navBars) else controller.hide(navBars)

    controller.useTransientBarsBySwipe()
    setSystemBarDarkMode(resolvedStatusBarDark, resolvedNavBarDark)
}

/**
 * 设置状态栏内容（文字/图标）深浅色。
 */
fun Activity.setStatusBarDarkFont(isDarkFont: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightStatusBars = isDarkFont
}

/**
 * 设置导航栏图标深色模式（API 26+ 生效）。
 */
fun Activity.setNavigationBarDarkIcon(isDarkIcon: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.isAppearanceLightNavigationBars = resolveNavigationBarAppearance(isDarkIcon)
}

/**
 * 动态设置系统栏内容深浅色。
 *
 * @param isStatusBarDarkFont null 表示保持当前状态栏外观不变
 * @param isNavigationBarDarkIcon null 表示保持当前导航栏外观不变
 */
fun Activity.setSystemBarDarkMode(
    isStatusBarDarkFont: Boolean? = null,
    isNavigationBarDarkIcon: Boolean? = null
) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    if (isStatusBarDarkFont != null) {
        controller.isAppearanceLightStatusBars = isStatusBarDarkFont
    }
    if (isNavigationBarDarkIcon != null) {
        controller.isAppearanceLightNavigationBars =
            resolveNavigationBarAppearance(isNavigationBarDarkIcon)
    }
}

/**
 * 根据当前窗口在状态栏区域下方实际显示的背景，自动刷新状态栏内容深浅色。
 *
 * 适用于 Fragment 切换、折叠 AppBar、主题背景动态变化等场景，
 */
fun Activity.syncStatusBarFontWithBg(
    fallbackColor: Int = Color.WHITE
) {
    val decorView = window.decorView
    if (decorView.width > 0 && decorView.height > 0 && decorView.isLaidOut) {
        setStatusBarDarkFont(shouldUseDarkStatusBarFont(fallbackColor))
    } else {
        OneShotPreDrawListener.add(decorView) {
            setStatusBarDarkFont(shouldUseDarkStatusBarFont(fallbackColor))
        }
    }
}

/**
 * 根据当前窗口在导航栏区域下方实际显示的背景，自动刷新导航栏图标深浅色。
 */
fun Activity.syncNavBarIconWithBg(
    fallbackColor: Int = Color.WHITE
) {
    val decorView = window.decorView
    if (decorView.width > 0 && decorView.height > 0 && decorView.isLaidOut) {
        setNavigationBarDarkIcon(shouldUseDarkNavBarIcon(fallbackColor))
    } else {
        OneShotPreDrawListener.add(decorView) {
            setNavigationBarDarkIcon(shouldUseDarkNavBarIcon(fallbackColor))
        }
    }
}

/**
 * 根据当前窗口在系统栏区域下方实际显示的背景，同时刷新状态栏和导航栏图标深浅色。
 */
fun Activity.syncSystemBarsWithBg(
    statusBarFallbackColor: Int = Color.WHITE,
    navigationBarFallbackColor: Int = Color.WHITE
) {
    val decorView = window.decorView
    if (decorView.width > 0 && decorView.height > 0 && decorView.isLaidOut) {
        setSystemBarDarkMode(
            isStatusBarDarkFont = shouldUseDarkStatusBarFont(statusBarFallbackColor),
            isNavigationBarDarkIcon = shouldUseDarkNavBarIcon(navigationBarFallbackColor)
        )
    } else {
        OneShotPreDrawListener.add(decorView) {
            setSystemBarDarkMode(
                isStatusBarDarkFont = shouldUseDarkStatusBarFont(statusBarFallbackColor),
                isNavigationBarDarkIcon = shouldUseDarkNavBarIcon(navigationBarFallbackColor)
            )
        }
    }
}

/**
 * 基于采样推断状态栏内容是否应使用深色。
 */
fun Activity.shouldUseDarkStatusBarFont(
    fallbackColor: Int = Color.WHITE
): Boolean {
    return resolveStatusBarBackgroundColor(this, fallbackColor).isLightColor()
}



/**
 * 根据当前窗口在导航栏区域下方实际显示的背景，推断导航栏图标是否应使用深色。
 */
fun Activity.shouldUseDarkNavBarIcon(
    fallbackColor: Int = Color.WHITE
): Boolean {
    return resolveNavigationBarBackgroundColor(this, fallbackColor).isLightColor()
}


/**
 * 基于当前可见区域的渲染结果，判断 View 背景是否为浅色。
 *
 * 返回：
 * - `true`：浅色背景，通常适合深色文字/图标
 * - `false`：深色背景，通常适合浅色文字/图标
 * - `null`：当前无法可靠获取，例如 View 未布局、未 attach、不可见或采样失败
 */
fun View.isBackgroundLight(): Boolean? {
    return sampleRenderedBgColor()?.isLightColor()
}

/**
 * 采样 View 背景的平均颜色。
 */
fun View.sampleRenderedBgColor(): Int? {
    if (!ViewCompat.isAttachedToWindow(this) || !isShown || width <= 0 || height <= 0) {
        return null
    }

    if (!getGlobalVisibleRect(TMP_RECT_ON_SCREEN) || TMP_RECT_ON_SCREEN.isEmpty) {
        return null
    }

    val rootView = rootView ?: return null
    if (rootView.width <= 0 || rootView.height <= 0) {
        return null
    }

    rootView.getLocationOnScreen(TMP_LOCATION)
    TMP_RECT_ON_SCREEN.offset(-TMP_LOCATION[0], -TMP_LOCATION[1])

    TMP_RECT_ROOT_BOUNDS.set(0, 0, rootView.width, rootView.height)
    if (!TMP_RECT_ON_SCREEN.intersect(TMP_RECT_ROOT_BOUNDS)) {
        return null
    }

    return captureAverageColor(
        view = rootView,
        captureRect = TMP_RECT_ON_SCREEN
    )
}

// ======================== 自动推断辅助函数 ========================

private val NAVIGATION_BAR_FALLBACK_SCRIM = Color.argb(102, 0, 0, 0)
private const val SYSTEM_BAR_CAPTURE_WIDTH = 24
private const val SYSTEM_BAR_CAPTURE_HEIGHT = 8

// 重用内存池：测量计算
private val TMP_RECT_ON_SCREEN = Rect()
private val TMP_RECT_ROOT_BOUNDS = Rect()
private val TMP_LOCATION = IntArray(2)

// 重用内存池：采样像素提取
private var cachedBitmap: Bitmap? = null
private var cachedCanvas: Canvas? = null
private var cachedPixels: IntArray? = null

private enum class SystemBarEdge {
    Top, Bottom
}

internal fun resolveNavigationBarColor(
    strategy: ImmersionStrategy,
    isNavigationBarDarkIcon: Boolean,
    sdkInt: Int = Build.VERSION.SDK_INT
): Int {
    if (strategy == ImmersionStrategy.Transparent) return Color.TRANSPARENT
    return if (sdkInt in Build.VERSION_CODES.M until Build.VERSION_CODES.O && isNavigationBarDarkIcon) {
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

private fun resolveStatusBarBackgroundColor(
    activity: Activity,
    fallbackColor: Int
): Int {
    return resolveSystemBarBackgroundColor(
        activity = activity,
        fallbackColor = fallbackColor,
        insetType = WindowInsetsCompat.Type.statusBars(),
        edge = SystemBarEdge.Top
    )
}

private fun resolveNavigationBarBackgroundColor(
    activity: Activity,
    fallbackColor: Int
): Int {
    return resolveSystemBarBackgroundColor(
        activity = activity,
        fallbackColor = fallbackColor,
        insetType = WindowInsetsCompat.Type.navigationBars(),
        edge = SystemBarEdge.Bottom
    )
}

private fun resolveSystemBarBackgroundColor(
    activity: Activity,
    fallbackColor: Int,
    insetType: Int,
    edge: SystemBarEdge
): Int {
    val captureRect = resolveSystemBarCaptureRect(
        activity = activity,
        insetType = insetType,
        edge = edge
    ) ?: return fallbackColor
    return captureAverageColor(activity.window.decorView, captureRect) ?: fallbackColor
}

private fun resolveSystemBarCaptureRect(
    activity: Activity,
    insetType: Int,
    edge: SystemBarEdge
): Rect? {
    val decorView = activity.window.decorView
    val insets = ViewCompat.getRootWindowInsets(decorView) ?: return null
    val barInsets = insets.getInsets(insetType)
    val barSize = when (edge) {
        SystemBarEdge.Top -> barInsets.top
        SystemBarEdge.Bottom -> barInsets.bottom
    }
    if (barSize <= 0 || decorView.width <= 0 || decorView.height <= 0) {
        return null
    }
    return when (edge) {
        SystemBarEdge.Top -> Rect(0, 0, decorView.width, barSize)
        SystemBarEdge.Bottom -> Rect(0, decorView.height - barSize, decorView.width, decorView.height)
    }
}

private fun captureAverageColor(
    view: View,
    captureRect: Rect
): Int? {
    if (captureRect.width() <= 0 || captureRect.height() <= 0) return null

    val bitmapWidth = minOf(SYSTEM_BAR_CAPTURE_WIDTH, captureRect.width().coerceAtLeast(1))
    val bitmapHeight = minOf(SYSTEM_BAR_CAPTURE_HEIGHT, captureRect.height().coerceAtLeast(1))

    if (cachedBitmap == null || cachedBitmap!!.width < bitmapWidth || cachedBitmap!!.height < bitmapHeight) {
        cachedBitmap?.recycle()
        cachedBitmap = Bitmap.createBitmap(SYSTEM_BAR_CAPTURE_WIDTH, SYSTEM_BAR_CAPTURE_HEIGHT, Bitmap.Config.ARGB_8888)
        cachedCanvas = Canvas(cachedBitmap!!)
        cachedPixels = IntArray(SYSTEM_BAR_CAPTURE_WIDTH * SYSTEM_BAR_CAPTURE_HEIGHT)
    }

    val bitmap = cachedBitmap!!
    val canvas = cachedCanvas!!
    val pixels = cachedPixels!!

    return runCatching {
        bitmap.eraseColor(Color.TRANSPARENT)
        canvas.save()
        canvas.scale(
            bitmapWidth.toFloat() / captureRect.width(),
            bitmapHeight.toFloat() / captureRect.height()
        )
        canvas.translate(-captureRect.left.toFloat(), -captureRect.top.toFloat())
        view.draw(canvas)
        canvas.restore()

        bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)
        averageColors(pixels, bitmapWidth * bitmapHeight)
    }.getOrNull()
}

private fun averageColors(colors: IntArray, count: Int): Int? {
    if (count <= 0) return null
    var alphaSum = 0L
    var redSum = 0L
    var greenSum = 0L
    var blueSum = 0L
    var validCount = 0

    for (i in 0 until count) {
        val color = colors[i]
        val alpha = Color.alpha(color)
        if (alpha > 0) {
            alphaSum += alpha
            redSum += Color.red(color)
            greenSum += Color.green(color)
            blueSum += Color.blue(color)
            validCount++
        }
    }

    if (validCount == 0) return null
    return Color.argb(
        (alphaSum / validCount).toInt(),
        (redSum / validCount).toInt(),
        (greenSum / validCount).toInt(),
        (blueSum / validCount).toInt()
    )
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
