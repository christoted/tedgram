package com.example.tedgram.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.tedgram.R
import com.example.tedgram.databinding.ActivityMainBinding
import com.example.tedgram.presentation.ui.home.HomeFragment
import com.example.tedgram.presentation.ui.notification.NotificationFragment
import com.example.tedgram.presentation.ui.post.PostFragment
import com.example.tedgram.presentation.ui.profile.ProfileFragment
import com.example.tedgram.presentation.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        setCurrentFragment(homeFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.home -> {
                    val homeFragment = HomeFragment()
                    setCurrentFragment(homeFragment)
                }

                R.id.search -> {
                    val searchFragment = SearchFragment()
                    setCurrentFragment(searchFragment)
                }

                R.id.post -> {
                    val postFragment = PostFragment()
                    setCurrentFragment(postFragment)
                }

                R.id.notification -> {
                    val notificationFragment = NotificationFragment()
                    setCurrentFragment(notificationFragment)
                }

                R.id.profile -> {
                    val profileFragment = ProfileFragment()
                    setCurrentFragment(profileFragment)
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentMain, fragment)
            addToBackStack(null)
            commit()
        }
    }
}