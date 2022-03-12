package com.example.githubuserapp.ui.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String,
    var fullName: String?,
    var photo: Int?,
    var avatarUrl: String?,
    var totalRepositories: String?,
    var location: String?,
    var company: String?,
    var following: String?,
    var followers: String?
) : Parcelable