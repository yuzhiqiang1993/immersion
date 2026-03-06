package com.yzq.immersion

/**
 * 沉浸式策略。
 * - [Auto]：优先保障不同 Android 版本下的可读性与兼容性
 * - [Transparent]：保持系统栏完全透明，追求更强视觉沉浸感
 */
enum class ImmersionStrategy {
    Auto,
    Transparent
}
