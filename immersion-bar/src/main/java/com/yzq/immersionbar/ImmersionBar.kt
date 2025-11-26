package com.yzq.immersionbar

import android.app.Activity
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
     * 启用沉浸式模式 (Edge-to-Edge)
     *
     * @param activity 目标 Activity
     * @param rootView 需要应用 Insets 的视图（传入则自动添加 padding，不传则不处理,不传 view 会延伸到状态栏，大多数情况是要传的）
     * @param darkStatusBarText 状态栏文字是否为深色（true=深色文字，适合浅色（例如白色）背景）
     * @param showStatusBar 是否显示状态栏
     * @param showNavigationBar 是否显示导航栏
     */
    fun enable(
        activity: Activity,
        rootView: View? = null,
        darkStatusBarText: Boolean = true,
        showStatusBar: Boolean = true,
        showNavigationBar: Boolean = true
    ) {
        ImmersionDelegate.enableEdgeToEdge(activity)

        if (rootView != null) {
            InsetsDelegate.applyWindowInsets(rootView)
        }

        ImmersionDelegate.updateSystemBars(
            activity,
            showStatusBar,
            showNavigationBar,
            darkStatusBarText
        )
    }

    /**
     * 手动应用 WindowInsets 到指定视图 (添加 Padding)
     */
    fun applyWindowInsets(view: View) {
        InsetsDelegate.applyWindowInsets(view)
    }

    /**
     * 移除 WindowInsets 监听器
     */
    fun removeWindowInsets(view: View) {
        InsetsDelegate.removeWindowInsets(view)
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
    fun hasNotch(): Boolean = BarUtils.hasNotch()
}