package com.yzq.immersion

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * WindowInsets View 级避让扩展
 * 用于精细化控制哪些 View 需要避让系统栏。
 *
 * @author : yuzhiqiang
 */

// ======================== Padding 避让 ========================

/**
 * 为 View 增加状态栏高度的 PaddingTop
 * @param add 是否增加状态栏高度的避让。如果为 false，则恢复 View 原始的 Padding
 */
fun View.applyStatusBarPadding(add: Boolean = true) {
    var initialPaddingTop = getTag(R.id.immersion_original_padding_top) as? Int
    if (initialPaddingTop == null) {
        initialPaddingTop = this.paddingTop
        setTag(R.id.immersion_original_padding_top, initialPaddingTop)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        val topOffset = if (add) statusBarInsets.top else 0
        view.setPadding(
            view.paddingLeft, initialPaddingTop + topOffset, view.paddingRight, view.paddingBottom
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 PaddingBottom
 * @param add 是否增加导航栏高度的避让。如果为 false，则恢复 View 原始的 Padding
 */
fun View.applyNavigationBarPadding(add: Boolean = true) {
    var initialPaddingBottom = getTag(R.id.immersion_original_padding_bottom) as? Int
    if (initialPaddingBottom == null) {
        initialPaddingBottom = this.paddingBottom
        setTag(R.id.immersion_original_padding_bottom, initialPaddingBottom)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val bottomOffset = if (add) navBarInsets.bottom else 0
        view.setPadding(
            view.paddingLeft,
            view.paddingTop,
            view.paddingRight,
            initialPaddingBottom + bottomOffset
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时避让顶部状态栏和底部导航栏。
 */
fun View.applySystemBarsPadding(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    var initialPaddingTop = getTag(R.id.immersion_original_padding_top) as? Int
    if (initialPaddingTop == null) {
        initialPaddingTop = this.paddingTop
        setTag(R.id.immersion_original_padding_top, initialPaddingTop)
    }

    var initialPaddingBottom = getTag(R.id.immersion_original_padding_bottom) as? Int
    if (initialPaddingBottom == null) {
        initialPaddingBottom = this.paddingBottom
        setTag(R.id.immersion_original_padding_bottom, initialPaddingBottom)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topOffset = if (addStatusBar) systemBars.top else 0
        val bottomOffset = if (addNavigationBar) systemBars.bottom else 0

        view.setPadding(
            view.paddingLeft,
            initialPaddingTop + topOffset,
            view.paddingRight,
            initialPaddingBottom + bottomOffset
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

// ======================== Margin 避让 ========================

/**
 * 为 View 增加状态栏高度的 MarginTop
 * @param add 是否增加状态栏高度的避让。如果为 false，则恢复 View 原始的 Margin
 */
fun View.applyStatusBarMargin(add: Boolean = true) {
    var initialMarginTop = getTag(R.id.immersion_original_margin_top) as? Int
    if (initialMarginTop == null) {
        initialMarginTop = (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
        setTag(R.id.immersion_original_margin_top, initialMarginTop)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            val topOffset = if (add) statusBarInsets.top else 0
            lp.topMargin = initialMarginTop + topOffset
            view.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 MarginBottom
 * @param add 是否增加导航栏高度的避让。如果为 false，则恢复 View 原始的 Margin
 */
fun View.applyNavigationBarMargin(add: Boolean = true) {
    var initialMarginBottom = getTag(R.id.immersion_original_margin_bottom) as? Int
    if (initialMarginBottom == null) {
        initialMarginBottom =
            (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
        setTag(R.id.immersion_original_margin_bottom, initialMarginBottom)
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            val bottomOffset = if (add) navBarInsets.bottom else 0
            lp.bottomMargin = initialMarginBottom + bottomOffset
            view.layoutParams = lp
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时以 Margin 方式避让顶部状态栏和底部导航栏。
 */
fun View.applySystemBarsMargin(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    var initialMarginTop = getTag(R.id.immersion_original_margin_top) as? Int
    if (initialMarginTop == null) {
        initialMarginTop = (this.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
        setTag(R.id.immersion_original_margin_top, initialMarginTop)
    }

    var initialMarginBottom = getTag(R.id.immersion_original_margin_bottom) as? Int
    if (initialMarginBottom == null) {
        initialMarginBottom =
            (this.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
        setTag(R.id.immersion_original_margin_bottom, initialMarginBottom)
    }

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
