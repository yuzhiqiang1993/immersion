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
- 🔄 **动态切换**: 支持运行时启用/禁用沉浸式模式

## 📦 安装

在模块的 `build.gradle.kts` 文件中添加依赖：

> 📢 **最新版本**：请访问 [Maven Central](https://central.sonatype.com/artifact/com.xeonyu/immersionbar) 获取最新版本号。

```kotlin
dependencies {
    implementation("com.xeonyu:immersionbar:x.x.x")
}
```

将 `x.x.x` 替换为最新版本号。

## 🚀 快速开始

### 基础用法

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 启用沉浸式模式（默认：导航栏自动 padding，状态栏透明）
        ImmersionBar.enable(this)
    }
}
```

## 📖 API 文档

### 核心方法 - ImmersionBar.enable()

启用沉浸式模式的主要方法，一行代码即可完成所有沉浸式设置。

```kotlin
ImmersionBar.enable(
    activity: Activity,                    // 必需 - 目标 Activity 实例
    paddingStatusBar: Boolean = false,      // 可选 - 是否添加状态栏 padding
    paddingNavigationBar: Boolean = true,    // 可选 - 是否添加导航栏 padding
    darkStatusBarText: Boolean = true,     // 可选 - 状态栏文字颜色模式
    showStatusBar: Boolean = true,         // 可选 - 是否显示状态栏
    showNavigationBar: Boolean = true      // 可选 - 是否显示导航栏
)
```

#### 参数详细说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| **activity** | `Activity` | 必需 | 需要设置沉浸式的 Activity 实例。通常传入 `this` |
| **paddingStatusBar** | `Boolean` | `false` | 是否为状态栏添加顶部 padding。<br>• **false**：内容蔓延到状态栏（默认）<br>• **true**：添加 padding 避开状态栏 |
| **paddingNavigationBar** | `Boolean` | `true` | 是否为导航栏添加底部 padding。<br>• **true**：添加 padding 避开导航栏（推荐，默认）<br>• **false**：内容蔓延到导航栏 |
| **darkStatusBarText** | `Boolean` | `true` | 状态栏文字颜色模式。<br>• **true**：深色文字（适合白色/浅色背景）<br>• **false**：浅色文字（适合黑色/深色背景）<br>• **注意**：仅在 Android 6.0+ 生效 |
| **showStatusBar** | `Boolean` | `true` | 是否显示状态栏。<br>• **true**：显示状态栏<br>• **false**：隐藏状态栏<br>• **隐藏时**：可通过下滑手势临时唤出 |
| **showNavigationBar** | `Boolean` | `true` | 是否显示导航栏。<br>• **true**：显示导航栏<br>• **false**：隐藏导航栏<br>• **隐藏时**：可通过上滑手势临时唤出 |

### 禁用沉浸式 - ImmersionBar.disable()

禁用沉浸式模式，恢复到默认状态（系统栏白色，内容不延伸）。

```kotlin
ImmersionBar.disable(activity: Activity)
```

### 更新系统栏状态 - ImmersionBar.updateSystemBars()

动态更新系统栏的显示/隐藏状态和文字颜色，无论沉浸式是否启用都有效。

```kotlin
ImmersionBar.updateSystemBars(
    activity: Activity,                    // 目标 Activity
    showStatusBar: Boolean = true,         // 是否显示状态栏
    showNavigationBar: Boolean = true,     // 是否显示导航栏
    darkStatusBarText: Boolean = true      // 状态栏文字是否为深色
)
```

### 辅助方法

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

### Dialog 沉浸式

#### 全屏 Dialog

```kotlin
// 启用全屏 Dialog 沉浸式（内容延伸到系统栏下方）
ImmersionBar.enableFullScreenDialog(
    dialog: Dialog,                       // 目标 Dialog
    darkStatusBarText: Boolean = true,    // 状态栏文字是否为深色
    paddingStatusBar: Boolean = false,    // 是否添加状态栏 padding
    paddingNavigationBar: Boolean = false // 是否添加导航栏 padding
)
```

#### 底部弹窗

```kotlin
// 启用底部弹窗沉浸式（导航栏透明）
ImmersionBar.enableBottomSheetDialog(
    dialog: Dialog,                       // 目标 Dialog
    paddingNavigationBar: Boolean = false // 是否添加导航栏 padding
)
```

#### Dialog 状态栏文字

```kotlin
// 动态设置 Dialog 状态栏文字颜色
ImmersionBar.setDialogStatusBarTextDark(
    dialog: Dialog,        // 目标 Dialog
    isDark: Boolean = true // true=深色文字，false=浅色文字
)
```

## 🎨 使用场景

### 1. 推荐配置 (Demo默认效果)

这种配置方式既保留了沉浸感（内容延伸到底部），又避免了顶部内容被状态栏遮挡。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 现代化沉浸式：
        // 1. 状态栏添加 padding，避免内容被遮挡
        // 2. 导航栏不加 padding，内容延伸到底部（配合透明导航栏）
        ImmersionBar.enable(
            activity = this,
            paddingStatusBar = true,      // 避开状态栏
            paddingNavigationBar = false, // 延伸到导航栏
            darkStatusBarText = true
        )
    }
}
```

### 2. 完全全屏 (内容延伸到所有区域)

```kotlin
// 内容延伸到所有系统栏下方
ImmersionBar.enable(
    activity = this,
    paddingStatusBar = false,     // 延伸到状态栏
    paddingNavigationBar = false, // 延伸到导航栏
    darkStatusBarText = true
)
```

### 3. 传统沉浸式 (保守模式)

```kotlin
// 类似传统 FitsSystemWindows=true 的效果
ImmersionBar.enable(
    activity = this,
    paddingStatusBar = true,      // 避开状态栏
    paddingNavigationBar = true,  // 避开导航栏
    darkStatusBarText = true
)
```

### 4. 隐藏导航栏

```kotlin
// 隐藏导航栏，保持状态栏可见
ImmersionBar.enable(
    activity = this,
    showStatusBar = true,
    showNavigationBar = false, // 隐藏导航栏
    paddingNavigationBar = false // 既然隐藏了，通常不需要 padding
)
```

### 5. 深色背景配合浅色文字

```kotlin
// 深色背景使用浅色状态栏文字
ImmersionBar.enable(
    activity = this,
    paddingStatusBar = true,
    darkStatusBarText = false,  // 浅色文字适合深色背景
)
```

### 6. 动态切换沉浸式模式

```kotlin
class MainActivity : AppCompatActivity() {

    private var isImmersionEnabled = true

    private fun toggleImmersion() {
        if (isImmersionEnabled) {
            // 禁用沉浸式
            ImmersionBar.disable(this)
            // 禁用后仍可控制系统栏显示/隐藏
            ImmersionBar.updateSystemBars(
                activity = this,
                darkStatusBarText = true,
                showStatusBar = true,
                showNavigationBar = true
            )
        } else {
            // 启用沉浸式
            ImmersionBar.enable(
                activity = this,
                paddingStatusBar = true,
                paddingNavigationBar = false
            )
        }
        isImmersionEnabled = !isImmersionEnabled
    }
}
```

### 7. 动态切换主题

```kotlin
class ThemedActivity : AppCompatActivity() {
    private fun toggleTheme() {
        val isLightTheme = currentTheme == Theme.LIGHT

        // 更新背景颜色
        binding.rootView.setBackgroundColor(
            if (isLightTheme) Color.WHITE else Color.BLACK
        )

        // 同步更新状态栏文字颜色
        ImmersionBar.setStatusBarTextDark(this, isLightTheme)
    }
}
```

### 8. 全屏视频/图片查看器

```kotlin
// 完全沉浸式：隐藏所有系统栏 & 内容延伸
ImmersionBar.enable(
    activity = this,
    showStatusBar = false,     // 隐藏状态栏
    showNavigationBar = false, // 隐藏导航栏
    paddingStatusBar = false,
    paddingNavigationBar = false
)
```

### 9. 全屏 Dialog

```kotlin
// 创建全屏 Dialog
val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
dialog.setContentView(R.layout.dialog_fullscreen)

// 一行代码启用沉浸式
ImmersionBar.enableFullScreenDialog(dialog)

dialog.show()
```

### 10. 底部弹窗

```kotlin
// 创建底部弹窗
val bottomSheet = BottomSheetDialog(this)
bottomSheet.setContentView(R.layout.dialog_bottom_sheet)

// 一行代码启用沉浸式（导航栏透明）
ImmersionBar.enableBottomSheetDialog(bottomSheet)

bottomSheet.show()
```

## 💡 最佳实践

### 推荐配置组合

```kotlin
// 1. 现代化 Edge-to-Edge（推荐）
ImmersionBar.enable(
    activity = this,
    paddingStatusBar = true,      // 顶部避让
    paddingNavigationBar = false  // 底部延伸
)

// 2. 全屏阅读/视频
ImmersionBar.enable(
    activity = this,
    showStatusBar = false,
    showNavigationBar = false,
    paddingStatusBar = false,
    paddingNavigationBar = false
)
```

### 注意事项

1. **paddingNavigationBar 参数**：
   - 默认为 `true`（添加 padding）：内容不延伸到导航栏区域（推荐用于常规页面）
   - 设置为 `false`（不添加 padding）：内容延伸到导航栏区域（推荐用于沉浸式主页/详情页）

2. **版本兼容**：`darkStatusBarText` 仅在 Android 6.0+ 生效

3. **手势导航**：隐藏的系统栏可通过手势随时唤出

4. **性能优化**：避免在 `onCreate` 外频繁调用 `enable()` 方法

5. **禁用沉浸式**：`disable()` 后系统栏会恢复为白色，内容不再延伸到系统栏下方

6. **Padding 参数依赖关系**：
   - `paddingStatusBar` 和 `paddingNavigationBar` 参数仅在沉浸式模式开启时有效
   - 关闭沉浸式模式时，padding 设置会自动失效
   - 在实现 UI 开关时，建议将 padding 开关的启用状态绑定到沉浸式开关状态
   - 示例代码：
     ```kotlin
     // 沉浸式关闭时，禁用并关闭 padding 开关
     if (!isChecked) {
         binding.switchPaddingStatusBar.isChecked = false
         binding.switchPaddingNavBar.isChecked = false
     }
     // 根据 immersive 状态控制 padding 开关是否可用
     binding.switchPaddingStatusBar.isEnabled = isChecked
     binding.switchPaddingNavBar.isEnabled = isChecked
     ```



## 📄 许可证

本项目采用 Apache License 2.0 许可证。详情请查看 [LICENSE](LICENSE) 文件。

---

如果这个项目对您有帮助，请给个 ⭐️ Star 支持一下！

---

**English Documentation**: [README_EN.md](README_EN.md)
