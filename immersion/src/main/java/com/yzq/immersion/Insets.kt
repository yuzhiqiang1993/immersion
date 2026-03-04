package com.yzq.immersion

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * WindowInsets View 级避让扩展
 * 用于精细化控制哪些 View 需要避让系统栏。
 * 通过 View Tag 持久化原始 Padding/Margin，确保多次调用不会叠加。
 *
 * @author : yuzhiqiang
 */

// Tag Key：用于在 View 上持久化原始 Padding/Margin
private val TAG_ORIGINAL_PADDING_TOP = R.id.immersion_original_padding_top
private val TAG_ORIGINAL_PADDING_BOTTOM = R.id.immersion_original_padding_bottom
private val TAG_ORIGINAL_MARGIN_TOP = R.id.immersion_original_margin_top
private val TAG_ORIGINAL_MARGIN_BOTTOM = R.id.immersion_original_margin_bottom

/**
 * 安全地获取或初始化 View 的原始 PaddingTop。
 * 仅在首次调用时存储，后续调用直接复用，防止叠加。
 */
private fun View.getOrSaveOriginalPaddingTop(): Int {
    val saved = getTag(TAG_ORIGINAL_PADDING_TOP) as? Int
    if (saved != null) return saved
    setTag(TAG_ORIGINAL_PADDING_TOP, paddingTop)
    return paddingTop
}

private fun View.getOrSaveOriginalPaddingBottom(): Int {
    val saved = getTag(TAG_ORIGINAL_PADDING_BOTTOM) as? Int
    if (saved != null) return saved
    setTag(TAG_ORIGINAL_PADDING_BOTTOM, paddingBottom)
    return paddingBottom
}

private fun View.getOrSaveOriginalMarginTop(): Int {
    val saved = getTag(TAG_ORIGINAL_MARGIN_TOP) as? Int
    if (saved != null) return saved
    val margin = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    setTag(TAG_ORIGINAL_MARGIN_TOP, margin)
    return margin
}

private fun View.getOrSaveOriginalMarginBottom(): Int {
    val saved = getTag(TAG_ORIGINAL_MARGIN_BOTTOM) as? Int
    if (saved != null) return saved
    val margin = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    setTag(TAG_ORIGINAL_MARGIN_BOTTOM, margin)
    return margin
}

// ======================== Padding 避让 ========================

/**
 * 为 View 增加状态栏高度的 PaddingTop
 */
fun View.applyStatusBarPadding() {
    val initialTop = getOrSaveOriginalPaddingTop()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.setPadding(
            view.paddingLeft,
            initialTop + statusBarInsets.top,
            view.paddingRight,
            view.paddingBottom
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 PaddingBottom
 */
fun View.applyNavigationBarPadding() {
    val initialBottom = getOrSaveOriginalPaddingBottom()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.setPadding(
            view.paddingLeft,
            view.paddingTop,
            view.paddingRight,
            initialBottom + navBarInsets.bottom
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时避让顶部状态栏和底部导航栏。
 * 可多次调用，参数变化时自动更新，不会叠加。
 */
fun View.applySystemBarsPadding(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    val initialTop = getOrSaveOriginalPaddingTop()
    val initialBottom = getOrSaveOriginalPaddingBottom()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topOffset = if (addStatusBar) systemBars.top else 0
        val bottomOffset = if (addNavigationBar) systemBars.bottom else 0

        view.setPadding(
            view.paddingLeft,
            initialTop + topOffset,
            view.paddingRight,
            initialBottom + bottomOffset
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

// ======================== Margin 避让 ========================

/**
 * 为 View 增加状态栏高度的 MarginTop
 */
fun View.applyStatusBarMargin() {
    val initialMarginTop = getOrSaveOriginalMarginTop()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.topMargin = initialMarginTop + statusBarInsets.top
            view.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 MarginBottom
 */
fun View.applyNavigationBarMargin() {
    val initialMarginBottom = getOrSaveOriginalMarginBottom()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.bottomMargin = initialMarginBottom + navBarInsets.bottom
            view.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时以 Margin 方式避让顶部状态栏和底部导航栏。
 * 可多次调用，参数变化时自动更新，不会叠加。
 */
fun View.applySystemBarsMargin(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    val initialMarginTop = getOrSaveOriginalMarginTop()
    val initialMarginBottom = getOrSaveOriginalMarginBottom()

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topOffset = if (addStatusBar) systemBars.top else 0
        val bottomOffset = if (addNavigationBar) systemBars.bottom else 0

        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.topMargin = initialMarginTop + topOffset
            lp.bottomMargin = initialMarginBottom + bottomOffset
            view.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

// ======================== 系统栏高度查询 ========================

/**
 * 获取状态栏高度（px）。
 * 必须在 View 已 attach 到 Window 后调用，否则返回 0。
 */
val View.statusBarHeight: Int
    get() {
        val insets = ViewCompat.getRootWindowInsets(this) ?: return 0
        return insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
    }

/**
 * 获取导航栏高度（px）。
 * 必须在 View 已 attach 到 Window 后调用，否则返回 0。
 */
val View.navigationBarHeight: Int
    get() {
        val insets = ViewCompat.getRootWindowInsets(this) ?: return 0
        return insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
    }

/**
 * 获取 Activity 状态栏高度（px）。
 */
val Activity.statusBarHeight: Int
    get() = window.decorView.statusBarHeight

/**
 * 获取 Activity 导航栏高度（px）。
 */
val Activity.navigationBarHeight: Int
    get() = window.decorView.navigationBarHeight
