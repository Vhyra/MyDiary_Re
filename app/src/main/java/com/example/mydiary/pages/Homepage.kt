package com.example.mydiary.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.mydiary.MainActivity
import com.example.mydiary.R
import com.example.mydiary.data.DataViewModel

class Homepage: BaseFrag() {
    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var stats_view: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_homepage, container, false)

        val dic_button: Button = rootView.findViewById(R.id.dictionary_btn)
        val stats_button: Button = rootView.findViewById(R.id.stats_button)
        val memoryGameBtn: Button = rootView.findViewById(R.id.memoryGame_button)
        val diary_button: Button = rootView.findViewById(R.id.diary_button)

        stats_view = rootView.findViewById(R.id.stats_view)

        val img: ImageView = rootView.findViewById(R.id.imageView)

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            stats_view.text = "${getString(R.string.n_days)}: ${stats.first}\n${getString(R.string.n_words)}: ${stats.second}"
        }

        img.setOnClickListener {
            contract.manage_audio()
        }


        dic_button.setOnClickListener {
            contract.changeFrag("dictionary")
        }
        stats_button.setOnClickListener {
            contract.changeFrag("statsFrag")
        }
        memoryGameBtn.setOnClickListener {
            contract.changeFrag("memoryGame")
        }
        diary_button.setOnClickListener {
            contract.changeFrag("diary")
        }

        return rootView

    }

    companion object {
        fun newInstance(): Homepage {
            return Homepage()
        }
    }


}