package com.yzq.immersionbar

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isNotEmpty
import com.yzq.immersionbar.data.ActivityState
import com.yzq.immersionbar.data.PaddingInfo
import com.yzq.immersionbar.data.ViewPaddingState

/**
 * WindowInsets 处理委托
 */
internal object InsetsDelegate {

    private val stateMap = mutableMapOf<Activity, ActivityState>()
    private val viewPaddingMap = mutableMapOf<View, ViewPaddingState>()
    private var registeredApp: Application? = null

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

    fun saveOriginalNavigationBarColor(activity: Activity) {
        ensureRegistered(activity)
        getOrCreateState(activity)
    }

    fun restoreOriginalNavigationBarColor(activity: Activity) {
        stateMap[activity]?.let { state ->
            activity.window.navigationBarColor = state.originalNavigationBarColor
        }
    }

    fun handleInsets(
        activity: Activity,
        consumeTop: Boolean,
        consumeBottom: Boolean,
        view: View? = null
    ) {
        val targetView = view ?: findContentView(activity) ?: return
        val currentInfo = stateMap[activity]?.paddingInfo

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

    fun clearInsets(activity: Activity) {
        val paddingInfo = stateMap[activity]?.paddingInfo ?: return
        ViewCompat.setOnApplyWindowInsetsListener(paddingInfo.view, null)
        paddingInfo.view.setPadding(
            paddingInfo.view.paddingLeft,
            paddingInfo.originalTop,
            paddingInfo.view.paddingRight,
            paddingInfo.originalBottom
        )
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

    private fun ensureRegistered(activity: Activity) {
        val app = activity.application
        if (registeredApp !== app) {
            registeredApp?.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
            app.registerActivityLifecycleCallbacks(lifecycleCallbacks)
            registeredApp = app
        }
    }

    private fun getOrCreateState(activity: Activity): ActivityState {
        return stateMap.getOrPut(activity) {
            ActivityState(originalNavigationBarColor = activity.window.navigationBarColor)
        }
    }

    private fun updateState(activity: Activity, transform: (ActivityState) -> ActivityState) {
        stateMap[activity] = transform(getOrCreateState(activity))
    }

    private fun findContentView(activity: Activity): View? {
        val contentView = activity.findViewById<View>(android.R.id.content) as? ViewGroup
        return contentView?.takeIf { viewGroup ->
            viewGroup.isNotEmpty()
        }?.getChildAt(0)
    }

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
}
