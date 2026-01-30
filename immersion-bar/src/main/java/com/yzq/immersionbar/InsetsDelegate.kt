package com.yzq.immersionbar

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isNotEmpty
import com.yzq.immersionbar.data.ActivityState
import com.yzq.immersionbar.data.PaddingInfo
import com.yzq.immersionbar.data.ViewPaddingState


/**
 * @description: WindowInsets 处理委托, 负责 Activity 级沉浸式状态快照，以及 Activity/View 两条 insets padding 链路
 * @author : yuzhiqiang
 */

internal object InsetsDelegate {

    // Activity 级状态：沉浸式开关、系统栏样式快照、目标 padding 信息
    private val stateMap = mutableMapOf<Activity, ActivityState>()

    // 独立 View 的 insets padding 状态（不依赖 Activity 沉浸式）
    private val viewPaddingMap = mutableMapOf<View, ViewPaddingState>()

    // 当前已注册生命周期监听的 Application
    private var registeredApp: Application? = null

    // Activity 销毁时清理监听器，避免引用失效 View
    private val lifecycleCallbacks = object : ActivityLifecycleCallbacksAdapter() {
        override fun onActivityDestroyed(activity: Activity) {
            stateMap.remove(activity)?.paddingInfo?.let { paddingInfo ->
                ViewCompat.setOnApplyWindowInsetsListener(paddingInfo.view, null)
            }
        }
    }

    fun isImmersionEnabled(activity: Activity): Boolean {
        return stateMap[activity]?.immersionEnabled ?: false
    }

    fun setImmersionEnabled(activity: Activity, enabled: Boolean) {
        ensureRegistered(activity)
        updateState(activity) { state ->
            state.copy(immersionEnabled = enabled)
        }
    }

    // 保存进入沉浸式前的系统栏样式（仅保存一次，避免被覆盖）
    fun saveOriginalSystemBarStyle(activity: Activity) {
        ensureRegistered(activity)
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        updateState(activity) { state ->
            if (state.snapshotCaptured) {
                state
            } else {
                state.copy(
                    originalStatusBarColor = window.statusBarColor,
                    originalNavigationBarColor = window.navigationBarColor,
                    originalLightNavigationBars = controller.isAppearanceLightNavigationBars,
                    snapshotCaptured = true
                )
            }
        }
    }

    // 恢复进入沉浸式前保存的系统栏样式
    fun restoreOriginalSystemBarStyle(activity: Activity) {
        stateMap[activity]?.let { state ->
            if (!state.snapshotCaptured) {
                return@let
            }
            val window = activity.window
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            window.statusBarColor = state.originalStatusBarColor
            activity.window.navigationBarColor = state.originalNavigationBarColor
            controller.isAppearanceLightNavigationBars = state.originalLightNavigationBars
            updateState(activity) { current ->
                current.copy(snapshotCaptured = false)
            }
        }
    }

    // 为 Activity 目标 View 应用状态栏/导航栏 insets padding
    fun handleInsets(
        activity: Activity,
        consumeTop: Boolean,
        consumeBottom: Boolean,
        view: View? = null
    ) {
        val targetView = view ?: findContentView(activity) ?: return
        val currentInfo = stateMap[activity]?.paddingInfo

        if (currentInfo != null && currentInfo.view !== targetView) {
            clearPaddingInfo(currentInfo)
        }

        val newInfo = if (currentInfo?.view === targetView) {
            currentInfo.copy(consumeTop = consumeTop, consumeBottom = consumeBottom)
        } else {
            PaddingInfo(
                view = targetView,
                originalTop = targetView.paddingTop,
                originalBottom = targetView.paddingBottom,
                consumeTop = consumeTop,
                consumeBottom = consumeBottom
            )
        }

        updateState(activity) { state ->
            state.copy(paddingInfo = newInfo)
        }
        setupWindowInsetsListener(activity, targetView)
        ViewCompat.requestApplyInsets(targetView)
    }

    // 清理 Activity 目标 View 的 insets 监听并恢复原始 padding
    fun clearInsets(activity: Activity) {
        val paddingInfo = stateMap[activity]?.paddingInfo ?: return
        clearPaddingInfo(paddingInfo)
        updateState(activity) { state ->
            state.copy(paddingInfo = null)
        }
    }

    /**
     * 为指定 View 应用 WindowInsets padding（独立于 Activity 的沉浸式设置）
     *
     * View detach 时自动清理，无需手动调用 clearViewInsetsPadding。
     */
    fun applyViewInsetsPadding(view: View, consumeTop: Boolean, consumeBottom: Boolean) {
        val existingState = viewPaddingMap[view]

        if (existingState != null) {
            val updatedState = existingState.copy(consumeTop = consumeTop, consumeBottom = consumeBottom)
            viewPaddingMap[view] = updatedState
        } else {
            val detachListener = createDetachListener(view)
            val newState = ViewPaddingState(
                originalTop = view.paddingTop,
                originalBottom = view.paddingBottom,
                consumeTop = consumeTop,
                consumeBottom = consumeBottom,
                detachListener = detachListener
            )
            viewPaddingMap[view] = newState
            view.addOnAttachStateChangeListener(detachListener)
        }

        setupViewWindowInsetsListener(view)
        ViewCompat.requestApplyInsets(view)
    }

    /**
     * 清除指定 View 的 WindowInsets padding
     *
     * 通常无需手动调用，View detach 时会自动清理。
     */
    fun clearViewInsetsPadding(view: View) {
        val state = viewPaddingMap.remove(view) ?: return
        view.removeOnAttachStateChangeListener(state.detachListener)
        ViewCompat.setOnApplyWindowInsetsListener(view, null)
        view.setPadding(
            view.paddingLeft,
            state.originalTop,
            view.paddingRight,
            state.originalBottom
        )
    }

    private fun createDetachListener(view: View): View.OnAttachStateChangeListener {
        return object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(attachedView: View) {}

            override fun onViewDetachedFromWindow(detachedView: View) {
                clearViewInsetsPadding(detachedView)
            }
        }
    }

    private fun setupViewWindowInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { targetView, insets ->
            val state = viewPaddingMap[targetView]
            if (state == null) {
                return@setOnApplyWindowInsetsListener insets
            }

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topPadding = if (state.consumeTop) {
                systemBars.top
            } else {
                0
            }
            val bottomPadding = if (state.consumeBottom) {
                systemBars.bottom
            } else {
                0
            }
            val newTop = state.originalTop + topPadding
            val newBottom = state.originalBottom + bottomPadding

            if (targetView.paddingTop != newTop || targetView.paddingBottom != newBottom) {
                targetView.setPadding(
                    targetView.paddingLeft,
                    newTop,
                    targetView.paddingRight,
                    newBottom
                )
            }
            insets
        }
    }

    // 确保每个 Application 仅注册一次生命周期监听
    private fun ensureRegistered(activity: Activity) {
        val app = activity.application
        if (registeredApp !== app) {
            registeredApp?.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
            app.registerActivityLifecycleCallbacks(lifecycleCallbacks)
            registeredApp = app
        }
    }

    // 获取或创建 Activity 状态快照
    private fun getOrCreateState(activity: Activity): ActivityState {
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        return stateMap.getOrPut(activity) {
            ActivityState(
                originalStatusBarColor = window.statusBarColor,
                originalNavigationBarColor = window.navigationBarColor,
                originalLightNavigationBars = controller.isAppearanceLightNavigationBars
            )
        }
    }

    private fun updateState(activity: Activity, transform: (ActivityState) -> ActivityState) {
        stateMap[activity] = transform(getOrCreateState(activity))
    }

    // 默认取 content 根布局下第一个子 View 作为 insets 目标
    private fun findContentView(activity: Activity): View? {
        val contentView = activity.findViewById<View>(android.R.id.content) as? ViewGroup
        return contentView?.takeIf { viewGroup ->
            viewGroup.isNotEmpty()
        }?.getChildAt(0)
    }

    // Activity 级 insets 回调：基于配置叠加 top/bottom padding
    private fun setupWindowInsetsListener(activity: Activity, view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { targetView, insets ->
            val paddingInfo = stateMap[activity]?.paddingInfo
            if (paddingInfo == null || paddingInfo.view !== targetView) {
                return@setOnApplyWindowInsetsListener insets
            }

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topPadding = if (paddingInfo.consumeTop) {
                systemBars.top
            } else {
                0
            }
            val bottomPadding = if (paddingInfo.consumeBottom) {
                systemBars.bottom
            } else {
                0
            }
            val newTop = paddingInfo.originalTop + topPadding
            val newBottom = paddingInfo.originalBottom + bottomPadding

            if (targetView.paddingTop != newTop || targetView.paddingBottom != newBottom) {
                targetView.setPadding(
                    targetView.paddingLeft,
                    newTop,
                    targetView.paddingRight,
                    newBottom
                )
            }
            insets
        }
    }

    // 清理 insets 监听并恢复记录的原始 padding
    private fun clearPaddingInfo(paddingInfo: PaddingInfo) {
        ViewCompat.setOnApplyWindowInsetsListener(paddingInfo.view, null)
        paddingInfo.view.setPadding(
            paddingInfo.view.paddingLeft,
            paddingInfo.originalTop,
            paddingInfo.view.paddingRight,
            paddingInfo.originalBottom
        )
    }
}
