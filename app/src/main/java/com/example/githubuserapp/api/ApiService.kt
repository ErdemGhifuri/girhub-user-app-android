package com.example.githubuserapp.api

import com.example.githubuserapp.GithubSearchResponse
import com.example.githubuserapp.UserFollowingFollowersResponseItem
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/search/users")
    fun searchUser(
        @Query("q") q: String
    ): Call<GithubSearchResponse>

    @GET("/users/{username}")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<GithubUserDetailResponse>

    @GET("/users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String,
        @Query("page") page: Int = 1
    ): Call<Array<UserFollowingFollowersResponseItem>>

    @GET("/users/{username}/following")
    fun getFollowing(
        @Path("username") username: String,
        @Query("page") page: Int = 1
    ): Call<Array<UserFollowingFollowersResponseItem>>
}