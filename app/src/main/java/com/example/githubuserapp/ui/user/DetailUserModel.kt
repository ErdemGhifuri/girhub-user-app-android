package com.example.githubuserapp.ui.user

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.githubuserapp.database.Favorite
import com.example.githubuserapp.repository.FavoriteRepository

class DetailUserModel (application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun insert(favorite: Favorite) {
        mFavoriteRepository.insert(favorite)
    }

    fun update(favorite: Favorite) {
        mFavoriteRepository.update(favorite)
    }

    fun delete(userName: String) {
        mFavoriteRepository.delete(userName)
    }
}