package com.yzq.immersionbar

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isNotEmpty


/**
 * WindowInsets 处理
 */
internal object InsetsDelegate {

    private data class ViewPaddingInfo(
        val view: View,
        val originalTop: Int,
        val originalBottom: Int,
        val consumeTop: Boolean,
        val consumeBottom: Boolean
    )

    private val insetsViewMap = mutableMapOf<Activity, ViewPaddingInfo>()
    private val originalNavigationBarColorMap = mutableMapOf<Activity, Int>()
    private val immersionEnabledMap = mutableMapOf<Activity, Boolean>()

    /**
     * 检查沉浸式是否已启用
     */
    fun isImmersionEnabled(activity: Activity): Boolean {
        return immersionEnabledMap[activity] ?: false
    }

    /**
     * 设置沉浸式启用状态
     */
    fun setImmersionEnabled(activity: Activity, enabled: Boolean) {
        immersionEnabledMap[activity] = enabled
    }

    /**
     * 保存原始导航栏颜色
     */
    fun saveOriginalNavigationBarColor(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            originalNavigationBarColorMap[activity] = activity.window.navigationBarColor
        }
    }

    /**
     * 恢复原始导航栏颜色
     */
    fun restoreOriginalNavigationBarColor(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val originalColor = originalNavigationBarColorMap.remove(activity)
            if (originalColor != null) {
                activity.window.navigationBarColor = originalColor
            }
        }
    }

    /**
     * 处理 View 的 WindowInsets (Padding)
     *
     * @param activity Activity
     * @param consumeTop 是否消耗顶部 insets (状态栏)
     * @param consumeBottom 是否消耗底部 insets (导航栏)
     * @param view 目标 View，如果不传则自动使用根布局的第一个子 view
     */
    fun handleInsets(activity: Activity, consumeTop: Boolean, consumeBottom: Boolean, view: View? = null) {
        val targetView = view ?: run {
            val contentView = activity.findViewById<View>(android.R.id.content) as? ViewGroup
            contentView?.takeIf { it.isNotEmpty() }?.getChildAt(0)
        }

        targetView?.let { v ->
            val existingInfo = insetsViewMap[activity]

            if (existingInfo != null && existingInfo.view === v) {
                // 更新配置
                insetsViewMap[activity] = existingInfo.copy(consumeTop = consumeTop, consumeBottom = consumeBottom)
            } else {
                // 新的 view，保存原始 padding
                insetsViewMap[activity] = ViewPaddingInfo(v, v.paddingTop, v.paddingBottom, consumeTop, consumeBottom)
            }

            // 必须重新请求应用 insets，以确保更新立即生效
            ViewCompat.requestApplyInsets(v)
            setupWindowInsetsListener(activity, v)
        }
    }

    /**
     * 设置 WindowInsets 监听器
     */
    private fun setupWindowInsetsListener(activity: Activity, view: View) {
        // 这里不能直接从 map 获取 info，因为闭包会捕获旧的 info 对象
        // 应该在回调中获取最新的 info

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val info = insetsViewMap[activity]
            if (info == null || info.view !== v) {
                return@setOnApplyWindowInsetsListener insets
            }

            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 基于原始 padding 计算新的 padding
            val newTop = info.originalTop + if (info.consumeTop) systemBarsInsets.top else 0
            val newBottom = info.originalBottom + if (info.consumeBottom) systemBarsInsets.bottom else 0

            if (v.paddingTop != newTop || v.paddingBottom != newBottom) {
                v.setPadding(
                    v.paddingLeft,
                    newTop,
                    v.paddingRight,
                    newBottom
                )
            }

            insets
        }
    }

    /**
     * 清除 insets 监听器
     */
    fun clearInsets(activity: Activity) {
        insetsViewMap.remove(activity)?.let { info ->
            ViewCompat.setOnApplyWindowInsetsListener(info.view, null)
            info.view.setPadding(
                info.view.paddingLeft,
                info.originalTop,
                info.view.paddingRight,
                info.originalBottom
            )
        }
    }

}
