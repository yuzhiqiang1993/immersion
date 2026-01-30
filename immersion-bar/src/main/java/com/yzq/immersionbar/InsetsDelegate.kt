package com.yzq.immersionbar

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isNotEmpty

/**
 * WindowInsets 处理委托
 */
internal object InsetsDelegate {

    private data class ActivityState(
        val originalNavigationBarColor: Int,
        val immersionEnabled: Boolean = false,
        val paddingInfo: PaddingInfo? = null
    )

    private data class PaddingInfo(
        val view: View,
        val originalTop: Int,
        val originalBottom: Int,
        val consumeTop: Boolean,
        val consumeBottom: Boolean
    )

    private val stateMap = mutableMapOf<Activity, ActivityState>()
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
