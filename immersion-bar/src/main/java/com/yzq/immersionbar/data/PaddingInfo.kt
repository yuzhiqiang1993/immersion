package com.yzq.immersionbar.data

import android.view.View

internal data class PaddingInfo(
    val view: View,
    val originalTop: Int,
    val originalBottom: Int,
    val consumeTop: Boolean,
    val consumeBottom: Boolean
)
