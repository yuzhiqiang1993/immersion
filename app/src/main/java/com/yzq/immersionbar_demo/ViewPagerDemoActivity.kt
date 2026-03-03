package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yzq.immersion.applyNavigationBarPadding
import com.yzq.immersion.setupImmersion
import com.yzq.immersionbar_demo.databinding.ActivityViewPagerDemoBinding

/**
 * ViewPager 演示页面
 *
 * 展示在 ViewPager 中各 Fragment 独立控制避让模式：
 * - Page 1: 完全沉浸（不做避让）
 * - Page 2: applyStatusBarPadding（内容推下，背景蔓延）
 * - Page 3+: applyStatusBarMargin（内容和背景全部推下）
 */
class ViewPagerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPagerDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 库 API：Activity 开启沉浸式
        setupImmersion()

        // 库 API：ViewPager 底部避让导航栏
        binding.viewPager.applyNavigationBarPadding()

        val colors = listOf(
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#9C27B0"), // Purple
            Color.WHITE                  // White
        )

        binding.viewPager.adapter = DemoPagerAdapter(this, colors)
    }

    private class DemoPagerAdapter(
        activity: FragmentActivity,
        private val colors: List<Int>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = colors.size

        override fun createFragment(position: Int): Fragment {
            val avoidMode = when (position) {
                0 -> 0  // 完全沉浸
                1 -> 1  // Padding 避让
                else -> 2  // Margin 避让
            }
            return SimpleFragment.newInstance("Page ${position + 1}", colors[position], avoidMode)
        }
    }
}
