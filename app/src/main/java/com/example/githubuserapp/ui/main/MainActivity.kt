package com.example.githubuserapp.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserapp.*
import com.example.githubuserapp.api.ApiConfig
import com.example.githubuserapp.api.GithubUserDetailResponse
import com.example.githubuserapp.databinding.ActivityMainBinding
import com.example.githubuserapp.helper.ViewModelFactory
import com.example.githubuserapp.preferences.SettingsPreferences
import com.example.githubuserapp.ui.adapter.ListGithubUserAdapter
import com.example.githubuserapp.ui.favorite.FavoriteActivity
import com.example.githubuserapp.ui.settings.SettingsActivity
import com.example.githubuserapp.ui.user.DetailUserActivity
import com.example.githubuserapp.ui.user.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val list = ArrayList<User>()
    private var isInitialData = true
    private val initialListGithubUser: ArrayList<User>
        get() {
            val dataFullName = resources.getStringArray(R.array.name)
            val dataUsername = resources.getStringArray(R.array.username)
            val dataCompany = resources.getStringArray(R.array.company)
            val dataLocation = resources.getStringArray(R.array.location)
            val dataTotalRepos = resources.getStringArray(R.array.repository)
            val dataFollowers = resources.getStringArray(R.array.followers)
            val dataFollowing = resources.getStringArray(R.array.following)
            val dataPhoto = resources.obtainTypedArray(R.array.avatar)
            val listUser = ArrayList<User>()
            for (i in dataFullName.indices) {
                val user = User(
                    dataUsername[i],
                    dataFullName[i],
                    dataPhoto.getResourceId(i, -1),
                    null,
                    dataTotalRepos[i],
                    dataLocation[i],
                    dataCompany[i],
                    dataFollowing[i],
                    dataFollowers[i]
                )
                listUser.add(user)
            }
            return listUser
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvGithubUser.setHasFixedSize(true)

        getCurrentTheme()

        list.addAll(initialListGithubUser)
        showRecyclerList()
    }

    private fun getCurrentTheme() {
        val pref = SettingsPreferences.getInstance(dataStore)
        val mainViewModel = obtainViewModel(this@MainActivity)

        mainViewModel.getThemeSettings(pref)?.observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            })
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    private fun showRecyclerList() {
        binding.rvGithubUser.layoutManager = LinearLayoutManager(this)
        val listGithubUserAdapter = ListGithubUserAdapter(list)
        binding.rvGithubUser.adapter = listGithubUserAdapter

        listGithubUserAdapter.setOnItemClickCallback(object :
            ListGithubUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showDetailUser(data)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val settingsBtn = menu.findItem(R.id.settings)
        settingsBtn.setOnMenuItemClickListener(this)
        val favoriteBtn = menu.findItem(R.id.favorite)
        favoriteBtn.setOnMenuItemClickListener(this)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.length > 1) {
                    findUser(query)
                    showRecyclerList()
                    isInitialData = false
                } else if(query.isEmpty()) {
                    list.clear()
                    list.addAll(initialListGithubUser)
                    isInitialData = true
                    showRecyclerList()
                }
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.length > 1) {
                    findUser(newText)
                    isInitialData = false
                } else if(newText.isEmpty()) {
                    list.clear()
                    list.addAll(initialListGithubUser)
                    isInitialData = true
                    showRecyclerList()
                }
                return false
            }
        })

        return true
    }

    private fun showDetailUser(user: User) {
        val toDetailUserActivity = Intent(this@MainActivity, DetailUserActivity::class.java)
        toDetailUserActivity.putExtra(DetailUserActivity.EXTRA_DETAIL_USER, user)
        startActivity(toDetailUserActivity)
    }

    private fun findUser(username: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().searchUser(username)
        client.enqueue(object : Callback<GithubSearchResponse> {
            override fun onResponse(
                call: Call<GithubSearchResponse>,
                response: Response<GithubSearchResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        list.clear()
                        for (i in responseBody.items.indices) {
                            val user = User(
                                responseBody.items[i].login,
                                null,
                                null,
                                responseBody.items[i].avatarUrl,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                            list.add(user)
                        }
                        showRecyclerList()
                    }
                } else {
                    Log.e("SEARCH_USER", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<GithubSearchResponse>, t: Throwable) {
                showLoading(false)
                Log.e("SEARCH_USER", "onFailure: ${t.message}")
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item.toString()) {
            "Settings" -> {
                val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            "Favorite" -> {
                val favoriteIntent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(favoriteIntent)
                true
            }
            else -> {
                false
            }
        }
    }
}