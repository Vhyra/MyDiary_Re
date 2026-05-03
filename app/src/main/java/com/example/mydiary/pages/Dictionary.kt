package com.example.mydiary.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.adapters.CustomDictionaryAdapter
import com.example.mydiary.data.DataViewModel


class Dictionary: BaseFrag() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var rootView: View
    private val viewModel: DataViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_dictionary, container, false)

        initData()

        rootView.findViewById<Button?>(R.id.home_btn)?.setOnClickListener {
            contract.changeFrag("homepage")
        }

        rootView.findViewById<Button?>(R.id.add_word)?.setOnClickListener {
            contract.changeFrag("new_word")
        }


        rootView.findViewById<SearchView?>(R.id.searchView)?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { focusOnSearchResult(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return rootView

    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData(){

        recyclerView = rootView.findViewById<RecyclerView>(R.id.diary_page_rec)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val customAdapter = CustomDictionaryAdapter(
            viewModel.records.value ?: mutableListOf(),
            requireContext(),
            contract,
            lifecycleScope
        )

        recyclerView.adapter = customAdapter

        // Notifica adapter se i dati cambiano
        viewModel.records.observe(viewLifecycleOwner) {
            customAdapter.notifyDataSetChanged()
        }
    }

    fun focusOnSearchResult(query: String) {
        val index = viewModel.records.value?.indexOfFirst {
            it.first.contains(query, ignoreCase = true) ||
                    it.second.contains(query, ignoreCase = true)
        } ?: -1

        if (index != -1) {
            recyclerView.scrollToPosition(index)
        } else {
            Toast.makeText(requireContext(), getString(R.string.not_found), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): Dictionary {
            return Dictionary()
        }
    }

}