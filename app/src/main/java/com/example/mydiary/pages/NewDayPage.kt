package com.example.mydiary.pages

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.data.DataViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.contracts.contract

class NewDayPage : BaseFrag() {
    private var dateText = ""
    private var journalText = ""
    private var word_suggestion: TextView? = null
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contract.setCurrentFragment("new_day")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_new_day_page, container, false)

        val date: EditText = rootView.findViewById(R.id.editTextDate)
        val text: EditText = rootView.findViewById(R.id.editTextTextMultiLine)

        val dictionaryContainer = rootView.findViewById<View>(R.id.landscapeFragmentDicView)
        if (dictionaryContainer != null) {
            // Se questo container è presente, siamo in modalità landscape
            if (childFragmentManager.findFragmentById(R.id.landscapeFragmentDicView) == null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.landscapeFragmentDicView, Dictionary())
                    .commit()
            }
        }

        rootView.findViewById<Button?>(R.id.home_btn)?.setOnClickListener {
            contract.changeFrag("homepage")
        }

        rootView.findViewById<Button?>(R.id.fast_dictionary_btn)
            ?.setOnClickListener {
                contract.changeFrag("new_word")
            }

        rootView.findViewById<Button?>(R.id.land_home_btn)?.setOnClickListener {
            contract.changeFrag("homepage")
        }

        rootView.findViewById<Button?>(R.id.land_save_btn)?.setOnClickListener {
            Toast.makeText(context, getString(R.string.saveInProgress), Toast.LENGTH_SHORT).show()
            rootView.findViewById<Button?>(R.id.land_save_btn)?.isClickable = false
            save(date, text)
            rootView.findViewById<Button?>(R.id.land_save_btn)?.isClickable = true

        }

        rootView.findViewById<Button?>(R.id.save_data_btn)?.setOnClickListener {
            Toast.makeText(context, getString(R.string.saveInProgress), Toast.LENGTH_SHORT).show()
            rootView.findViewById<Button?>(R.id.save_data_btn)?.isClickable = false
            save(date, text)
            rootView.findViewById<Button?>(R.id.save_data_btn)?.isClickable = true
        }

        word_suggestion = rootView.findViewById(R.id.wordSuggestion)

        word_suggestion?.movementMethod = ScrollingMovementMethod.getInstance()

        rootView.findViewById<SearchView?>(R.id.wordSearchView)?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { wordSuggestion(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return rootView
    }

    private fun save(date: EditText, text: EditText){

        if(checkDate(date.text.toString())) {
            lifecycleScope.launch {
                val saved = contract.addRecord(date.text.toString(), text.text.toString())
                if (saved) {
                    Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    date.setText("")
                    text.setText("")
                } else {
                    Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(context, getString(R.string.dateError), Toast.LENGTH_SHORT).show()
        }


    }

    private fun checkDate(date: String): Boolean{
        val regex = Regex("""\d{2}/\d{2}/\d{4}""") // Formato: dd/MM/yyyy

        return regex.matches(date)
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onPause() {
        super.onPause()
        // Forza portrait solo se stiamo uscendo dal fragment (non durante la rotazione)
        val isChangingConfigurations = activity?.isChangingConfigurations ?: false
        if (!isChangingConfigurations) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Salva i valori correnti dei campi di testo
        val dateField = view?.findViewById<EditText>(R.id.editTextDate)
        val textField = view?.findViewById<EditText>(R.id.editTextTextMultiLine)

        dateText = dateField?.text?.toString() ?: ""
        journalText = textField?.text?.toString() ?: ""
    }

    private fun wordSuggestion(query: String) {
        val matches = viewModel.records.value?.filter {
            it.first.contains(query, ignoreCase = true) ||
                    it.second.contains(query, ignoreCase = true)
        } ?: mutableListOf()

        if (matches.isNotEmpty()) {
            val suggestionText = matches.joinToString(separator = "\n\n") { "${it.first}:\n${it.second}" }
            word_suggestion?.text = suggestionText
        } else {
            Toast.makeText(context, getString(R.string.not_found), Toast.LENGTH_SHORT).show()
        }
    }

    fun convertDateToISO(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString // Se la conversione fallisce, restituisce la data originale
        }
    }


    companion object {
        fun newInstance(): NewDayPage {
            return NewDayPage()
        }
    }
}
