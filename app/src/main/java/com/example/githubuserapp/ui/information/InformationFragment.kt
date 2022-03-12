package com.example.githubuserapp.ui.information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.githubuserapp.R

class InformationFragment : Fragment() {

    companion object {
        var EXTRA_COMPANY = "extra_company"
        var EXTRA_LOCATION = "extra_location"
        var EXTRA_TOTAL_REPOS = "extra_total_repos"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val company = view.findViewById<TextView>(R.id.tv_company)
        val location = view.findViewById<TextView>(R.id.tv_location)
        val totalRepo = view.findViewById<TextView>(R.id.tv_total_repositories)

        if(arguments != null) {
            val companyText: String = "Company: ${if(arguments?.getString(EXTRA_COMPANY) != "null") arguments?.getString(
                EXTRA_COMPANY
            ) else "-"}"
            val locationText: String = "Location: ${if(arguments?.getString(EXTRA_LOCATION) != "null") arguments?.getString(
                EXTRA_LOCATION
            ) else "-"}"
            val totalRepoText: String = "Total Repo(s): ${arguments?.getString(EXTRA_TOTAL_REPOS)}"

            company.text = companyText
            location.text = locationText
            totalRepo.text = totalRepoText
        }
    }

}