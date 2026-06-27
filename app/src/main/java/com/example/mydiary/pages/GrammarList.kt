package com.example.mydiary.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.R
import com.example.mydiary.adapters.CustomAdapter
import com.example.mydiary.adapters.CustomGrammarAdapter
import com.example.mydiary.data.DataViewModel
import kotlin.getValue

class GrammarList : BaseFrag() {
    private lateinit var rootView: View
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grammar_list, container, false)

        initData()

        val home_btn: Button = rootView.findViewById(R.id.home_btn)
        val new_grammar: Button = rootView.findViewById(R.id.add_grammar_button)

        home_btn.setOnClickListener {
            contract.changeFrag("homepage")
        }

        new_grammar.setOnClickListener {
            contract.changeFrag("new_grammar")
        }


        return rootView

    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData(){

        val recyclerView: RecyclerView = rootView.findViewById(R.id.grammar_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val customAdapter = CustomGrammarAdapter(
            viewModel.grammarList.value ?: mutableListOf(),
            requireContext(),
            contract,
            lifecycleScope
        )

        recyclerView.adapter = customAdapter

        viewModel.grammarList.observe(viewLifecycleOwner) {
            customAdapter.notifyDataSetChanged()
        }

    }

    companion object {
        fun newInstance(): GrammarList {
            return GrammarList()
        }
    }

}