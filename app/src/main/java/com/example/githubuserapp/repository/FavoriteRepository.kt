package com.example.githubuserapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.githubuserapp.database.Favorite
import com.example.githubuserapp.database.FavoriteDao
import com.example.githubuserapp.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteRepository(application: Application) {
    private val mFavoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        mFavoriteDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<Favorite>> = mFavoriteDao.getAllFavorites()

    fun getFavoritesByUsername(userName: String): LiveData<List<Favorite>> = mFavoriteDao.getFavoritesByUsername(userName)

    fun insert(favorite: Favorite) {
        executorService.execute { mFavoriteDao.insert(favorite) }
    }

    fun delete(userName: String) {
        executorService.execute { mFavoriteDao.delete(userName) }
    }

    fun update(favorite: Favorite) {
        executorService.execute { mFavoriteDao.update(favorite) }
    }
}