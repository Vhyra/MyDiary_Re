package com.example.mydiary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.interfaces.MainActInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CustomDictionaryAdapter(
    private val dataSet: MutableList<Triple<String, String, Pair<String, String>>>,
    private val context: Context,
    private val contract: MainActInterface,
    private val scope: CoroutineScope
    ) :
    RecyclerView.Adapter<CustomDictionaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val word: TextView
        val set: TextView
        val translation: TextView
        val notes: TextView
        val delBtn: Button
        val editBtn: Button

        init {
            // Define click listener for the ViewHolder's View
            word = view.findViewById(R.id.word)
            set = view.findViewById(R.id.setNumText)
            translation = view.findViewById(R.id.translation)
            notes = view.findViewById(R.id.notes)
            delBtn = view.findViewById(R.id.delete_btn)
            editBtn = view.findViewById(R.id.editButton)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.words_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val (word, tranlsation, notes_set) = dataSet[position]
        val (notes, set) = notes_set
        viewHolder.word.text = word
        viewHolder.translation.text = tranlsation
        viewHolder.notes.text = notes
        viewHolder.set.text = "Set: ${set}"

        viewHolder.notes.movementMethod = ScrollingMovementMethod.getInstance() //Rende scrollabile notes

        viewHolder.notes.setOnTouchListener { v, _ ->
            // Disabilita lo scroll della RecyclerView mentre tocchi la TextView
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        viewHolder.editBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.popup_edit_word, null)

            // Prendi i campi
            val editWord = dialogView.findViewById<EditText>(R.id.edit_word)
            val editTranslation = dialogView.findViewById<EditText>(R.id.edit_translation)
            val editNotes = dialogView.findViewById<EditText>(R.id.edit_notes)
            val editSet = dialogView.findViewById<EditText>(R.id.edit_set)

            editWord.setText(word)
            editTranslation.setText(tranlsation)
            editNotes.setText(notes)
            editSet.setText(set)

            AlertDialog.Builder(context)
                .setTitle("EDIT")
                .setView(dialogView)
                .setPositiveButton("Ok") { _, _ ->
                    val newWord = editWord.text.toString()
                    val newTranslation = editTranslation.text.toString()
                    val newNotes = editNotes.text.toString()
                    val newSet = editSet.text.toString()

                    contract.removeWord(word, tranlsation, notes)

                    Toast.makeText(context, context.getString(R.string.edited), Toast.LENGTH_SHORT).show()

                    dataSet[position] = Triple(
                        newWord,
                        newTranslation,
                        Pair(newNotes, newSet)
                    )

                    scope.launch { contract.addWordRecord(newWord, newTranslation, newNotes, newSet) }

                    notifyItemChanged(position)

                }
                .setNegativeButton(context.getString(R.string.revertAction), null)
                .show()

        }

        viewHolder.delBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.confirm))
            builder.setMessage(context.getString(R.string.warning))

            builder.setPositiveButton("OK") { dialog, which ->
                contract.removeWord(word, tranlsation, notes)

                Toast.makeText(context, context.getString(R.string.edited), Toast.LENGTH_SHORT).show()

                dataSet.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, dataSet.size)

            }
            builder.setNegativeButton(context.getString(R.string.revertAction)) { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }


    fun focusOnSearchResult(query: String): Int {
        val index = dataSet.indexOfFirst { (word, _) ->
            word.toString().contains(query, ignoreCase = true)
        }

        if (index != -1) {
            return index
        } else {
            Toast.makeText(context, "Nessun risultato trovato", Toast.LENGTH_SHORT).show()
            return -1
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}