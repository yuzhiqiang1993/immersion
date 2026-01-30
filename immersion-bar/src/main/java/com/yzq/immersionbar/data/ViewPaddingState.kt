package com.yzq.immersionbar.data

import android.view.View

internal data class ViewPaddingState(
    val originalTop: Int,
    val originalBottom: Int,
    val consumeTop: Boolean,
    val consumeBottom: Boolean,
    val detachListener: View.OnAttachStateChangeListener
)
