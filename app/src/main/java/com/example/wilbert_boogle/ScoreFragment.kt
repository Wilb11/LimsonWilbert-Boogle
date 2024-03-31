package com.example.wilbert_boogle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
class ScoreFragment: Fragment() {
    private var scoreTextView: TextView? = null
    private var newGameButton: Button? = null
    private val GameShare by lazy { activity as? GameShare }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.score_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreTextView = view.findViewById(R.id.score_number)
        newGameButton = view.findViewById(R.id.new_game_button)
        newGameButton?.setOnClickListener {
            GameShare?.resetGame()
        }
    }

    override fun onDestroyView() {
        scoreTextView = null
        newGameButton = null
        super.onDestroyView()
    }

}