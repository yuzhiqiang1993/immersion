package com.yzq.immersion

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * View 级避让扩展，用于精细化系统栏避让控制。
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

    // 只安装一次监听器，通过更新 state 动态控制行为。
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val resolvedInsets = state.chainedInsetsListener?.onApplyWindowInsets(view, insets) ?: insets
        applyPaddingInsets(view, resolvedInsets, state)
        applyMarginInsets(view, resolvedInsets, state)
        resolvedInsets
    }
    state.listenerInstalled = true
}

private fun applyPaddingInsets(view: View, insets: WindowInsetsCompat, state: ImmersionInsetsState) {
    if (state.originalPaddingTop == null && state.originalPaddingBottom == null) return

    state.originalPaddingTop = rebaseOriginalInsetAwareValue(
        original = state.originalPaddingTop,
        lastApplied = state.lastAppliedPaddingTop,
        current = view.paddingTop
    )
    state.originalPaddingBottom = rebaseOriginalInsetAwareValue(
        original = state.originalPaddingBottom,
        lastApplied = state.lastAppliedPaddingBottom,
        current = view.paddingBottom
    )

    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    val targetTop = (state.originalPaddingTop ?: view.paddingTop) + if (state.addStatusBarPadding) systemBars.top else 0
    val targetBottom = (state.originalPaddingBottom ?: view.paddingBottom) + resolveBottomInset(
        insets = insets, includeBottom = state.addNavigationBarPadding
    )

    if (view.paddingTop != targetTop || view.paddingBottom != targetBottom) {
        view.setPadding(view.paddingLeft, targetTop, view.paddingRight, targetBottom)
    }
    state.lastAppliedPaddingTop = targetTop
    state.lastAppliedPaddingBottom = targetBottom
}

private fun applyMarginInsets(view: View, insets: WindowInsetsCompat, state: ImmersionInsetsState) {
    if (state.originalMarginTop == null && state.originalMarginBottom == null) return

    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
    state.originalMarginTop = rebaseOriginalInsetAwareValue(
        original = state.originalMarginTop,
        lastApplied = state.lastAppliedMarginTop,
        current = lp.topMargin
    )
    state.originalMarginBottom = rebaseOriginalInsetAwareValue(
        original = state.originalMarginBottom,
        lastApplied = state.lastAppliedMarginBottom,
        current = lp.bottomMargin
    )
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    val targetTop = (state.originalMarginTop ?: lp.topMargin) + if (state.addStatusBarMargin) systemBars.top else 0
    val targetBottom = (state.originalMarginBottom ?: lp.bottomMargin) + resolveBottomInset(
        insets = insets, includeBottom = state.addNavigationBarMargin
    )

    if (lp.topMargin != targetTop || lp.bottomMargin != targetBottom) {
        lp.topMargin = targetTop
        lp.bottomMargin = targetBottom
        view.layoutParams = lp
    }
    state.lastAppliedMarginTop = targetTop
    state.lastAppliedMarginBottom = targetBottom
}

private fun View.readTopMargin(): Int {
    return (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
}

private fun View.readBottomMargin(): Int {
    return (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
}

private fun resolveBottomInset(
    insets: WindowInsetsCompat, includeBottom: Boolean
): Int {
    if (!includeBottom) return 0
    // 合并取值：键盘显示时优先使用 IME；键盘隐藏时回落到导航栏。
    val bottomInsetsType = WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.ime()
    return insets.getInsets(bottomInsetsType).bottom
}

private fun rebaseOriginalInsetAwareValue(
    original: Int?,
    lastApplied: Int?,
    current: Int
): Int? {
    if (original == null) return null
    if (lastApplied == null) return current
    if (current == lastApplied) return original
    return original + (current - lastApplied)
}

private fun View.captureExistingInsetsListener(): OnApplyWindowInsetsListener? {
    return getTag(androidx.core.R.id.tag_on_apply_window_listener) as? OnApplyWindowInsetsListener
}

// ======================== Padding 避让 ========================

/**
 * 为 View 增加状态栏高度的 PaddingTop。
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
 * 为 View 增加底部区域高度的 PaddingBottom。
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
 * 同时避让顶部状态栏和底部区域（导航栏/输入法）。
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
 * @param add 是否增加状态栏高度的避让。false 时恢复到初始记录值
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
 * 为 View 增加底部区域高度的 MarginBottom（导航栏/输入法取更大值）
 * @param add 是否增加底部区域避让。false 时恢复到初始记录值
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
 * 同时以 Margin 方式避让顶部状态栏和底部区域（导航栏/输入法取更大值）。
 * 适用于需要“压缩可用空间”而非“在内容内留白”的场景。
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
