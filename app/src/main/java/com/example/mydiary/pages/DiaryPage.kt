package com.example.mydiary.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.R
import com.example.mydiary.adapters.CustomAdapter
import com.example.mydiary.adapters.CustomDictionaryAdapter
import com.example.mydiary.data.DataViewModel
import kotlin.getValue

class DiaryPage : BaseFrag() {
    private lateinit var rootView: View
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_diary_page, container, false)

        initData()

        val home_btn: Button = rootView.findViewById(R.id.home_btn)
        val add_page: Button = rootView.findViewById(R.id.add_page_button)

        home_btn.setOnClickListener {
            contract.changeFrag("homepage")
        }

        add_page.setOnClickListener {
            contract.changeFrag("new_day")
        }


        return rootView

    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData(){

        val recyclerView: RecyclerView = rootView.findViewById(R.id.diary_page_rec)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val customAdapter = CustomAdapter(
            viewModel.dayRecords.value ?: mutableListOf(),
            requireContext(),
            contract,
            lifecycleScope)

        recyclerView.adapter = customAdapter

        viewModel.dayRecords.observe(viewLifecycleOwner) {
            customAdapter.notifyDataSetChanged()
        }

    }

    companion object {
        fun newInstance(): DiaryPage {
            return DiaryPage()
        }
    }

}