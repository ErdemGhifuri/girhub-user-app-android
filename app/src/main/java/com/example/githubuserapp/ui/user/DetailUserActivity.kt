package com.example.githubuserapp.ui.user

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.githubuserapp.R
import com.example.githubuserapp.api.ApiConfig
import com.example.githubuserapp.api.GithubUserDetailResponse
import com.example.githubuserapp.database.Favorite
import com.example.githubuserapp.ui.adapter.SectionsPagerAdapter
import com.example.githubuserapp.databinding.ActivityDetailUserBinding
import com.example.githubuserapp.helper.ViewModelFactory
import com.example.githubuserapp.ui.main.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var detailUserModel: DetailUserModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var detailUser: User
    private var isDelete: Boolean = false

    companion object {
        const val EXTRA_DETAIL_USER = "extra_detail_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailUser = intent.getParcelableExtra<User>(EXTRA_DETAIL_USER) as User

        Glide.with(this)
            .load(detailUser.photo ?: detailUser.avatarUrl) // URL image
            .circleCrop() // change image to circle
            .into(binding.userPhoto) // imageView save place

        val usernameText: String = "@${if (detailUser.username != "null") detailUser.username else "-"}"
        binding.tvUsername.text = usernameText

        getDetailUser(detailUser)
    }

    private fun initiateAddFavoriteBtn() {
        val favoriteFloatingActionButton: FloatingActionButton = findViewById(R.id.fab_add)

        detailUserModel = obtainViewModel(this@DetailUserActivity)
        mainViewModel = obtainMainViewModel(this@DetailUserActivity)

        mainViewModel.getFavoritesByUsername(detailUser.username).observe(this, { favStatus: List<Favorite> ->
            if(favStatus.isNotEmpty()) {
                favoriteFloatingActionButton.setColorFilter(Color.RED)
                isDelete = true
            }
        })

        binding.fabAdd.setOnClickListener {
            if(isDelete) {
                detailUserModel.delete(detailUser.username)
                favoriteFloatingActionButton.colorFilter = null
                isDelete = false
            } else {
                val favorite = Favorite(0, detailUser.username, detailUser.avatarUrl ?: detailUser.photo.toString())
                detailUserModel.insert(favorite)
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailUserModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(DetailUserModel::class.java)
    }

    private fun obtainMainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    private fun getDetailUser(data: User) {
        showLoading(true)
        val client = ApiConfig.getApiService().getUserDetail(data.username)
        client.enqueue(object : Callback<GithubUserDetailResponse> {
            override fun onResponse(
                call: Call<GithubUserDetailResponse>,
                response: Response<GithubUserDetailResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    data.company = responseBody?.company.toString()
                    data.followers = responseBody?.followers.toString()
                    data.following = responseBody?.following.toString()
                    data.fullName = responseBody?.name.toString()
                    data.location = responseBody?.location.toString()
                    data.totalRepositories = responseBody?.publicRepos.toString()

                    binding.tvFullName.text = if (detailUser.fullName != "null") detailUser.fullName else "-"
                    val followersText: String = "Followers : ${detailUser.followers}"
                    binding.tvFollowers.text = followersText
                    val followingText: String = "Following : ${detailUser.following}"
                    binding.tvFollowing.text = followingText

                    val sectionsPagerAdapter = SectionsPagerAdapter(this@DetailUserActivity, detailUser)
                    binding.viewPager.adapter = sectionsPagerAdapter
                    val tabs: TabLayout = findViewById(R.id.tabs)
                    TabLayoutMediator(tabs, binding.viewPager) { tab, position ->
                        tab.text = resources.getString(TAB_TITLES[position])
                    }.attach()
                    supportActionBar?.elevation = 0f

                    initiateAddFavoriteBtn()
                } else {
                    Log.e("DETAIL_USER", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<GithubUserDetailResponse>, t: Throwable) {
                showLoading(false)
                Log.e("DETAIL_USER", "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}