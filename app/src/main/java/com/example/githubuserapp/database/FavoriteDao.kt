package com.example.githubuserapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: Favorite)

    @Update
    fun update(favorite: Favorite)

    @Query("DELETE from Favorite WHERE username = :userName")
    fun delete(userName: String)

    @Query("SELECT * from Favorite ORDER BY id ASC")
    fun getAllFavorites(): LiveData<List<Favorite>>

    @Query("SELECT * from Favorite WHERE username = :userName ORDER BY id ASC")
    fun getFavoritesByUsername(userName: String): LiveData<List<Favorite>>
}