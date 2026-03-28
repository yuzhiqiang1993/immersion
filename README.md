# Immersion

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersion.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

一个基于 Android Edge-to-Edge 的轻量沉浸式库，提供 Activity Window 配置和 View Insets 避让扩展。

## 特性

- **现代方案**：基于官方 Edge-to-Edge 模式设计，兼容 API 23+。
- **对称 API**：Activity 与 Fragment 享有完全一致的沉浸式配置扩展。
- **智能推断**：支持基于系统栏（状态栏/导航栏）下方背景色的**实时亮度自动反色**。
- **全能避让**：View 级 Insets 避让（Padding / Margin），底部避让自动合并导航栏与键盘。

## 安装

在模块的 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.xeonyu:immersion:x.x.x") // 请替换为最新版本
}
```

最新版本：<https://central.sonatype.com/artifact/com.xeonyu/immersion>

## 快速开始

### 1) Activity 开启

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 开启沉浸式
        setupImmersion()

        // 避让系统栏
        binding.root.applySystemBarsPadding()
    }
}
```

### 2) Fragment 开启

```kotlin
class SimpleFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Fragment 中也可以像在 Activity 中一样调用
        setupImmersion(isStatusBarDarkFont = true)
        
        // View 避让
        view.applyStatusBarPadding()
    }
}
```

## 核心 API 详解

### 基础沉浸式设置

针对 Activity 或 Fragment 调用 `setupImmersion`：

```kotlin
setupImmersion(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    isStatusBarDarkFont: Boolean? = null,    // null 表示根据背景自动推断
    isNavigationBarDarkIcon: Boolean? = null // null 表示根据背景自动推断
)
```

或使用全量配置对象：

```kotlin
setupImmersion(
    ImmersionOptions(
        strategy = ImmersionStrategy.Transparent // 追求彻底透明
    )
)
```

### 动态外观刷新

```kotlin
// 手动设置图标深浅色
setStatusBarDarkFont(true)
setNavigationBarDarkIcon(false)

// 根据背景自动同步（适合滑动过程中调用）
syncStatusBarFontWithBg() 
syncSystemBarsWithBg()    // 同时同步状态栏和导航栏
```

### View Insets 避让

支持 **Padding** 与 **Margin** 两种模式，底部避让会自动处理 `navigationBars | ime` 的合并逻辑。

```kotlin
// Padding 模式
view.applyStatusBarPadding()
view.applyNavigationBarPadding()
view.applySystemBarsPadding(addStatusBar = true, addNavigationBar = true)

// Margin 模式
view.applyStatusBarMargin()
view.applyNavigationBarMargin()
view.applySystemBarsMargin()
```

## 注意事项

- **调用时机**：建议在 `setContentView` 后调用 `setupImmersion()`。
- **自动推断**：由于推断是基于 View 树渲染采样的，建议在 View 布局完成后（或在滑动监听中）调用 `sync...WithBg()`。
- **导航栏限制**：导航栏深色图标（Light Navigation Bars）功能仅在 Android 8.0 (API 26) 以上生效。
- **零分配机制**：内核在高频采样时会复用局部对象池以优化性能，该机制仅在主线程运行，请勿在子线程调用采样 API。

## 许可证

本项目采用 Apache License 2.0。gationBar = true)
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
- 自动深浅色推断是启发式，当前基于系统栏区域的低分辨率渲染采样结果判断亮度。
- `syncStatusBarFontWithBg()` 适合在 Fragment 切换、折叠 AppBar、动态换肤后调用。
- `syncNavBarIconWithBg()` / `syncSystemBarsWithBg()` 适合在底部背景联动变化时调用。
- 对于视频、相机或独立 `Surface` 内容，建议显式指定系统栏深浅色。
- 导航栏深色图标仅 Android 8.0（API 26）及以上支持。

## 许可证

本项目采用 Apache License 2.0，详见 [LICENSE](LICENSE)。
