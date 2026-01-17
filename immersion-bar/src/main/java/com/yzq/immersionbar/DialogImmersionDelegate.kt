package com.yzq.immersionbar

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Dialog 沉浸式逻辑委托类
 *
 * @author : yuzhiqiang
 */
internal object DialogImmersionDelegate {

    /**
     * 启用全屏 Dialog 沉浸式模式
     *
     * @param dialog 目标 Dialog
     * @param darkStatusBarText 状态栏文字是否为深色
     */
    fun enableFullScreen(dialog: Dialog, darkStatusBarText: Boolean = true) {
        val window = dialog.window ?: return

        // 设置全屏布局
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // 内容延伸到系统栏下方
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 适配刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }

        // 设置系统栏透明
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // 禁用系统强制对比度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        // 设置状态栏文字颜色
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = darkStatusBarText
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * 启用底部弹窗 Dialog 沉浸式模式
     * 导航栏透明，内容延伸到导航栏下方
     *
     * @param dialog 目标 Dialog
     */
    fun enableBottomSheet(dialog: Dialog) {
        val window = dialog.window ?: return

        // 内容延伸到导航栏下方
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 设置导航栏透明
        window.navigationBarColor = Color.TRANSPARENT

        // 禁用导航栏对比度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        // 设置导航栏文字为浅色（适配大多数底部弹窗场景）
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightNavigationBars = false
    }

    /**
     * 为 Dialog 内容添加 insets padding
     *
     * @param dialog 目标 Dialog
     * @param paddingStatusBar 是否添加顶部 padding 避开状态栏
     * @param paddingNavigationBar 是否添加底部 padding 避开导航栏
     */
    fun handleInsets(dialog: Dialog, paddingStatusBar: Boolean, paddingNavigationBar: Boolean) {
        val window = dialog.window ?: return
        val contentView = window.decorView.findViewById<View>(android.R.id.content) as? ViewGroup
        val targetView = contentView?.getChildAt(0) ?: return

        val originalTop = targetView.paddingTop
        val originalBottom = targetView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(targetView) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val newTop = originalTop + if (paddingStatusBar) systemBarsInsets.top else 0
            val newBottom = originalBottom + if (paddingNavigationBar) systemBarsInsets.bottom else 0

            if (v.paddingTop != newTop || v.paddingBottom != newBottom) {
                v.setPadding(v.paddingLeft, newTop, v.paddingRight, newBottom)
            }

            insets
        }

        ViewCompat.requestApplyInsets(targetView)
    }

    /**
     * 设置 Dialog 状态栏文字颜色
     *
     * @param dialog 目标 Dialog
     * @param isDark true=深色文字（适合浅色背景），false=浅色文字（适合深色背景）
     */
    fun setStatusBarTextDark(dialog: Dialog, isDark: Boolean) {
        val window = dialog.window ?: return
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = isDark
    }
}
