package com.yzq.immersion

internal data class ImmersionInsetsState(
    var originalPaddingTop: Int? = null,
    var originalPaddingBottom: Int? = null,
    var originalMarginTop: Int? = null,
    var originalMarginBottom: Int? = null,
    var addStatusBarPadding: Boolean = false,
    var addNavigationBarPadding: Boolean = false,
    var addStatusBarMargin: Boolean = false,
    var addNavigationBarMargin: Boolean = false,
    var listenerInstalled: Boolean = false
)
