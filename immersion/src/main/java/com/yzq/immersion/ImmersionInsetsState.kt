package com.yzq.immersion

import androidx.core.view.OnApplyWindowInsetsListener

internal data class ImmersionInsetsState(
    var originalPaddingTop: Int? = null,
    var originalPaddingBottom: Int? = null,
    var originalMarginTop: Int? = null,
    var originalMarginBottom: Int? = null,
    var lastAppliedPaddingTop: Int? = null,
    var lastAppliedPaddingBottom: Int? = null,
    var lastAppliedMarginTop: Int? = null,
    var lastAppliedMarginBottom: Int? = null,
    var addStatusBarPadding: Boolean = false,
    var addNavigationBarPadding: Boolean = false,
    var addStatusBarMargin: Boolean = false,
    var addNavigationBarMargin: Boolean = false,
    var chainedInsetsListener: OnApplyWindowInsetsListener? = null,
    var listenerInstalled: Boolean = false
)
