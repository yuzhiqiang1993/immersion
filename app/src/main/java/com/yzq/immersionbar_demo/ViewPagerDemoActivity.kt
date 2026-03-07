package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yzq.immersion.setupImmersion
import com.yzq.immersionbar_demo.databinding.ActivityViewPagerDemoBinding

/**
 * ViewPager 演示页面
 *
 * 三页分别演示：
 * - 沉浸（不避让）
 * - Padding 避让
 * - Margin 避让
 */
class ViewPagerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPagerDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersion()

        val colors = listOf(
            Color.parseColor("#F44336"), Color.parseColor("#2196F3"), Color.WHITE
        )

        binding.viewPager.adapter = DemoPagerAdapter(this, colors)
    }

    private class DemoPagerAdapter(
        activity: FragmentActivity, private val colors: List<Int>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = colors.size

        override fun createFragment(position: Int): Fragment {
            val avoidMode = when (position) {
                0 -> 0
                1 -> 1
                else -> 2
            }
            val title = when (position) {
                0 -> "沉浸"
                1 -> "Padding"
                else -> "Margin"
            }
            return SimpleFragment.newInstance(title, colors[position], avoidMode)
        }
    }
}
