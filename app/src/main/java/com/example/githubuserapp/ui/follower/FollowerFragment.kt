package com.example.githubuserapp.ui.follower

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubuserapp.ui.adapter.ListFollowerFollowingAdapter
import com.example.githubuserapp.R
import com.example.githubuserapp.UserFollowingFollowersResponseItem
import com.example.githubuserapp.api.ApiConfig
import com.example.githubuserapp.api.GithubUserDetailResponse
import com.example.githubuserapp.ui.user.DetailUserActivity
import com.example.githubuserapp.ui.user.User
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowerFragment : Fragment() {

    private lateinit var followerFragmentView: View
    private lateinit var username: String
    private lateinit var rvUserFollower: RecyclerView
    private var isLoading = false
    private val list = ArrayList<User>()
    private var page = 1

    companion object {
        var EXTRA_USERNAME = "extra_username"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        followerFragmentView = view
        rvUserFollower = followerFragmentView.findViewById(R.id.rv_user_follower)

        if(arguments != null) {
            username = arguments?.getString(EXTRA_USERNAME).toString()

            getFollower()

            rvUserFollower.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    lifecycleScope.launch(Dispatchers.Default) {
                        delay(3000)
                        if (!recyclerView.canScrollVertically(1)) {
                            withContext(Dispatchers.Main) {
                                if(!isLoading) {
                                    isLoading = true
                                    page += 1
                                getFollower()
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun showRecyclerList() {
        rvUserFollower.layoutManager = LinearLayoutManager(requireContext())
        val listUserFollowerAdapter = ListFollowerFollowingAdapter(list)
        rvUserFollower.adapter = listUserFollowerAdapter

        listUserFollowerAdapter.setOnItemClickCallback(object :
            ListFollowerFollowingAdapter.OnItemClickCallback {
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
        val toDetailUserActivity = Intent(requireContext(), DetailUserActivity::class.java)
        toDetailUserActivity.putExtra(DetailUserActivity.EXTRA_DETAIL_USER, user)
        startActivity(toDetailUserActivity)
    }

    private fun showLoading(isLoading: Boolean) {
        val progressBar: ProgressBar = followerFragmentView.findViewById(R.id.progressBar)
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun getFollower() {
        showRecyclerList()
        showLoading(true)
        val client = ApiConfig.getApiService().getFollowers(username, page)
        client.enqueue(object : Callback<Array<UserFollowingFollowersResponseItem>> {
            override fun onResponse(
                call: Call<Array<UserFollowingFollowersResponseItem>>,
                response: Response<Array<UserFollowingFollowersResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        for (i in responseBody.indices) {
                            val user = User(
                                responseBody[i].login,
                                null,
                                null,
                                responseBody[i].avatarUrl,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                            list.add(user)
                        }
                        showRecyclerList()
                        isLoading = false
                    }
                } else {
                    Log.e("SEARCH_USER", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Array<UserFollowingFollowersResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e("SEARCH_USER", "onFailure: ${t.message}")
            }
        })
    }
}