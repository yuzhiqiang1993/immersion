package com.yzq.immersionbar_demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.ActivityCoordinatorDemoBinding
import com.yzq.immersionbar_demo.databinding.ItemColorPickerBinding

/**
 * @description: 折叠栏的示例
 * @author : yuzhiqiang
 */

class CoordinatorDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinatorDemoBinding
    private val colorAdapter = ColorAdapter()
    private var isDarkStatusBarText = true
    private var currentBackgroundColor = Color.WHITE
    private var currentScrollProgress = 0f

    private val predefinedColors = listOf(
        "#FFFFFF", "#F5F5F5", "#E3F2FD", "#E8F5E8", "#FFF3E0", "#FCE4EC",
        "#1E1E1E", "#2C2C2C", "#1565C0", "#2E7D32", "#E65100", "#C2185B"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinatorDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupImmersionBar()
        setupRecyclerView()
        setupClickListeners()
        updateSystemInfo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupImmersionBar() {
        // 为整个 Coordinator 添加沉浸式效果，不指定 rootView
        ImmersionBar.enable(
            activity = this,
            rootView = null,
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )

        // 手动处理 WindowInsets，让 Toolbar 成为沉浸式 View
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 1. 动态调整 Toolbar 的高度以包含状态栏
            val typedValue = android.util.TypedValue()
            var actionBarHeight = 0
            if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                actionBarHeight = android.util.TypedValue.complexToDimensionPixelSize(
                    typedValue.data,
                    resources.displayMetrics
                )
            }

            // 设置 Toolbar 的高度为 actionBarSize + statusBarHeight
            val toolbarLp = binding.toolbar.layoutParams
            toolbarLp.height = actionBarHeight + systemBars.top
            binding.toolbar.layoutParams = toolbarLp

            // 2. 给 Toolbar 添加顶部 Padding，将内容（标题、图标）推下来
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft,
                systemBars.top,
                binding.toolbar.paddingRight,
                binding.toolbar.paddingBottom
            )

            // 3. 设置 CollapsingToolbarLayout 的最小高度
            binding.collapsingToolbar.minimumHeight = actionBarHeight + systemBars.top

            // 不消费 Insets，让其他 View 也能接收到
            insets
        }

        // 监听 AppBar 的滚动变化来动态调整状态栏文字颜色和工具栏颜色
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            // 计算折叠进度 (0 = 完全展开, 1 = 完全折叠)
            val totalScrollRange = appBarLayout.totalScrollRange
            currentScrollProgress = if (totalScrollRange != 0) {
                Math.abs(verticalOffset).toFloat() / totalScrollRange
            } else {
                0f
            }

            updateColorsBasedOnState(currentBackgroundColor)
        }

        // 初始化设置 AppBar 展开时的背景色和标题颜色
        updateAppBarContentColor(Color.WHITE)
        updateHeaderContentColor(Color.WHITE)
    }

    private fun updateColorsBasedOnState(baseColor: Int) {
        if (currentScrollProgress > 0.7f) {
            // 完全折叠状态：Toolbar 显示不透明背景和标题
            val collapsedColor = darkenColor(baseColor)
            binding.collapsingToolbar.setBackgroundColor(collapsedColor)
            binding.toolbar.setBackgroundColor(collapsedColor)

            // 显示 Toolbar 标题，强制使用白色
            binding.toolbar.title = "Coordinator 演示"
            binding.toolbar.setTitleTextColor(Color.WHITE)
            binding.toolbar.setNavigationIconTint(Color.WHITE)

            // 确保 CollapsingToolbar 不显示标题（双重保险）
            binding.collapsingToolbar.title = ""

            ImmersionBar.setStatusBarTextDark(this, false)
        } else {
            // 展开或部分折叠状态：Toolbar 透明，隐藏标题
            binding.collapsingToolbar.setBackgroundColor(baseColor)
            binding.toolbar.setBackgroundColor(Color.TRANSPARENT)

            // 隐藏 Toolbar 标题
            binding.toolbar.title = ""

            // 更新 Header Content 的颜色
            updateHeaderContentColor(baseColor)

            // 根据当前背景色决定状态栏文字颜色
            val shouldUseDarkText = isLightColor(baseColor)
            ImmersionBar.setStatusBarTextDark(this, shouldUseDarkText)

            // Toolbar 图标颜色也要根据背景调整
            val iconColor =
                if (shouldUseDarkText) Color.rgb(33, 33, 33) else Color.rgb(255, 255, 255)
            binding.toolbar.setNavigationIconTint(iconColor)
            // 虽然标题隐藏了，但也更新一下颜色以防万一
            binding.toolbar.setTitleTextColor(iconColor)
        }
    }


    private fun setupRecyclerView() {
        binding.colorRecycler.apply {
            layoutManager = GridLayoutManager(this@CoordinatorDemoActivity, 6)
            adapter = colorAdapter
        }

        colorAdapter.onColorSelected = { color ->
            applyBackgroundColor(color)
        }
    }

    private fun setupClickListeners() {
        // 悬浮按钮：随机切换背景颜色
        binding.fab.setOnClickListener {
            val randomColorString = predefinedColors.random()
            val color = randomColorString.toColorInt()
            applyBackgroundColor(color)
        }

        // 状态栏文字颜色开关
        binding.switchDarkStatus.setOnCheckedChangeListener { _, isChecked ->
            isDarkStatusBarText = isChecked
            ImmersionBar.setStatusBarTextDark(this, isDarkStatusBarText)
            updateSystemInfo()
        }

        // 状态栏显示开关
        binding.switchShowStatus.setOnCheckedChangeListener { _, isChecked ->
            ImmersionBar.enable(
                activity = this,
                rootView = null,
                darkStatusBarText = isDarkStatusBarText,
                showStatusBar = isChecked,
                showNavigationBar = true
            )
            updateSystemInfo()
        }
    }

    private fun applyBackgroundColor(color: Int) {
        // 更新全局背景色变量
        currentBackgroundColor = color

        // 平滑过渡背景颜色
        val currentColor =
            (binding.coordinator.background as? android.graphics.drawable.ColorDrawable)?.color
                ?: Color.WHITE

        val colorAnim = ValueAnimator.ofArgb(currentColor, color).apply {
            duration = 300
            addUpdateListener { animator ->
                val animatedColor = animator.animatedValue as Int
                // 同时修改整个 Coordinator 的背景
                binding.coordinator.setBackgroundColor(animatedColor)

                // 更新 AppBar 可折叠内容区域的背景
                updateAppBarContentColor(animatedColor)

                // 根据当前状态（折叠/展开）更新 Toolbar 和 CollapsingToolbar 的颜色
                updateColorsBasedOnState(animatedColor)
            }
        }
        colorAnim.start()

        // 智能调整状态栏文字颜色
        val shouldUseDarkText = isLightColor(color)
        if (shouldUseDarkText != isDarkStatusBarText) {
            isDarkStatusBarText = shouldUseDarkText
            binding.switchDarkStatus.isChecked = shouldUseDarkText
            ImmersionBar.setStatusBarTextDark(this, shouldUseDarkText)
        }

        // 更新其他 UI 元素的颜色
        val textColor = if (shouldUseDarkText) Color.parseColor("#212121") else Color.WHITE
        val infoBgColor =
            if (shouldUseDarkText) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        val infoTextColor =
            if (shouldUseDarkText) Color.parseColor("#616161") else Color.parseColor("#BDBDBD")

        binding.switchDarkStatus.setTextColor(textColor)
        binding.switchShowStatus.setTextColor(textColor)
        binding.tvSelectColorLabel.setTextColor(textColor)

        binding.tvInfo.setBackgroundColor(infoBgColor)
        binding.tvInfo.setTextColor(infoTextColor)

        updateSystemInfo()
    }

    private fun updateHeaderContentColor(backgroundColor: Int) {
        val isLightBg = isLightColor(backgroundColor)

        // 手动更新 AppBar 内容区域的文字颜色
        val linearLayout = binding.collapsingToolbar.getChildAt(0) as? LinearLayout
        if (linearLayout != null && linearLayout.childCount >= 2) {
            val titleTextView = linearLayout.getChildAt(0) as? TextView
            val subtitleTextView = linearLayout.getChildAt(1) as? TextView

            val textColor = if (isLightBg) Color.rgb(33, 33, 33) else Color.rgb(255, 255, 255)
            val subtitleColor = if (isLightBg) Color.rgb(97, 97, 97) else Color.rgb(245, 245, 245)

            titleTextView?.setTextColor(textColor)
            subtitleTextView?.setTextColor(subtitleColor)
        }
    }

    private fun updateAppBarContentColor(color: Int) {
        // 找到 AppBar 中的 LinearLayout 内容区域并更新背景颜色
        val linearLayout = binding.collapsingToolbar.getChildAt(0) as? LinearLayout
        linearLayout?.setBackgroundColor(color)
    }

    private fun isLightColor(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000
        return brightness > 128
    }

    private fun darkenColor(color: Int): Int {
        val factor = 0.7f
        val red = (Color.red(color) * factor).toInt()
        val green = (Color.green(color) * factor).toInt()
        val blue = (Color.blue(color) * factor).toInt()
        return Color.rgb(red, green, blue)
    }

    private fun updateSystemInfo() {
        binding.root.post {
            val insets = ViewCompat.getRootWindowInsets(binding.root)
            val sb = StringBuilder()
            sb.append("系统信息:\n")
            sb.append("• Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")

            if (insets != null) {
                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                sb.append("• 状态栏高度: ${statusBar.top}px\n")
                sb.append("• 导航栏高度: ${navBar.bottom}px\n")
            }

            sb.append("• AppBar 状态: 可折叠\n")
            sb.append("• 状态栏文字: ${if (isDarkStatusBarText) "深色" else "浅色"}")

            binding.tvInfo.text = sb.toString()
        }
    }

    inner class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

        var onColorSelected: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val binding = ItemColorPickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ColorViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            val colorString = predefinedColors[position]
            val color = colorString.toColorInt()

            holder.bind(color)
        }

        override fun getItemCount(): Int = predefinedColors.size

        inner class ColorViewHolder(private val binding: ItemColorPickerBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(color: Int) {
                binding.colorView.setBackgroundColor(color)

                binding.root.setOnClickListener {
                    onColorSelected?.invoke(color)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}