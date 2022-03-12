package com.example.githubuserapp.ui.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserapp.api.ApiConfig
import com.example.githubuserapp.api.GithubUserDetailResponse
import com.example.githubuserapp.database.Favorite
import com.example.githubuserapp.databinding.ActivityFavoriteBinding
import com.example.githubuserapp.helper.ViewModelFactory
import com.example.githubuserapp.ui.adapter.ListGithubUserAdapter
import com.example.githubuserapp.ui.main.MainViewModel
import com.example.githubuserapp.ui.user.DetailUserActivity
import com.example.githubuserapp.ui.user.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FavoriteActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private var _binding: ActivityFavoriteBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        mainViewModel = obtainMainViewModel(this@FavoriteActivity)
        getFavoriteUsers()
    }

    override fun onRestart() {
        super.onRestart()

        mainViewModel = obtainMainViewModel(this@FavoriteActivity)
        getFavoriteUsers()
    }

    private fun getFavoriteUsers() {
        mainViewModel.getAllFavorites().observe(this, { favStatus: List<Favorite> ->
            val list = ArrayList<User>()
            for (item in favStatus) {
                try {
                    val user = User(
                        item.username ?: "",
                        null,
                        item.avatar?.toInt(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                    list.add(user)
                } catch (error: Exception) {
                    val user = User(
                        item.username ?: "",
                        null,
                        null,
                        item.avatar,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                    list.add(user)
                }
            }

            showRecyclerList(list)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showRecyclerList(list: ArrayList<User>) {
        _binding?.rvGithubUser?.layoutManager = LinearLayoutManager(this)
        val listGithubUserAdapter = ListGithubUserAdapter(list)
        _binding?.rvGithubUser?.adapter = listGithubUserAdapter

        listGithubUserAdapter.setOnItemClickCallback(object :
            ListGithubUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                getDetailUser(data)
            }
        })
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
                    showDetailUser(data)
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

    private fun showDetailUser(user: User) {
        val toDetailUserActivity = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
        toDetailUserActivity.putExtra(DetailUserActivity.EXTRA_DETAIL_USER, user)
        startActivity(toDetailUserActivity)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            _binding?.progressBar?.visibility = View.VISIBLE
        } else {
            _binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun obtainMainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }
}