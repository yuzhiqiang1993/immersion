package com.yzq.immersionbar

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View


/**
 * @description: 状态栏沉浸式
 * Andorid 15开始，官方强制推荐使用Edge-to-Edge，因此，不提供更改状态栏颜色的 api 了
 * 应该让内容的颜色蔓延到状态栏以达到沉浸式的效果
 * @author : yuzhiqiang
 */

object ImmersionBar {

    /**
     * 启用沉浸式模式
     *
     * @param activity 目标 Activity
     * @param paddingStatusBar 是否添加顶部 padding 避开状态栏（true=添加 padding 避开，false=蔓延到状态栏，默认 false）
     * @param paddingNavigationBar 是否添加底部 padding 避开导航栏（true=添加 padding 避开，false=蔓延到导航栏，默认 true）
     * @param darkStatusBarText 状态栏文字是否为深色（true=深色文字，适合浅色背景）
     * @param showStatusBar 是否显示状态栏
     * @param showNavigationBar 是否显示导航栏
     */
    fun enable(
        activity: Activity,
        paddingStatusBar: Boolean = false,
        paddingNavigationBar: Boolean = true,
        darkStatusBarText: Boolean = true,
        showStatusBar: Boolean = true,
        showNavigationBar: Boolean = true
    ) {
        // 保存原始导航栏颜色，以便 disable 时恢复
        InsetsDelegate.saveOriginalNavigationBarColor(activity)

        ImmersionDelegate.enableEdgeToEdge(activity, !paddingNavigationBar)

        ImmersionDelegate.updateSystemBars(
            activity,
            showStatusBar = showStatusBar,
            showNavigationBar = showNavigationBar,
            darkStatusBarText = darkStatusBarText
        )

        // 处理状态栏 insets
        // 处理 insets
        InsetsDelegate.handleInsets(activity, paddingStatusBar, paddingNavigationBar)

    }


    /**
     * 设置状态栏文字颜色
     *
     * @param isDark true=深色文字（适合浅色背景），false=浅色文字（适合深色背景）
     */
    fun setStatusBarTextDark(activity: Activity, isDark: Boolean = true) {
        ImmersionDelegate.setStatusBarLightMode(activity, isDark)
    }

    /**
     * 禁用沉浸式模式，恢复到默认状态（内容不延伸到状态栏）
     *
     * @param activity 目标 Activity
     */
    fun disable(activity: Activity) {
        ImmersionDelegate.disableEdgeToEdge(activity)
    }

    /**
     * 更新系统栏显示/隐藏状态
     *
     * @param activity 目标 Activity
     * @param showStatusBar 是否显示状态栏
     * @param showNavigationBar 是否显示导航栏
     * @param darkStatusBarText 状态栏文字是否为深色
     */
    fun updateSystemBars(
        activity: Activity,
        showStatusBar: Boolean = true,
        showNavigationBar: Boolean = true,
        darkStatusBarText: Boolean = true
    ) {
        ImmersionDelegate.updateSystemBars(
            activity,
            showStatusBar = showStatusBar,
            showNavigationBar = showNavigationBar,
            darkStatusBarText = darkStatusBarText
        )
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int = BarUtils.getStatusBarHeight(context)

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int = BarUtils.getNavigationBarHeight(context)

    /**
     * 是否有导航栏
     */
    fun hasNavigationBar(context: Context): Boolean = BarUtils.hasNavigationBar(context)

    /**
     * 是否有刘海屏
     */
    fun hasNotch(activity: Activity): Boolean = BarUtils.hasNotch(activity)

    /**
     * 启用全屏 Dialog 沉浸式模式
     * 内容延伸到状态栏和导航栏下方，系统栏透明
     *
     * @param dialog 目标 Dialog
     * @param darkStatusBarText 状态栏文字是否为深色（true=深色文字，适合浅色背景）
     * @param paddingStatusBar 是否添加顶部 padding 避开状态栏
     * @param paddingNavigationBar 是否添加底部 padding 避开导航栏
     */
    fun enableFullScreenDialog(
        dialog: Dialog,
        darkStatusBarText: Boolean = true,
        paddingStatusBar: Boolean = false,
        paddingNavigationBar: Boolean = false
    ) {
        DialogImmersionDelegate.enableFullScreen(dialog, darkStatusBarText)
        if (paddingStatusBar || paddingNavigationBar) {
            DialogImmersionDelegate.handleInsets(dialog, paddingStatusBar, paddingNavigationBar)
        }
    }

    /**
     * 启用底部弹窗 Dialog 沉浸式模式
     * 导航栏透明，内容延伸到导航栏下方
     *
     * @param dialog 目标 Dialog（通常是 BottomSheetDialog）
     * @param paddingNavigationBar 是否添加底部 padding 避开导航栏
     */
    fun enableBottomSheetDialog(
        dialog: Dialog,
        paddingNavigationBar: Boolean = false
    ) {
        DialogImmersionDelegate.enableBottomSheet(dialog)
        if (paddingNavigationBar) {
            DialogImmersionDelegate.handleInsets(dialog, paddingStatusBar = false, paddingNavigationBar = true)
        }
    }

    /**
     * 设置 Dialog 状态栏文字颜色
     *
     * @param dialog 目标 Dialog
     * @param isDark true=深色文字（适合浅色背景），false=浅色文字（适合深色背景）
     */
    fun setDialogStatusBarTextDark(dialog: Dialog, isDark: Boolean = true) {
        DialogImmersionDelegate.setStatusBarTextDark(dialog, isDark)
    }

    /**
     * 为指定 View 应用系统栏 padding
     *
     * View detach 时自动清理，无需手动调用 clearInsetsPadding。
     * 此方法独立于 enable() / disable()，可单独使用。
     *
     * @param view 目标 View
     * @param paddingStatusBar 是否添加顶部 padding 避开状态栏
     * @param paddingNavigationBar 是否添加底部 padding 避开导航栏
     */
    fun applyInsetsPadding(
        view: View,
        paddingStatusBar: Boolean = true,
        paddingNavigationBar: Boolean = true
    ) {
        InsetsDelegate.applyViewInsetsPadding(view, paddingStatusBar, paddingNavigationBar)
    }

    /**
     * 清除指定 View 的系统栏 padding
     *
     * 通常无需手动调用，View detach 时会自动清理。
     *
     * @param view 目标 View
     */
    fun clearInsetsPadding(view: View) {
        InsetsDelegate.clearViewInsetsPadding(view)
    }
}
