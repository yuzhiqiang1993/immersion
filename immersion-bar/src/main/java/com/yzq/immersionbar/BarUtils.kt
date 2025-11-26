package com.yzq.immersionbar

import android.content.Context
import android.os.Build
import android.view.ViewConfiguration


/**
 * @description: 一些工具类
 * @author : yuzhiqiang
 */

internal object BarUtils {

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // 默认值 24dp
            val density = context.resources.displayMetrics.density
            (24 * density + 0.5f).toInt()
        }
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            // 默认值 48dp
            val density = context.resources.displayMetrics.density
            (48 * density + 0.5f).toInt()
        }
    }

    /**
     * 是否有导航栏
     */
    fun hasNavigationBar(context: Context): Boolean {
        val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
        val hasBackKey =
            android.view.KeyCharacterMap.deviceHasKey(android.view.KeyEvent.KEYCODE_BACK)
        return !(hasMenuKey || hasBackKey)
    }

    /**
     * 是否有刘海屏
     */
    fun hasNotch(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}
