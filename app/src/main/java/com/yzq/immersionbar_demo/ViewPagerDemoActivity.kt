package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yzq.immersion.setupImmersion
import com.yzq.immersionbar_demo.databinding.ActivityViewPagerDemoBinding

class ViewPagerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPagerDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 库 API：Activity 开启沉浸式
        setupImmersion()

        val colors = listOf(Color.parseColor("#F44336"), Color.parseColor("#2196F3"), Color.WHITE)
        binding.viewPager.apply {
            adapter = DemoPagerAdapter(this@ViewPagerDemoActivity, colors)
        }
    }

    private class DemoPagerAdapter(
        activity: FragmentActivity, private val colors: List<Int>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount() = colors.size

        override fun createFragment(position: Int): Fragment {
            val title = when (position) { 0 -> "沉浸"; 1 -> "Padding"; else -> "Margin" }
            return SimpleFragment.newInstance(title, colors[position], position)
        }
    }
}
