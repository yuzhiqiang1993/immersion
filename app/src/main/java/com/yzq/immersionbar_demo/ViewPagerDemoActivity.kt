package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.ActivityViewPagerDemoBinding

class ViewPagerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPagerDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 启用沉浸式
        ImmersionBar.enable(this, paddingNavigationBar = false)

        val colors = listOf(
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#9C27B0"), // Purple
            Color.WHITE                  // White
        )

        val adapter = DemoPagerAdapter(this, colors)
        binding.viewPager.adapter = adapter
    }

    private class DemoPagerAdapter(
        activity: FragmentActivity,
        private val colors: List<Int>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = colors.size

        override fun createFragment(position: Int): Fragment {
            return SimpleFragment.newInstance("Page ${position + 1}", colors[position])
        }
    }
}
