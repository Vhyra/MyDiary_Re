package com.example.mydiary.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mydiary.R
import com.example.mydiary.data.DataViewModel
import kotlinx.coroutines.launch

class NewGrammar : BaseFrag() {
    private lateinit var viewModel: DataViewModel

    private lateinit var rule: TextView
    private lateinit var descr: TextView
    private lateinit var chapter: TextView
    private lateinit var setText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_new_grammar, container, false)

        val home_button: Button = rootView.findViewById(R.id.home_btn)
        val save_rule: Button = rootView.findViewById(R.id.add_rule)
        rule = rootView.findViewById(R.id.rule)
        descr = rootView.findViewById(R.id.desc)
        chapter = rootView.findViewById(R.id.editChapter)

        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]

        home_button.setOnClickListener {
            contract.changeFrag("homepage")
        }

        save_rule.setOnClickListener {
            save_rule.isClickable = false
            val nRule = rule.text.toString().trim() // Rimuove gli spazi
            val nDescription = descr.text.toString().trim()
            var nChapter = chapter.text.toString().trim()

            // Verifica che i campi word e translation non siano vuoti o solo spazi
            if (nRule.isEmpty() || nDescription.isEmpty() || nChapter.isEmpty()) {
                // Mostra il Toast se uno dei campi è vuoto o contiene solo spazi
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
            } else {
                // Salva il record nel database
                if (!duplicated(nRule)) {
                    lifecycleScope.launch {
                        val success = contract.addRuleRecord(nRule, nDescription, nChapter)
                        if (success) {
                            rule.text = ""
                            descr.text = ""
                            Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            save_rule.isClickable = true

        }



        return rootView
    }

    private fun duplicated(query: String): Boolean {
        val index = viewModel.grammarList.value?.indexOfFirst {
            it.first.equals(query, ignoreCase = true)
        }

        if (index != -1) {
            Toast.makeText(context, getString(R.string.duplic), Toast.LENGTH_SHORT).show()
            return true
        } else {
            return false
        }
    }

    companion object {
        fun newInstance(): NewWord {
            return NewWord()
        }
    }
}