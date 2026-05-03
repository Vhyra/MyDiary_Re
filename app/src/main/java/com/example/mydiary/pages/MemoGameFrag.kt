package com.example.mydiary.pages

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.data.DataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MemoGameFrag : BaseFrag() {
    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var correctGuess: String
    private lateinit var wordToGuessView: TextView
    private lateinit var scoreView: TextView
    private lateinit var spinner: Spinner
    private var buttons = arrayOfNulls<Button>(4)
    private var k = ""
    private var t = ""
    private var correct = 0
    private var missed = 0
    private var words = ""
    private lateinit var setsWords: Map<String, List<Triple<String, String, Pair<String, String>>>>
    private lateinit var setSelected: List<Triple<String, String, Pair<String, String>>>
    private var to_guess = 0
    private var max_set_value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_memo_game, container, false)

        wordToGuessView = rootView.findViewById(R.id.word_to_guess_view)
        spinner = rootView.findViewById(R.id.setSpinner)

        setsWords = viewModel.records.value?.groupBy { it.third.second } ?: mapOf()

        viewModel.records.observe(viewLifecycleOwner) { records ->
            setsWords = records.groupBy { it.third.second }
        }

        val setNames = setsWords.keys.toList()

        Log.d("SET NAME", setNames.toString())

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, setNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                setSelected = setsWords[setNames[position]]!!.shuffled()
                to_guess = 0
                max_set_value = setSelected.size - 1
                setRandomWord()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        buttons = arrayOf(
            rootView.findViewById(R.id.button_1),
            rootView.findViewById(R.id.button2),
            rootView.findViewById(R.id.button3),
            rootView.findViewById(R.id.button4)
        )

        for(btn in buttons){
            if (btn != null) {
                btn.setOnClickListener {
                    checkGuess(btn.text.toString())
                }
            }
        }

        val skipButton: Button = rootView.findViewById(R.id.skipBtn)
        val bacBtn: Button = rootView.findViewById(R.id.back_btn)
        val saveBtn: Button = rootView.findViewById(R.id.saveBtn)
        scoreView = rootView.findViewById(R.id.scoreView)

        scoreView.text = "${getString(R.string.actualScore)}:\n" +
                "${getString(R.string.right)}:${correct}\n" +
                "${getString(R.string.wrong)}:${missed}"


        skipButton.setOnClickListener {
            missed += 1
            words += "$k: $t - ${getString(R.string.missed)}|||" // for the score

            to_guess += 1

            updateScore()
            setRandomWord()
        }

        bacBtn.setOnClickListener {
            contract.changeFrag("homepage")
        }

        saveBtn.setOnClickListener {
            saveScoreData()
        }

        return  rootView
    }

    private fun updateScore(){
        scoreView.text = "${getString(R.string.actualScore)}:\n" +
                "${getString(R.string.right)}:${correct}\n" +
                "${getString(R.string.wrong)}:${missed}"
    }

    private fun setRandomWord(){

        if (to_guess > max_set_value){
            to_guess = 0
        }
        val randomRow = setSelected[to_guess]
        if(Math.random() < 0.5){
            k = randomRow.first //for the score
            t = randomRow.second

            wordToGuessView.text = randomRow.first
            correctGuess = randomRow.second

            val random = (0..3).random()

            for (i in buttons.indices){
                println("La random è ${random} ----------------------")
                if(i == random){
                    buttons[i]?.setText(correctGuess)
                }else{
                    val randomRow_fake = setSelected.random()
                    buttons[i]?.setText(randomRow_fake.second)

                }
            }

        }else{
            k = randomRow.first //for the score
            t = randomRow.second

            wordToGuessView.text = randomRow.second
            correctGuess = randomRow.first

            val random = (0..3).random()

            for (i in buttons.indices){
                println("La random è ${random} ----------------------")
                if(i == random){
                    buttons[i]?.setText(correctGuess)
                }else{
                    val randomRow_fake = setSelected.random()
                    buttons[i]?.setText(randomRow_fake.first)

                }
            }
        }

    }

    private fun checkGuess(guess: String){
        if(guess.equals(correctGuess, ignoreCase = true)){
            Toast.makeText(context, getString(R.string.correct), Toast.LENGTH_SHORT).show()

            words += "$k: $t - ${getString(R.string.guessed)}|||" // for the score

            correct += 1

            to_guess += 1

            updateScore()
            setRandomWord()
        }else{
            missed += 1
            words += "$k: $t - ${getString(R.string.missed)}|||" // for the score
            updateScore()
            Toast.makeText(context, getString(R.string.retry), Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveScoreData(){
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = formatter.format(Date())

        contract.addScore(formattedDate.toString(),
            correct.toString(),
            missed.toString(),
            words)

        correct = 0
        missed = 0

        to_guess = 0

        updateScore()

    }

    companion object {
        fun newInstance(): Dictionary {
            return Dictionary()
        }
    }
}