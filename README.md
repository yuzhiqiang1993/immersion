# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersion.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

一个现代化的 Android 沉浸式状态栏库，基于 Android 官方推荐的 Edge-to-Edge 模式设计，提供简洁易用的 API。

## 特性

- **现代化实现**: 基于 Android 15+ 官方推荐的 Edge-to-Edge 模式
- **全面兼容**: 支持 API 23+ (Android 6.0+)
- **灵活配置**: 支持状态栏、导航栏精细化独立避让（Padding & Margin两套方案）
- **智能设色**: 根据背景色（基于 CIEXYZ 亮度模型）自动匹配状态栏深浅文字
- **开发利器**: 丰富的 Kotlin 扩展（高度获取、颜色亮度判断、变暗等）
- **动态更新**: 支持实时刷新显示状态，安全多次调用不起冲突

## 安装

在模块的 `build.gradle.kts` 文件中添加依赖：

> **最新版本**：请访问 [Maven Central](https://central.sonatype.com/artifact/com.xeonyu/immersion) 获取最新版本号。

```kotlin
dependencies {
    implementation("com.xeonyu:immersion:2.0.0") // 请替换为实际最新版
}
```

将 `x.x.x` 替换为最新版本号。

## 快速开始

### 基础用法

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 启用沉浸式模式，只需调用此简单扩展方法
        setupImmersion()
    }
}
```

## API 文档

### 核心方法 - Activity.setupImmersion()

为 Activity 的 Window 开启 Edge-to-Edge，并一次性完成包括显示策略、刘海屏适配、底座深浅色判断等沉浸式设置。

```kotlin
// 默认为：系统栏全开，文字颜色根据背景色智能推断
setupImmersion(
    showStatusBar: Boolean = true,         // 可选 - 是否显示状态栏
    showNavigationBar: Boolean = true,     // 可选 - 是否显示导航栏
    isStatusBarDark: Boolean? = null,      // 可选 - 状态栏文字图标深浅色。null表示根据背景自动计算
    isNavigationBarDark: Boolean? = null   // 可选 - 导航栏文字图标深浅色。null表示跟随自动计算
)
```

#### 参数详细说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| **showStatusBar** | `Boolean` | `true` | 是否可见状态栏。<br>• **false**：隐藏状态栏（可通过下滑手势临时唤出） |
| **showNavigationBar** | `Boolean` | `true` | 是否可见导航栏。<br>• **false**：隐藏导航栏（可通过上滑手势临时唤出） |
| **isStatusBarDark** | `Boolean?` | `null` | 强制干预状态栏文字模式：<br>• **null**：基于本库的 CIEXYZ 亮度计算由背景色动态决定<br>• **true**：深黑文字<br>• **false**：纯白文字 |
| **isNavigationBarDark** | `Boolean?` | `null` | 配置导航栏按钮颜色模式逻辑，同上。 |

#### 高级策略（可选）

默认调用已经足够覆盖大多数场景（默认策略为 `Auto`）。

当你需要更明确地控制视觉效果与兼容策略时，可使用高级重载：

```kotlin
setupImmersion(
    ImmersionOptions(
        strategy = ImmersionStrategy.Transparent
    )
)
```

策略说明：

- `ImmersionStrategy.Auto`（默认）：优先保障兼容性。在 API 23~25 上若导航栏需要深色图标，会自动回退半透明深色底以保证白色图标可读。
- `ImmersionStrategy.Transparent`：保持导航栏全透明，追求更强沉浸感。

### 动态刷新系统栏状态

在新框架中，如果你需要动态显示/隐藏，或者想要修改深浅色的配置，**只需用新的参数再次调用 `setupImmersion` 即可**。本方法极其轻量且安全，可反复覆盖调用而无性能副作用。

例如在运行时关闭状态栏并强设由于背景变化需要的黑色文字：
```kotlin
setupImmersion(
    showStatusBar = false,
    isStatusBarDark = true
)
```

#### 系统信息获取（Kotlin 扩展属性）

框架提供了一组极为简便的扩展属性，能够直接从 Activity、Fragment 甚至是任意 View 身上获取。

```kotlin
// 获取状态栏高度（像素）
val statusBarH = this.statusBarHeight

// 获取导航栏高度（像素）
val navBarH = this.navigationBarHeight

// 一键获取 ActionBar / Toolbar 原生高度
val actionBarH = this.actionBarHeight

// 检查设备是否包含导航栏
val hasNavBar = this.hasNavigationBar

// 检查是否为刘海屏设备
val isNotch = this.hasNotch
```

#### 颜色与亮度判断（设计辅助）

提供了一组基于 AndroidX 最新标准的颜色与亮度扩展函数，能帮助应用精确掌控 Material Design 对比度表现。

```kotlin
// 判断颜色在视觉感知上是否属于“浅色/亮色”
// 基于 Android官方的 ColorUtils.calculateLuminance (CIEXYZ 颜色空间映射)
val isLight = Color.WHITE.isLightColor() // 返回 true

// 快速得到一个颜色的加深/变暗版本，方便做 Toolbar 折叠差值
// factor: 暗化程度 0f(黑) ~ 1f(原色)，默认 0.7f
val darkColor = baseColor.darkenColor() 
// 或自定义比例
val darkerColor = baseColor.darkenColor(0.5f) 
```

### 独立 View 布局避让 (Insets)

框架提供了极其轻量化的局部扩展，专为那些处于沉浸式之下、但不希望被系统图层遮盖的 View 而设计。

#### 1. Padding 避让模式
适用于想要扩大 View 的内边距来让出状态栏空间的场景。
```kotlin
// 仅增加状态栏 Padding，参数代表是否启用增加
view.applyStatusBarPadding(add = true)

// 仅增加导航栏 Padding
view.applyNavigationBarPadding(add = true)

// 同时控制两个维度的 Padding
view.applySystemBarsPadding(
    addStatusBar = true,
    addNavigationBar = false
)
```

#### 2. Margin 避让模式
适用于需要让整个 View 直接向下平移，不干涉其内部展示的场景。
```kotlin
// 增加状态栏 Margin
view.applyStatusBarMargin(add = true)

// 增加导航栏 Margin
view.applyNavigationBarMargin(add = true)

// 同时控制两个维度的 Margin
view.applySystemBarsMargin(addStatusBar = true, addNavigationBar = true)
```

#### 核心特性

1.  **绝对安全，防叠加**：框架只会**记录一次** View 在 XML 中写好的初始 Padding 或 Margin。后续无论你怎么疯狂反复切换开关、不管 View 的大小被系统多少次重新测量，它的高度叠加只会在“基准值”上发生，绝不会无限长高，也绝不会出现关闭后吃掉原始 XML 高度的问题。
2.  **极简低侵入**：去除了沉长繁琐的业务判断，底层仅使用局部闭包和轻便的 系统专属 View Tag 实现。
3.  **支持动态增减**：传入 `add = false` 即可瞬间抽离因系统栏附加产生的高度，恢复其百分百本来的样子。


### Dialog 沉浸式

同样仅需调用我们提供的同名扩展方法即可。

#### 全屏 / 常规 Dialog

```kotlin
// 启用 Dialog 沉浸式扩展（内容延伸到系统栏下方）
dialog.setupImmersion(
    isStatusBarDark: Boolean? = null,
    isNavigationBarDark: Boolean? = null,
    strategy: ImmersionStrategy = ImmersionStrategy.Auto
)
```

#### 底部弹窗 BottomSheet

专门针对底部弹窗设计了边缘延伸处理：

```kotlin
// 启用底部弹窗沉浸式（导航栏透明并拉伸）
bottomSheetDialog.setupBottomSheetImmersion(
    isNavigationBarDark: Boolean? = null,
    strategy: ImmersionStrategy = ImmersionStrategy.Auto
)
```

## 组合使用场景指南

新架构将 **Window层沉浸** 与 **View层避让** 彻底解耦，你能极度灵活地拼装它们以达到各种需求：

### 1. 推荐配置 (常规沉浸式页面)

绝大多数带标题栏的普通界面，在内容透出到底部导航栏的同时，也要保证顶部不和状态栏重叠。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. (Window) 开启沉浸式，透明系统栏
        setupImmersion()
        
        // 2. (View) 为最外层根布局增加顶部的状态栏 Padding，防止内容被遮挡
        // 而底部由于没写 addNavigationBar=true，列表能够优雅地垫在透明导航栏下方滑动
        findViewById<View>(android.R.id.content).applySystemBarsPadding(
            addStatusBar = true,      // 避开状态栏
            addNavigationBar = false  // 延伸铺满导航栏
        )
    }
}
```

### 2. 传统沉浸式 (保守防遮挡)

若你不想任何内容渗透到系统栏下方，只想保持系统栏透明改变其背景色而已，相当于传统的 FitsSystemWindows=true 效果。

```kotlin
setupImmersion()

// 根布局同时避开上下两端
view.applySystemBarsPadding(
    addStatusBar = true,
    addNavigationBar = true
)
```

### 3. 全屏阅读 / 视频 / 图片查看器

这通常不需要系统栏。

```kotlin
// 完全隐藏所有系统栏与导航条
setupImmersion(
    showStatusBar = false,
    showNavigationBar = false
)
// 此时根本无需申请任何 Padding 避让
```

### 4. 动态更改主题与颜色

```kotlin
class ThemedActivity : AppCompatActivity() {
    private fun toggleTheme() {
        val isLightTheme = currentTheme == Theme.LIGHT

        // 更新背景颜色
        binding.rootView.setBackgroundColor(
            if (isLightTheme) Color.WHITE else Color.BLACK
        )

        // 背景变化了，再次调用一行代码
        // 本库会依靠 backgroundColor 自动重算并把状态栏文字翻转深浅色
        setupImmersion()
    }
}
```

### 9. 全屏 Dialog

```kotlin
// 创建全屏 Dialog
val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
dialog.setContentView(R.layout.dialog_fullscreen)

// 一行代码启用沉浸式
dialog.setupImmersion()

dialog.show()
```

### 10. 底部弹窗

```kotlin
// 创建底部弹窗
val bottomSheet = BottomSheetDialog(this)
bottomSheet.setContentView(R.layout.dialog_bottom_sheet)

// 一行代码启用沉浸式（导航栏透明）
bottomSheet.setupBottomSheetImmersion()

bottomSheet.show()
```

## 最佳实践与注意事项

### 架构解耦思想

**Window 配置** 与 **View 避让** 分离。

1. **Window 层 (`setupImmersion`)**
   只负责：是否全屏延伸、是否显示/隐藏系统界面、并推断出顶部状态栏和底部导航小横条应该是黑色还是白色
2. **View 层 (`apply***Padding/Margin`)**
   只负责：把处于全面屏下的单个或一组 View（比如一个 Toolbar ，一个底部的悬浮按钮）向下或向上避开系统控件。

### 注意事项

1. **版本兼容**：最低支持 Android 6.0（API 23）。状态栏深色图标能力从 Android 6.0 起支持，导航栏深色图标能力从 Android 8.0（API 26）起支持；在 API 23~25 下，默认 `Auto` 策略会自动进行可读性兜底。
2. **手势导航**：隐藏的系统栏可通过手势随时唤出（设计为 `SHOW_TRANSIENT_BARS_BY_SWIPE`）。
3. **安全重复调用**：无论是改变沉浸式策略(`setupImmersion`)，还是控制特定 View 避让的开关状态(`applySystemBarsPadding`)，在新框架底层均没有副作用，**你可以安全地在任何生命周期或者点击事件里随心所欲地重复调用它们**。
4. **Padding 清理**：当 `add = false` 传入视图避让扩展方法时，View 将安全剥离因为 Insets 叠加产生的值，恢复到读取到的原始 XML 状态。



## 许可证

本项目采用 Apache License 2.0 许可证。详情请查看 [LICENSE](LICENSE) 文件。

---

如果这个项目对您有帮助，请给个 Star 支持一下！
