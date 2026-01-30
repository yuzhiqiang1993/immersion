package com.yzq.immersionbar_demo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.ActivityViewPaddingDemoBinding

/**
 * View Padding 演示页面
 *
 * 演示场景：全屏覆盖层 (Status View) 的沉浸式处理
 *
 * 核心问题：
 * 当 Activity 启用沉浸式 (Edge-to-Edge) 后，内容会延伸到状态栏下方。
 * 如果页面上覆盖了一个全屏的 Loading/Error View，它的顶部内容（如标题栏）也会被状态栏遮挡。
 *
 * 解决方案：
 * 使用 `ImmersionBar.applyInsetsPadding(view, paddingStatusBar = true)` 为覆盖层的顶部 View 独立添加 padding，
 * 而不影响底层主页面的布局。
 */
class ViewPaddingDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPaddingDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPaddingDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 主页面启用基础沉浸式
        // paddingStatusBar=false, paddingNavigationBar=false 表示内容完全延伸，由我们手动控制 padding
        ImmersionBar.enable(
            activity = this,
            paddingStatusBar = false,
            paddingNavigationBar = false,
            darkStatusBarText = false
        )

        // 2. 模拟主页面的一些 padding (防止主内容被遮挡)
        // 这里只是为了让主页面好看一点，不是本次演示的重点
        ImmersionBar.applyInsetsPadding(binding.header, paddingStatusBar = true)
        ImmersionBar.applyInsetsPadding(binding.bottomNav, paddingNavigationBar = true)

        initListeners()
        updateInfo()
    }

    private fun initListeners() {
        // --- 主页面事件 ---
        binding.btnShowStatusView.setOnClickListener {
            binding.statusViewContainer.visibility = View.VISIBLE
            updateInfo()
        }

        // --- Status View 事件 ---
        binding.btnDismissStatusView.setOnClickListener {
            binding.statusViewContainer.visibility = View.GONE
        }

        // 核心：控制 Status View 顶部的 Title 是否避开状态栏
        binding.switchStatusBarPadding.setOnCheckedChangeListener { _, isChecked ->
            applyStatusViewPadding(isChecked)
            updateInfo()
        }
    }

    /**
     * 对 Status View 的标题栏应用/清除 padding
     */
    private fun applyStatusViewPadding(apply: Boolean) {
        if (apply) {
            // [核心 API]
            // 为 tvStatusTitle 增加顶部 padding，使其避开状态栏
            ImmersionBar.applyInsetsPadding(
                view = binding.tvStatusTitle,
                paddingStatusBar = true
            )
        } else {
            // 清除 padding，恢复原始状态 (会被状态栏遮挡)
            ImmersionBar.clearInsetsPadding(binding.tvStatusTitle)
        }
    }


    /**
     * 更新界面信息展示
     */
    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        binding.root.post {
            val insets = ViewCompat.getRootWindowInsets(binding.root)
            val statusBarHeight = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

            // 主页面信息
            binding.tvMainInfo.text = "系统状态栏高度: ${statusBarHeight}px\n" +
                    "Header PaddingTop: ${binding.header.paddingTop}px"

            // Status View 信息
            val titlePadding = binding.tvStatusTitle.paddingTop
            val isApplied = binding.switchStatusBarPadding.isChecked
            
            val statusInfo = StringBuilder()
            statusInfo.append("Title PaddingTop: ${titlePadding}px\n")
            if (titlePadding > 0 && titlePadding >= statusBarHeight) {
                statusInfo.append("✅ 已避开状态栏 (Padding >= StatusBar)")
            } else {
                statusInfo.append("⚠️ 文字被遮挡 (Padding < StatusBar)")
            }
            
            binding.tvStatusInfo.text = statusInfo.toString()
        }
    }
}
