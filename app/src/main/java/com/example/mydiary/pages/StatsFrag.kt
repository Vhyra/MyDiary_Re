package com.example.mydiary.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.adapters.CustomDictionaryAdapter
import com.example.mydiary.adapters.CustomStatsAdapter
import com.example.mydiary.data.DataViewModel

class StatsFrag : BaseFrag() {
    private lateinit var records: MutableList<Triple<String, Pair<String, String>, String>>
    private lateinit var recyclerView: RecyclerView
    private lateinit var rootView: View
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_stats, container, false)

        initData()

        rootView.findViewById<Button?>(R.id.h_btn)?.setOnClickListener {
            contract.changeFrag("homepage")
        }

        return rootView

    }

    override fun onResume() {
        super.onResume()

        initData()

    }

    private fun initData(){

        recyclerView = rootView.findViewById(R.id.stats_rec)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val customAdapter = CustomStatsAdapter(
            viewModel.gameStats.value ?: mutableListOf(),
            requireContext(),
            contract)


        recyclerView.adapter = customAdapter

        viewModel.gameStats.observe(viewLifecycleOwner) {
            customAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(): Dictionary {
            return Dictionary()
        }
    }

}