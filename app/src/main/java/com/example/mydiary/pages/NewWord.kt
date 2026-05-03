package com.example.mydiary.pages

import android.os.Bundle
import android.os.LocaleList
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
import java.util.Locale


class NewWord: BaseFrag() {
    private lateinit var viewModel: DataViewModel

    private lateinit var word: TextView
    private lateinit var text: TextView
    private lateinit var notes: TextView
    private lateinit var setText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_new_word, container, false)

        val home_button: Button = rootView.findViewById(R.id.home_btn)
        val save_word: Button = rootView.findViewById(R.id.add_word)
        word = rootView.findViewById(R.id.word)
        text = rootView.findViewById(R.id.translation)
        notes = rootView.findViewById(R.id.notes)
        setText = rootView.findViewById(R.id.editTextNumber)

        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]

        word.imeHintLocales = LocaleList(Locale.KOREAN)

        home_button.setOnClickListener {
            contract.changeFrag("homepage")
        }

        save_word.setOnClickListener {
            save_word.isClickable = false
            val nWord = word.text.toString().trim() // Rimuove gli spazi
            val translation = text.text.toString().trim()
            var notesText = notes.text.toString().trim()
            var set = setText.text.toString().trim()

            // Verifica che i campi word e translation non siano vuoti o solo spazi
            if (nWord.isEmpty() || translation.isEmpty()) {
                // Mostra il Toast se uno dei campi è vuoto o contiene solo spazi
                Toast.makeText(context, getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
            } else {
                // Se il campo notes è vuoto, assegnagli un trattino "-"
                if (notesText.isEmpty()) {
                    notesText = "-"
                }

                if (set.isEmpty()){
                    set = "0"
                }

                // Salva il record nel database
                if(!duplicated(nWord)) {
                    lifecycleScope.launch {
                        val success = contract.addWordRecord(nWord, translation, notesText, set)
                        if (success) {
                            word.text = ""
                            text.text = ""
                            notes.text = ""
                            setText.text = ""
                            Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            save_word.isClickable = true

        }



        return rootView
    }

    private fun duplicated(query: String): Boolean {
        val index = viewModel.records.value?.indexOfFirst {
            it.first.equals(query, ignoreCase = true)
        }

        if (index != -1) {
            Toast.makeText(context, getString(R.string.duplicated), Toast.LENGTH_SHORT).show()
            return true
        }else{
            return false
        }
    }

    companion object {
        fun newInstance(): NewWord {
            return NewWord()
        }
    }

}