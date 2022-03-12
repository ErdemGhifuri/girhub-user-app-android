package com.example.githubuserapp.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.githubuserapp.database.Favorite
import com.example.githubuserapp.preferences.SettingsPreferences
import com.example.githubuserapp.repository.FavoriteRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private var pref: SettingsPreferences? = null
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getThemeSettings(currentPref: SettingsPreferences): LiveData<Boolean>? {
        pref = currentPref
        return pref?.getThemeSetting()?.asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        if(pref != null) {
            viewModelScope.launch {
                pref?.saveThemeSetting(isDarkModeActive)
            }
        }
    }

    fun getAllFavorites(): LiveData<List<Favorite>> = mFavoriteRepository.getAllFavorites()

    fun getFavoritesByUsername(userName: String): LiveData<List<Favorite>> = mFavoriteRepository.getFavoritesByUsername(userName)
}