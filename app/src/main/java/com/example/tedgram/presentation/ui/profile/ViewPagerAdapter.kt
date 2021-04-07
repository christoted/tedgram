package com.example.tedgram.presentation.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tedgram.R
import com.example.tedgram.presentation.ui.profile.below.PostFragment
import com.example.tedgram.presentation.ui.profile.below.SavedFragment
import java.util.ArrayList

class ViewPagerAdapter(private val context: ProfileFragment, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        var tabTitles = intArrayOf(R.string.post, R.string.save)
    }


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                 PostFragment()
            }

            1 -> {
                 SavedFragment()
            }

            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabTitles[position])
    }


}