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
 */
fun View.applyStatusBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        view.setPadding(
            view.paddingLeft,
            statusBarInsets.top,
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
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.setPadding(
            view.paddingLeft,
            view.paddingTop,
            view.paddingRight,
            navBarInsets.bottom
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
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topOffset = if (addStatusBar) systemBars.top else 0
        val bottomOffset = if (addNavigationBar) systemBars.bottom else 0

        view.setPadding(
            view.paddingLeft,
            topOffset,
            view.paddingRight,
            bottomOffset
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
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.topMargin = statusBarInsets.top
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
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.bottomMargin = navBarInsets.bottom
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
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val topOffset = if (addStatusBar) systemBars.top else 0
        val bottomOffset = if (addNavigationBar) systemBars.bottom else 0

        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        if (lp != null) {
            lp.topMargin = topOffset
            lp.bottomMargin = bottomOffset
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
