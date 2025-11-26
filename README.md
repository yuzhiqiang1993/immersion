# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersionbar.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersionbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

一个现代化的 Android 沉浸式状态栏库，基于 Android 官方推荐的 Edge-to-Edge 模式设计，提供简洁易用的 API。

## ✨ 特性

- 🚀 **现代化实现**: 基于 Android 15+ 官方推荐的 Edge-to-Edge 模式
- 📱 **全面兼容**: 支持 API 21+ (Android 5.0+)
- 🎨 **简洁 API**: 链式调用，一行代码实现沉浸式
- 🔧 **灵活配置**: 支持状态栏、导航栏独立控制
- 🌙 **智能适配**: 自动处理深色/浅色状态栏文字
- 📊 **实时信息**: 提供状态栏/导航栏高度等系统信息

## 📦 安装

在模块的 `build.gradle.kts` 文件中添加依赖：

```kotlin
dependencies {
    implementation("com.xeonyu:immersionbar:1.0.0")
}
```

## 🚀 快速开始

### 基础用法

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 启用沉浸式模式
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container), // 需要添加状态栏 padding 的根视图，以免覆盖状态栏
            darkStatusBarText = true,  // 深色文字，适合浅色背景
            showStatusBar = true,      // 显示状态栏
            showNavigationBar = true   // 显示导航栏
        )
    }
}
```

## 📖 API 文档

### 核心方法 - ImmersionBar.enable()

启用沉浸式模式的主要方法，一行代码即可完成所有沉浸式设置。

```kotlin
ImmersionBar.enable(
    activity: Activity,                    // 必需 - 目标 Activity 实例
    rootView: View? = null,                // 可选 - 需要应用 Insets 的视图
    darkStatusBarText: Boolean = true,     // 可选 - 状态栏文字是否为深色
    showStatusBar: Boolean = true,         // 可选 - 是否显示状态栏
    showNavigationBar: Boolean = true      // 可选 - 是否显示导航栏
)
```

#### 参数详细说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| **activity** | `Activity` | 必需 | 需要设置沉浸式的 Activity 实例。通常传入 `this` |
| **rootView** | `View?` | `null` | 需要自动添加系统栏 padding 的视图。<br>• **传入值**：自动为该视图添加状态栏和导航栏高度的 padding<br>• **null**：不自动处理 padding，内容会延伸到系统栏下方<br>• **常见用法**：传入根布局或内容容器，避免内容被系统栏遮挡 |
| **darkStatusBarText** | `Boolean` | `true` | 状态栏文字颜色模式。<br>• **true**：深色文字（适合白色/浅色背景）<br>• **false**：浅色文字（适合黑色/深色背景）<br>• **注意**：仅在 Android 6.0+ 生效 |
| **showStatusBar** | `Boolean` | `true` | 是否显示状态栏。<br>• **true**：显示状态栏<br>• **false**：隐藏状态栏<br>• **隐藏时**：可通过下滑手势临时唤出 |
| **showNavigationBar** | `Boolean` | `true` | 是否显示导航栏。<br>• **true**：显示导航栏<br>• **false**：隐藏导航栏<br>• **隐藏时**：可通过上滑手势临时唤出 |

### 辅助方法

#### WindowInsets 相关方法

```kotlin
// 手动为指定视图应用 WindowInsets（添加系统栏高度的 padding）
ImmersionBar.applyWindowInsets(view: View)

// 移除视图的 WindowInsets 监听器
ImmersionBar.removeWindowInsets(view: View)
```

#### 状态栏文字控制

```kotlin
// 动态设置状态栏文字颜色
ImmersionBar.setStatusBarTextDark(
    activity: Activity,        // 目标 Activity
    isDark: Boolean = true     // true=深色文字，false=浅色文字
)
```

#### 系统信息获取

```kotlin
// 获取状态栏高度（像素）
val statusBarHeight = ImmersionBar.getStatusBarHeight(context: Context)

// 获取导航栏高度（像素）
val navigationBarHeight = ImmersionBar.getNavigationBarHeight(context: Context)

// 检查设备是否包含导航栏（返回 Boolean）
val hasNavigationBar = ImmersionBar.hasNavigationBar(context: Context)

// 检查是否为刘海屏设备（返回 Boolean）
val hasNotch = ImmersionBar.hasNotch()
```

## 🎨 使用场景

### 1. 完整沉浸式布局

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 完整沉浸式：内容延伸到系统栏下方，自动处理 padding
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container),
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )
    }
}
```

### 2. 隐藏导航栏

```kotlin
// 隐藏导航栏，保持状态栏可见
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = false  // 隐藏导航栏
)
```

### 3. 深色背景配合浅色文字

```kotlin
// 深色背景使用浅色状态栏文字
ImmersionBar.enable(
    activity = this,
    rootView = binding.rootView,
    darkStatusBarText = false,  // 浅色文字适合深色背景
    showStatusBar = true,
    showNavigationBar = true
)
```

### 4. 在 Fragment 中使用

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 为 Fragment 的根视图应用 WindowInsets
        ImmersionBar.applyWindowInsets(view)
    }
}
```

### 5. 动态切换主题

```kotlin
class ThemedActivity : AppCompatActivity() {
    private fun toggleTheme() {
        val isDarkBackground = currentTheme == Theme.DARK

        // 更新背景颜色
        binding.rootView.setBackgroundColor(
            if (isDarkBackground) Color.BLACK else Color.WHITE
        )

        // 同步更新状态栏文字颜色
        ImmersionBar.setStatusBarTextDark(this, !isDarkBackground)
    }
}
```

## 💡 最佳实践

### 推荐配置组合

```kotlin
// 1. 大多数应用的推荐配置
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = true
)

// 2. 全屏视频/图片查看器
ImmersionBar.enable(
    activity = this,
    rootView = null,  // 不需要 padding，内容完全延伸
    darkStatusBarText = false,
    showStatusBar = false,    // 隐藏状态栏
    showNavigationBar = false // 隐藏导航栏
)

// 3. 阅读类应用（可隐藏导航栏）
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = isLightTheme,
    showStatusBar = true,
    showNavigationBar = false  // 隐藏导航栏获得更大阅读空间
)
```

### 注意事项

1. **rootView 参数**：大多数情况下都应该传入，避免内容被系统栏遮挡
2. **版本兼容**：`darkStatusBarText` 仅在 Android 6.0+ 生效
3. **手势导航**：隐藏的系统栏可通过手势随时唤出
4. **性能优化**：避免在 `onCreate` 外频繁调用 `enable()` 方法


## 📄 许可证

本项目采用 Apache License 2.0 许可证。详情请查看 [LICENSE](LICENSE) 文件。

---

**作者**: [yuzhiqiang](https://github.com/yuzhiqiang1993)
**版本**: 1.0.0
**更新时间**: 2025-11-26
**仓库地址**: https://github.com/yuzhiqiang1993/immersionbar

如果这个项目对您有帮助，请给个 ⭐️ Star 支持一下！

---

**English Documentation**: [README_EN.md](README_EN.md)