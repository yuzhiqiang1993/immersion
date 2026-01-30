package com.yzq.immersionbar

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.ViewConfiguration


/**
 * @description: 系统栏相关工具类
 * @author : yuzhiqiang
 */

internal object BarUtils {

    private const val DEFAULT_STATUS_BAR_HEIGHT_DP = 24
    private const val DEFAULT_NAVIGATION_BAR_HEIGHT_DP = 48

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            val density = context.resources.displayMetrics.density
            (DEFAULT_STATUS_BAR_HEIGHT_DP * density + 0.5f).toInt()
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
            val density = context.resources.displayMetrics.density
            (DEFAULT_NAVIGATION_BAR_HEIGHT_DP * density + 0.5f).toInt()
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
    fun hasNotch(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = activity.window.decorView.rootWindowInsets
            return windowInsets?.displayCutout != null
        }
        return false
    }
}
