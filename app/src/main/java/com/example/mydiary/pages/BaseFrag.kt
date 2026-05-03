package com.example.mydiary.pages

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.mydiary.interfaces.MainActInterface

abstract class BaseFrag : Fragment() {

    protected lateinit var contract: MainActInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contract = context as? MainActInterface
            ?: throw ClassCastException("Interface not implemented")
    }

}