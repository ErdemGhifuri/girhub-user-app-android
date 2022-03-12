package com.example.githubuserapp.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.githubuserapp.ui.follower.FollowerFragment
import com.example.githubuserapp.ui.following.FollowingFragment
import com.example.githubuserapp.ui.information.InformationFragment
import com.example.githubuserapp.ui.user.User

class SectionsPagerAdapter(activity: AppCompatActivity, private val detailUser: User) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                val mInformationFragment = InformationFragment()
                val mBundle = Bundle()

                mBundle.putString(InformationFragment.EXTRA_COMPANY, detailUser.company)
                mBundle.putString(InformationFragment.EXTRA_LOCATION, detailUser.location)
                mBundle.putString(InformationFragment.EXTRA_TOTAL_REPOS, detailUser.totalRepositories)

                mInformationFragment.arguments = mBundle

                fragment = mInformationFragment
                return fragment
            }
            1 -> {
                val mFollowerFragment = FollowerFragment()
                val mBundle = Bundle()

                mBundle.putString(FollowerFragment.EXTRA_USERNAME, detailUser.username)
                mFollowerFragment.arguments = mBundle

                fragment = mFollowerFragment
                return fragment
            }
            2 -> {
                val mFollowingFragment = FollowingFragment()
                val mBundle = Bundle()

                mBundle.putString(FollowingFragment.EXTRA_USERNAME, detailUser.username)
                mFollowingFragment.arguments = mBundle

                fragment = mFollowingFragment
                return fragment
            }
        }
        return fragment as Fragment
    }

}