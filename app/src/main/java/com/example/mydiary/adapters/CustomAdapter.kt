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

class CustomAdapter(
    private val dataSet: MutableList<Pair<String, String>>,
    private val context: Context,
    private val contract: MainActInterface,
    private val scope: CoroutineScope
) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date_view: TextView
        val day_view: TextView
        val edit_btn: Button
        val conf_btn: Button
        val del_btn: Button

        var oldDay = ""
        var oldData = ""

        init {
            // Define click listener for the ViewHolder's View
            date_view = view.findViewById(R.id.date_view)
            day_view = view.findViewById(R.id.textView)
            edit_btn = view.findViewById(R.id.edit_btn)
            conf_btn = view.findViewById(R.id.confirm_btn)
            del_btn = view.findViewById(R.id.del_btn)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.page_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val (date, dayData) = dataSet[position]

        viewHolder.date_view.text = date
        viewHolder.day_view.text = dayData

        viewHolder.del_btn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Conferma azione")
            builder.setMessage("Sei sicuro di voler continuare con questa azione?")

            builder.setPositiveButton("OK") { dialog, which ->
                //mainActivity.dbHelper.removeDay(null, null, date, dayData, false)
                contract.removeDay(date, dayData, null, null, false)

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

            viewHolder.oldDay = (viewHolder.date_view.text).toString()
            viewHolder.oldData = (viewHolder.day_view.text).toString()

            viewHolder.day_view.post {
                viewHolder.day_view.requestFocus()
                val imm = viewHolder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(viewHolder.day_view, InputMethodManager.SHOW_IMPLICIT)
            }

            viewHolder.date_view.post {
                viewHolder.date_view.requestFocus()
                val imm = viewHolder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(viewHolder.date_view, InputMethodManager.SHOW_IMPLICIT)
            }

            viewHolder.date_view.apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
                inputType = InputType.TYPE_CLASS_TEXT
            }

            viewHolder.day_view.apply {
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

            viewHolder.date_view.apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL
            }

            viewHolder.day_view.apply {
                isEnabled = false
                isFocusable = false
                isFocusableInTouchMode = false
                inputType = InputType.TYPE_NULL
            }


            viewHolder.conf_btn.isEnabled = false
            viewHolder.conf_btn.isClickable = false

            contract.removeDay(
                viewHolder.oldDay,
                viewHolder.oldData,
                viewHolder.date_view.text.toString(),
                viewHolder.day_view.text.toString(), true)

            Toast.makeText(context, "Modificato", Toast.LENGTH_SHORT).show()

            dataSet[position] = Pair(viewHolder.date_view.text.toString(), viewHolder.day_view.text.toString())

            notifyItemChanged(position)

            viewHolder.conf_btn.isEnabled = false
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}