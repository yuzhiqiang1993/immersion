package com.yzq.immersionbar.data

internal data class ActivityState(
    val originalNavigationBarColor: Int,
    val immersionEnabled: Boolean = false,
    val paddingInfo: PaddingInfo? = null
)
