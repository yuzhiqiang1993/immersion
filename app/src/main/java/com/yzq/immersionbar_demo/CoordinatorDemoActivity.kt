package com.yzq.immersionbar_demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yzq.immersion.actionBarHeight
import com.yzq.immersion.applyNavigationBarMargin
import com.yzq.immersion.applyNavigationBarPadding
import com.yzq.immersion.darkenColor
import com.yzq.immersion.isLightColor
import com.yzq.immersion.navigationBarHeight
import com.yzq.immersion.setStatusBarDarkFont
import com.yzq.immersion.setupImmersion
import com.yzq.immersion.statusBarHeight
import com.yzq.immersionbar_demo.databinding.ActivityCoordinatorDemoBinding
import com.yzq.immersionbar_demo.databinding.ItemColorPickerBinding
import kotlin.math.abs

class CoordinatorDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinatorDemoBinding
    private val colorAdapter = ColorAdapter()
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
        // 库 API：初始化沉浸式（isStatusBarDarkFont = null 时自动推断）
        setupImmersion()

        // 库 API：底部内容区域避让导航栏
        binding.contentContainer.applyNavigationBarPadding()

        // Toolbar 适配状态栏高度
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            val statusBarTop = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = actionBarHeight + statusBarTop
            }
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft, statusBarTop,
                binding.toolbar.paddingRight, binding.toolbar.paddingBottom
            )
            binding.collapsingToolbar.minimumHeight = actionBarHeight + statusBarTop
            insets
        }

        // 监听折叠进度，动态调整状态栏文字颜色
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            currentScrollProgress = if (totalScrollRange != 0) {
                abs(verticalOffset).toFloat() / totalScrollRange
            } else 0f
            updateColorsBasedOnState(currentBackgroundColor)
        }
    }

    /**
     * 根据滑动折叠进度和背景色，统一刷新 Header/Toolbar/状态栏外观。
     */
    private fun updateColorsBasedOnState(baseColor: Int) {
        val isCollapsed = currentScrollProgress > 0.7f

        if (isCollapsed) {
            // 完全折叠：Toolbar 显示深色背景和白色标题
            val collapsedColor = baseColor.darkenColor()
            binding.collapsingToolbar.setBackgroundColor(collapsedColor)
            binding.toolbar.setBackgroundColor(collapsedColor)
            binding.toolbar.title = "Coordinator 演示"
            binding.toolbar.setTitleTextColor(Color.WHITE)
            binding.toolbar.setNavigationIconTint(Color.WHITE)
            binding.collapsingToolbar.title = ""

            // 库 API：深色背景 → 浅色状态栏文字
            setStatusBarDarkFont(false)
        } else {
            // 展开：Toolbar 透明，根据背景色自动适配
            binding.collapsingToolbar.setBackgroundColor(baseColor)
            binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            binding.toolbar.title = ""

            val isLightBg = baseColor.isLightColor()
            val contentColor = if (isLightBg) Color.rgb(33, 33, 33) else Color.WHITE
            binding.toolbar.setNavigationIconTint(contentColor)
            binding.toolbar.setTitleTextColor(contentColor)
            updateHeaderContentColor(isLightBg)

            // 库 API：根据背景亮度决定状态栏文字深浅
            setStatusBarDarkFont(isLightBg)
        }
    }

    private fun setupRecyclerView() {
        binding.colorRecycler.apply {
            layoutManager = GridLayoutManager(this@CoordinatorDemoActivity, 6)
            adapter = colorAdapter
        }
        colorAdapter.onColorSelected = { color -> applyBackgroundColor(color) }
    }

    private fun setupClickListeners() {
        binding.fab.setOnClickListener {
            applyBackgroundColor(predefinedColors.random().toColorInt())
        }

        // 库 API：FAB 避让导航栏
        binding.fab.applyNavigationBarMargin()

        binding.switchShowNavigation.setOnCheckedChangeListener { _, isChecked ->
            // 库 API：切换导航栏可见性
            setupImmersion(
                showStatusBar = binding.switchShowStatus.isChecked,
                showNavigationBar = isChecked
            )
            updateSystemInfo()
        }

        binding.switchShowStatus.setOnCheckedChangeListener { _, isChecked ->
            // 库 API：切换状态栏可见性
            setupImmersion(
                showStatusBar = isChecked,
                showNavigationBar = binding.switchShowNavigation.isChecked
            )
            updateSystemInfo()
        }
    }

    private fun applyBackgroundColor(color: Int) {
        currentBackgroundColor = color
        val currentColor =
            (binding.coordinator.background as? android.graphics.drawable.ColorDrawable)?.color
                ?: Color.WHITE

        ValueAnimator.ofArgb(currentColor, color).apply {
            duration = 300
            addUpdateListener {
                val animatedColor = it.animatedValue as Int
                binding.coordinator.setBackgroundColor(animatedColor)
                binding.headerContent.setBackgroundColor(animatedColor)
                updateColorsBasedOnState(animatedColor)
            }
        }.start()

        // 同步 Switch 状态与文字颜色
        val isLightBg = color.isLightColor()
        val textColor = if (isLightBg) Color.parseColor("#212121") else Color.WHITE
        binding.switchShowNavigation.setTextColor(textColor)
        binding.switchShowStatus.setTextColor(textColor)
        binding.tvSelectColorLabel.setTextColor(textColor)
        binding.tvInfo.setBackgroundColor(
            if (isLightBg) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        )
        binding.tvInfo.setTextColor(
            if (isLightBg) Color.parseColor("#616161") else Color.parseColor("#BDBDBD")
        )
        updateSystemInfo()
    }

    private fun updateHeaderContentColor(isLightBg: Boolean) {
        val textColor = if (isLightBg) Color.rgb(33, 33, 33) else Color.WHITE
        val subtitleColor = if (isLightBg) Color.rgb(97, 97, 97) else Color.rgb(245, 245, 245)
        // 通过 binding 直接查找 header_content 下的 child（LinearLayout 内的两个 TextView）
        val headerContent = binding.headerContent
        if (headerContent.childCount >= 2) {
            (headerContent.getChildAt(0) as? android.widget.TextView)?.setTextColor(textColor)
            (headerContent.getChildAt(1) as? android.widget.TextView)?.setTextColor(subtitleColor)
        }
    }

    private fun updateSystemInfo() {
        binding.root.post {
            binding.tvInfo.text = buildString {
                append("系统信息:\n")
                append("• Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                append("• 状态栏高度: ${statusBarHeight}px\n")
                append("• 导航栏高度: ${navigationBarHeight}px")
            }
        }
    }

    // ======================== Adapter ========================

    inner class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
        var onColorSelected: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            return ColorViewHolder(
                ItemColorPickerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.bind(predefinedColors[position].toColorInt())
        }

        override fun getItemCount(): Int = predefinedColors.size

        inner class ColorViewHolder(private val binding: ItemColorPickerBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(color: Int) {
                binding.colorView.setBackgroundColor(color)
                binding.root.setOnClickListener { onColorSelected?.invoke(color) }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish(); return true
    }
}
