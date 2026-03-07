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
 * 约定：
 * 1. 顶部避让使用状态栏 inset。
 * 2. 底部避让使用 `navigationBars | ime` 的合并结果（取更大的有效底部 inset）。
 * 3. 同一轴向建议只选择一个容器消费 inset，避免父子叠加导致额外间距。
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

    // 监听只安装一次，后续通过更新 state 控制行为，避免重复叠加监听器。
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
    val targetTop = (state.originalPaddingTop ?: view.paddingTop) + if (state.addStatusBarPadding) systemBars.top else 0
    val targetBottom = (state.originalPaddingBottom ?: view.paddingBottom) + resolveBottomInset(
        insets = insets, includeBottom = state.addNavigationBarPadding
    )

    if (view.paddingTop != targetTop || view.paddingBottom != targetBottom) {
        view.setPadding(view.paddingLeft, targetTop, view.paddingRight, targetBottom)
    }
}

private fun applyMarginInsets(view: View, insets: WindowInsetsCompat, state: ImmersionInsetsState) {
    if (state.originalMarginTop == null && state.originalMarginBottom == null) return

    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
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

// ======================== Padding 避让 ========================

/**
 * 为 View 增加状态栏高度的 PaddingTop
 * @param add 是否增加状态栏高度的避让。false 时恢复到初始记录值
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
 * 为 View 增加底部区域高度的 PaddingBottom（导航栏/输入法取更大值）
 * @param add 是否增加底部区域避让。false 时恢复到初始记录值
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
 * 同时避让顶部状态栏和底部区域（导航栏/输入法取更大值）。
 * 建议作为页面主容器的统一入口，避免在其子 View 再次做同轴向底部避让。
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
