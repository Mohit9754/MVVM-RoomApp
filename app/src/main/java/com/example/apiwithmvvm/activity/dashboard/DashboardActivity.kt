package com.example.apiwithmvvm.activity.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.apitesting.basic.UtilityTools.Utils
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.databinding.ActivityDashboardBinding
import com.example.apiwithmvvm.fragment.more.MoreFragment
import com.example.apiwithmvvm.fragment.allusers.AllUsersFragment
import com.example.apiwithmvvm.fragment.profile.ProfileFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun setTabAndViewPager() {

        val fragments: List<Fragment> =
            listOf(AllUsersFragment(), ProfileFragment(), MoreFragment())

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = fragments.size

            override fun createFragment(position: Int) = fragments[position]

        }

        val tabText = resources.getStringArray(R.array.arrTab)

        // Link TabLayout with ViewPager
        binding.viewPager.let {
            binding.tabLayout.let { it1 ->
                TabLayoutMediator(it1, it) { tab, position ->
                    tab.text = tabText[position]
                }.attach()
            }
        }
    }

    private fun init() {
        setTabAndViewPager()
    }

}