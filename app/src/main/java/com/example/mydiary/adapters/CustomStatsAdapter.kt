package com.example.mydiary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.interfaces.MainActInterface

class CustomStatsAdapter(
    private val dataSet: MutableList<Triple<String, Pair<String, String>, String>>,
    private val context: Context,
    private val contract: MainActInterface
    ) :
    RecyclerView.Adapter<CustomStatsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView
        val correct: TextView
        val missed: TextView
        val wordsList: TextView

        init {
            // Define click listener for the ViewHolder's View
            date = view.findViewById(R.id.date)
            correct = view.findViewById(R.id.correctView)
            missed = view.findViewById(R.id.missedView)
            wordsList = view.findViewById(R.id.listView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.stats_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val (date, pair, words) = dataSet[position]
        val (correct, missed) = pair
        viewHolder.date.text = "Date: " + date
        viewHolder.correct.text = "Correct: " + correct
        viewHolder.missed.text = "Missed: " + missed

        val wordsL = words.split("|||")

        var wrds = ""

        for (w in wordsL){
            wrds += w + "\n"
        }

        println("---------------"+words)

        viewHolder.wordsList.text = wrds

        viewHolder.wordsList.movementMethod = ScrollingMovementMethod.getInstance() //Rende scrollabile notes

        viewHolder.wordsList.setOnTouchListener { v, _ ->
            // Disabilita lo scroll della RecyclerView mentre tocchi la TextView
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}