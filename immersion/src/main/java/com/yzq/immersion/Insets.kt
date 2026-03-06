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

private fun View.obtainInsetsState(): ImmersionInsetsState {
    val existingState = getTag(R.id.immersion_insets_state) as? ImmersionInsetsState
    if (existingState != null) return existingState
    return ImmersionInsetsState().also {
        setTag(R.id.immersion_insets_state, it)
    }
}

private fun View.ensureInsetsListener(state: ImmersionInsetsState) {
    if (state.listenerInstalled) return

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        applyPaddingInsets(view, insets, state)
        applyMarginInsets(view, insets, state)
        insets
    }
    state.listenerInstalled = true
}

private fun applyPaddingInsets(view: View, insets: WindowInsetsCompat, state: ImmersionInsetsState) {
    if (state.originalPaddingTop == null && state.originalPaddingBottom == null) return

    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    val targetTop =
        (state.originalPaddingTop ?: view.paddingTop) + if (state.addStatusBarPadding) systemBars.top else 0
    val targetBottom =
        (state.originalPaddingBottom
            ?: view.paddingBottom) + if (state.addNavigationBarPadding) systemBars.bottom else 0

    if (view.paddingTop != targetTop || view.paddingBottom != targetBottom) {
        view.setPadding(view.paddingLeft, targetTop, view.paddingRight, targetBottom)
    }
}

private fun applyMarginInsets(view: View, insets: WindowInsetsCompat, state: ImmersionInsetsState) {
    if (state.originalMarginTop == null && state.originalMarginBottom == null) return

    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    val targetTop =
        (state.originalMarginTop ?: lp.topMargin) + if (state.addStatusBarMargin) systemBars.top else 0
    val targetBottom =
        (state.originalMarginBottom
            ?: lp.bottomMargin) + if (state.addNavigationBarMargin) systemBars.bottom else 0

    if (lp.topMargin != targetTop || lp.bottomMargin != targetBottom) {
        lp.topMargin = targetTop
        lp.bottomMargin = targetBottom
        view.layoutParams = lp
    }
}

private fun View.readTopMargin(): Int {
    return (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
}

private fun View.readBottomMargin(): Int {
    return (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
}

// ======================== Padding 避让 ========================

/**
 * 为 View 增加状态栏高度的 PaddingTop
 * @param add 是否增加状态栏高度的避让。如果为 false，则恢复 View 原始的 Padding
 */
fun View.applyStatusBarPadding(add: Boolean = true) {
    val state = obtainInsetsState()
    if (state.originalPaddingTop == null) {
        state.originalPaddingTop = paddingTop
    }
    state.addStatusBarPadding = add
    ensureInsetsListener(state)
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 PaddingBottom
 * @param add 是否增加导航栏高度的避让。如果为 false，则恢复 View 原始的 Padding
 */
fun View.applyNavigationBarPadding(add: Boolean = true) {
    val state = obtainInsetsState()
    if (state.originalPaddingBottom == null) {
        state.originalPaddingBottom = paddingBottom
    }
    state.addNavigationBarPadding = add
    ensureInsetsListener(state)
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时避让顶部状态栏和底部导航栏。
 */
fun View.applySystemBarsPadding(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    val state = obtainInsetsState()
    if (state.originalPaddingTop == null) {
        state.originalPaddingTop = paddingTop
    }
    if (state.originalPaddingBottom == null) {
        state.originalPaddingBottom = paddingBottom
    }
    state.addStatusBarPadding = addStatusBar
    state.addNavigationBarPadding = addNavigationBar
    ensureInsetsListener(state)
    ViewCompat.requestApplyInsets(this)
}

// ======================== Margin 避让 ========================

/**
 * 为 View 增加状态栏高度的 MarginTop
 * @param add 是否增加状态栏高度的避让。如果为 false，则恢复 View 原始的 Margin
 */
fun View.applyStatusBarMargin(add: Boolean = true) {
    val state = obtainInsetsState()
    if (state.originalMarginTop == null) {
        state.originalMarginTop = readTopMargin()
    }
    state.addStatusBarMargin = add
    ensureInsetsListener(state)
    ViewCompat.requestApplyInsets(this)
}

/**
 * 为 View 增加导航栏高度的 MarginBottom
 * @param add 是否增加导航栏高度的避让。如果为 false，则恢复 View 原始的 Margin
 */
fun View.applyNavigationBarMargin(add: Boolean = true) {
    val state = obtainInsetsState()
    if (state.originalMarginBottom == null) {
        state.originalMarginBottom = readBottomMargin()
    }
    state.addNavigationBarMargin = add
    ensureInsetsListener(state)
    ViewCompat.requestApplyInsets(this)
}

/**
 * 同时以 Margin 方式避让顶部状态栏和底部导航栏。
 */
fun View.applySystemBarsMargin(
    addStatusBar: Boolean = true, addNavigationBar: Boolean = true
) {
    val state = obtainInsetsState()
    if (state.originalMarginTop == null) {
        state.originalMarginTop = readTopMargin()
    }
    if (state.originalMarginBottom == null) {
        state.originalMarginBottom = readBottomMargin()
    }
    state.addStatusBarMargin = addStatusBar
    state.addNavigationBarMargin = addNavigationBar
    ensureInsetsListener(state)
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
