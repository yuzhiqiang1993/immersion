package com.yzq.immersionbar_demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yzq.immersion.applyNavigationBarPadding
import com.yzq.immersion.applyStatusBarPadding
import com.yzq.immersion.applySystemBarsPadding
import com.yzq.immersion.setupImmersion
import com.yzq.immersionbar_demo.databinding.ActivityViewPaddingDemoBinding

/**
 * View Padding 演示页面
 *
 * 演示场景：全屏覆盖层 (Status View) 的 View 级避让。
 * 展示如何对不同 View 独立使用库 API 进行避让。
 */
class ViewPaddingDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPaddingDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPaddingDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 库 API：开启沉浸式
        setupImmersion()

        // 库 API：对需要避让的 View 单独使用扩展
        binding.header.applyStatusBarPadding()
        binding.bottomNav.applyNavigationBarPadding()

        initListeners()
        updateInfo()
    }

    private fun initListeners() {
        binding.btnShowStatusView.setOnClickListener {
            binding.statusViewContainer.visibility = View.VISIBLE
            updateInfo()
        }

        binding.btnDismissStatusView.setOnClickListener {
            binding.statusViewContainer.visibility = View.GONE
        }

        // 库 API：通过 applySystemBarsPadding 动态控制开关
        binding.switchStatusBarPadding.setOnCheckedChangeListener { _, isChecked ->
            binding.statusViewContainer.applySystemBarsPadding(
                addStatusBar = isChecked,
                addNavigationBar = isChecked
            )
            updateInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        binding.root.post {
            val insets = ViewCompat.getRootWindowInsets(binding.root)
            val statusBarHeight = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

            binding.tvMainInfo.text =
                "系统状态栏高度: ${statusBarHeight}px\n" + "Header PaddingTop: ${binding.header.paddingTop}px"

            val titlePadding = binding.tvStatusTitle.paddingTop

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
