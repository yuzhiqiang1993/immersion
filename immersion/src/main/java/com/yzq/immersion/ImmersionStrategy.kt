package com.yzq.immersion

/**
 * 沉浸式策略。
 * - [Auto]：优先保障不同 Android 版本下的可读性与兼容性
 * - [Transparent]：保持系统栏完全透明，追求更强视觉沉浸感
 *
 * 主要差异：
 * - Auto：在旧系统深色导航图标场景按需回退 scrim，并保持对比度保护
 * - Transparent：尽量不做视觉兜底，保持纯透明效果
 */
enum class ImmersionStrategy {
    Auto, Transparent
}
