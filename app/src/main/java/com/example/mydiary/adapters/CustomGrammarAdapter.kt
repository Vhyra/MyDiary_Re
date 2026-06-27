package com.example.mydiary.adapters

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mydiary.R
import com.example.mydiary.interfaces.MainActInterface
import kotlinx.coroutines.CoroutineScope

class CustomGrammarAdapter(
    private val dataSet: MutableList<Triple<String, String, String>>,
    private val context: Context,
    private val contract: MainActInterface,
    private val scope: CoroutineScope
) :
    RecyclerView.Adapter<CustomGrammarAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rule: TextView
        val chapter: TextView
        val ruleDescription: TextView
        val edit_btn: Button
        val conf_btn: Button
        val del_btn: Button

        var old_rule = ""
        var old_chapter = ""
        var old_desc = ""

        init {
            // Define click listener for the ViewHolder's View
            rule = view.findViewById(R.id.grmmar_rule)
            chapter = view.findViewById(R.id.chapter)
            ruleDescription = view.findViewById(R.id.rule_desc)
            edit_btn = view.findViewById(R.id.edit_btn)
            conf_btn = view.findViewById(R.id.confirm_btn)
            del_btn = view.findViewById(R.id.del_btn)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grammar_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val (rule, description, chapter) = dataSet[position]

        viewHolder.rule.text = rule
        viewHolder.ruleDescription.text = description
        viewHolder.chapter.text = chapter

        viewHolder.del_btn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Conferma azione")
            builder.setMessage("Sei sicuro di voler continuare con questa azione?")

            builder.setPositiveButton("OK") { dialog, which ->
                //mainActivity.dbHelper.removeDay(null, null, date, dayData, false)
                contract.removeRule(
                    rule,
                    description,
                    chapter,
                    null,
                    null,
                    null,
                    false)

                Toast.makeText(context, "Modificato", Toast.LENGTH_SHORT).show()

                dataSet.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, dataSet.size)

            }
            builder.setNegativeButton("Annulla") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }


        viewHolder.edit_btn.setOnClickListener {

            viewHolder.old_rule = (viewHolder.rule.text).toString()
            viewHolder.old_desc = (viewHolder.ruleDescription.text).toString()
            viewHolder.old_chapter = (viewHolder.chapter.text).toString()

            viewHolder.rule.post {
                viewHolder.rule.requestFocus()
                val imm = viewHolder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(viewHolder.rule, InputMethodManager.SHOW_IMPLICIT)
            }

            viewHolder.ruleDescription.post {
                viewHolder.ruleDescription.requestFocus()
                val imm = viewHolder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(viewHolder.ruleDescription, InputMethodManager.SHOW_IMPLICIT)
            }

            viewHolder.chapter.post {
                viewHolder.chapter.requestFocus()
                val imm = viewHolder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(viewHolder.chapter, InputMethodManager.SHOW_IMPLICIT)
            }

            viewHolder.rule.apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
                inputType = InputType.TYPE_CLASS_TEXT
            }

            viewHolder.ruleDescription.apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
                inputType = InputType.TYPE_CLASS_TEXT
                requestFocus() // Metti il focus su questo campo
            }

            viewHolder.chapter.apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
                inputType = InputType.TYPE_CLASS_TEXT
                requestFocus() // Metti il focus su questo campo
            }

            viewHolder.conf_btn.isEnabled = true
            viewHolder.conf_btn.isClickable = true
        }

        viewHolder.conf_btn.setOnClickListener {

            viewHolder.rule.apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL
            }

            viewHolder.ruleDescription.apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL
            }

            viewHolder.chapter.apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL
            }


            viewHolder.conf_btn.isEnabled = false
            viewHolder.conf_btn.isClickable = false

            contract.removeRule(
                rule,
                description,
                chapter,
                viewHolder.rule.text.toString(),
                viewHolder.ruleDescription.text.toString(),
                viewHolder.chapter.text.toString(),
                true)

            Toast.makeText(context, "Modificato", Toast.LENGTH_SHORT).show()

            dataSet[position] = Triple(
                viewHolder.rule.text.toString(),
                viewHolder.ruleDescription.text.toString(),
                viewHolder.chapter.text.toString()
            )

            notifyItemChanged(position)

            viewHolder.conf_btn.isEnabled = false
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}