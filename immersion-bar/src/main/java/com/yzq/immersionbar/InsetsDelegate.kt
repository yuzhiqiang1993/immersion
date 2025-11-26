package com.yzq.immersionbar

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


/**
 * @description:  WindowInsets 处理类
 * @author : yuzhiqiang
 */

internal object InsetsDelegate {

    /**
     * 应用 WindowInsets 到指定视图（说白了就是给指定的 view 增加状态栏高度的 padding）
     */
    fun applyWindowInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                maxOf(v.paddingLeft, systemBars.left),
                maxOf(v.paddingTop, systemBars.top),
                maxOf(v.paddingRight, systemBars.right),
                maxOf(v.paddingBottom, systemBars.bottom)
            )
            insets
        }
    }

    /**
     * 移除 WindowInsets 监听器，移除 padding
     */
    fun removeWindowInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view, null)
    }
}
