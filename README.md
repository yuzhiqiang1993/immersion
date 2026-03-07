# Immersion

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersion.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

一个基于 Android Edge-to-Edge 的轻量沉浸式库，提供 Activity Window 配置和 View Insets 避让扩展。

## 特性

- 基于官方 Edge-to-Edge 方案（API 23+）
- Activity 级沉浸式一行开启
- View 级 Insets 避让（Padding / Margin）
- 底部避让自动合并导航栏与键盘（`navigationBars | ime`）
- 支持状态栏/导航栏图标深浅色控制
- 支持运行时动态切换系统栏图标深浅色
- 支持颜色亮度判断与工具扩展

## 安装

在模块的 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.xeonyu:immersion:x.x.x") // 请替换为 Maven Central 最新版本
}
```

最新版本：<https://central.sonatype.com/artifact/com.xeonyu/immersion>

## 快速开始

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Window 层：开启沉浸式
        setupImmersion()

        // 2) View 层：根布局避让状态栏 + 底部区域（导航栏/键盘）
        findViewById<View>(android.R.id.content).applySystemBarsPadding(
            addStatusBar = true,
            addNavigationBar = true
        )
    }
}
```

## 核心 API

### Activity

```kotlin
setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null
)
```

```kotlin
setupImmersion(
    ImmersionOptions(
        showStatusBar = true,
        showNavigationBar = true,
        isStatusBarDark = null,
        isNavigationBarDark = null,
        strategy = ImmersionStrategy.Transparent
    )
)
```

### 动态切换系统栏图标深浅色

```kotlin
// 仅改状态栏
setStatusBarDark(true)

// 仅改导航栏（深色图标仅 API 26+ 生效）
setNavigationBarDark(false)

// 同时改状态栏和导航栏；传 null 表示保持当前值不变
setSystemBarDarkMode(
    isStatusBarDark = true,
    isNavigationBarDark = true
)
```

### Strategy

- `ImmersionStrategy.Transparent`：视觉沉浸优先，系统栏保持透明，Android 10+ 关闭对比度保护。
- `ImmersionStrategy.Auto`：兼容可读性优先，旧系统按需回退导航栏底色，Android 10+ 保留对比度保护。

## View Insets API

### Padding 方案

```kotlin
view.applyStatusBarPadding(add = true)
view.applyNavigationBarPadding(add = true) // 底部自动包含导航栏/键盘
view.applySystemBarsPadding(addStatusBar = true, addNavigationBar = true)
```

### Margin 方案

```kotlin
view.applyStatusBarMargin(add = true)
view.applyNavigationBarMargin(add = true) // 底部自动包含导航栏/键盘
view.applySystemBarsMargin(addStatusBar = true, addNavigationBar = true)
```

### 说明

- 顶部避让使用状态栏 inset。
- 底部避让使用 `navigationBars | ime` 合并结果。
- 同一轴向建议只让一个容器消费 inset，避免父子叠加产生额外间距。

## 常用扩展

```kotlin
val statusBarPx = activity.statusBarHeight
val navBarPx = activity.navigationBarHeight
val toolbarPx = context.actionBarHeight

val isLight = colorInt.isLightColor()
val darker = colorInt.darkenColor(0.7f)
```

## 注意事项

- 建议在 `setContentView` 后调用 `setupImmersion()`。
- `setupImmersion()` 只处理 Window，不会自动处理内容 View 的避让。
- 自动深浅色推断是启发式（基于可提取背景色），复杂背景建议显式传参。
- 导航栏深色图标仅 Android 8.0（API 26）及以上支持。
- 当前版本不再提供 Dialog / BottomSheet 专用沉浸式扩展。

## 许可证

本项目采用 Apache License 2.0，详见 [LICENSE](LICENSE)。
