package com.yzq.immersionbar_demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yzq.immersion.applyNavigationBarMargin
import com.yzq.immersion.applyNavigationBarPadding
import com.yzq.immersion.setupImmersion
import com.yzq.immersionbar_demo.databinding.ActivityCoordinatorDemoBinding
import com.yzq.immersionbar_demo.databinding.ItemColorPickerBinding

class CoordinatorDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoordinatorDemoBinding
    private val colorAdapter = ColorAdapter()
    private var isDarkStatusBarText = true
    private var currentBackgroundColor = Color.WHITE
    private var currentScrollProgress = 0f

    private val predefinedColors = listOf(
        "#FFFFFF",
        "#F5F5F5",
        "#E3F2FD",
        "#E8F5E8",
        "#FFF3E0",
        "#FCE4EC",
        "#1E1E1E",
        "#2C2C2C",
        "#1565C0",
        "#2E7D32",
        "#E65100",
        "#C2185B"
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
        // 库 API：初始化沉浸式
        updateImmersion()

        // 库 API：底部内容区域用 applyNavigationBarPadding 避让导航栏
        binding.contentContainer.applyNavigationBarPadding()

        // Toolbar 需要手动计算高度（actionBarSize + 状态栏），库不负责这类自定义尺寸
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val actionBarHeight = resolveActionBarHeight()

            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = actionBarHeight + systemBars.top
            }
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft,
                systemBars.top,
                binding.toolbar.paddingRight,
                binding.toolbar.paddingBottom
            )
            binding.collapsingToolbar.minimumHeight = actionBarHeight + systemBars.top
            insets
        }

        // 监听折叠进度，动态调整状态栏文字颜色
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            currentScrollProgress = if (totalScrollRange != 0) {
                Math.abs(verticalOffset).toFloat() / totalScrollRange
            } else 0f
            updateColorsBasedOnState(currentBackgroundColor)
        }

        updateAppBarContentColor(Color.WHITE)
        updateHeaderContentColor(Color.WHITE)
    }

    private fun updateColorsBasedOnState(baseColor: Int) {
        if (currentScrollProgress > 0.7f) {
            // 完全折叠：Toolbar 显示深色背景和白色标题
            val collapsedColor = darkenColor(baseColor)
            binding.collapsingToolbar.setBackgroundColor(collapsedColor)
            binding.toolbar.setBackgroundColor(collapsedColor)
            binding.toolbar.title = "Coordinator 演示"
            binding.toolbar.setTitleTextColor(Color.WHITE)
            binding.toolbar.setNavigationIconTint(Color.WHITE)
            binding.collapsingToolbar.title = ""
            // 折叠时用浅色文字
            isDarkStatusBarText = false
            updateImmersion()
        } else {
            // 展开：Toolbar 透明，根据背景色自动适配
            binding.collapsingToolbar.setBackgroundColor(baseColor)
            binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            binding.toolbar.title = ""
            updateHeaderContentColor(baseColor)
            val shouldUseDarkText = isLightColor(baseColor)
            isDarkStatusBarText = shouldUseDarkText
            updateImmersion()
            val iconColor = if (shouldUseDarkText) Color.rgb(33, 33, 33) else Color.WHITE
            binding.toolbar.setNavigationIconTint(iconColor)
            binding.toolbar.setTitleTextColor(iconColor)
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

        binding.switchDarkStatus.setOnCheckedChangeListener { _, isChecked ->
            isDarkStatusBarText = isChecked
            updateImmersion()
            updateSystemInfo()
        }

        binding.switchShowStatus.setOnCheckedChangeListener { _, _ ->
            updateImmersion()
            updateSystemInfo()
        }

        binding.fab.applyNavigationBarMargin()
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
                updateAppBarContentColor(animatedColor)
                updateColorsBasedOnState(animatedColor)
            }
        }.start()

        val shouldUseDarkText = isLightColor(color)
        if (shouldUseDarkText != isDarkStatusBarText) {
            isDarkStatusBarText = shouldUseDarkText
            binding.switchDarkStatus.isChecked = shouldUseDarkText
        }
        updateImmersion()

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
        val linearLayout = binding.collapsingToolbar.getChildAt(0) as? LinearLayout
        if (linearLayout != null && linearLayout.childCount >= 2) {
            val textColor = if (isLightBg) Color.rgb(33, 33, 33) else Color.WHITE
            val subtitleColor = if (isLightBg) Color.rgb(97, 97, 97) else Color.rgb(245, 245, 245)
            (linearLayout.getChildAt(0) as? TextView)?.setTextColor(textColor)
            (linearLayout.getChildAt(1) as? TextView)?.setTextColor(subtitleColor)
        }
    }

    private fun updateAppBarContentColor(color: Int) {
        (binding.collapsingToolbar.getChildAt(0) as? LinearLayout)?.setBackgroundColor(color)
    }

    // ======================== 工具 ========================

    private fun resolveActionBarHeight(): Int {
        val typedValue = android.util.TypedValue()
        return if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            android.util.TypedValue.complexToDimensionPixelSize(
                typedValue.data, resources.displayMetrics
            )
        } else 0
    }

    private fun isLightColor(color: Int): Boolean {
        return (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000 > 128
    }

    private fun darkenColor(color: Int): Int {
        val factor = 0.7f
        return Color.rgb(
            (Color.red(color) * factor).toInt(),
            (Color.green(color) * factor).toInt(),
            (Color.blue(color) * factor).toInt()
        )
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
            sb.append("• 状态栏文字: ${if (isDarkStatusBarText) "深色" else "浅色"}")
            binding.tvInfo.text = sb.toString()
        }
    }

    /**
     * 统一的沉浸式更新入口，始终传入所有开关的当前状态。
     * 避免部分调用使用默认参数导致状态被覆盖。
     */
    private fun updateImmersion() {
        setupImmersion(
            showStatusBar = binding.switchShowStatus.isChecked,
            isStatusBarDark = isDarkStatusBarText
        )
    }

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
