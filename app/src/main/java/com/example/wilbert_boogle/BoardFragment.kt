package com.example.wilbert_boogle
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class BoardFragment : Fragment() {
    private val selectedLetters = StringBuilder()
    private val selectedButtonIds = mutableListOf<Int>()
    private lateinit var displayWord: TextView

    // Initialize this map directly to avoid unresolved reference
    private val mapOfButtons = mapOf(
        // Your button adjacency mapping
        R.id.button1 to listOf(R.id.button2, R.id.button5, R.id.button6),
        R.id.button2 to listOf(R.id.button1, R.id.button3, R.id.button5, R.id.button6, R.id.button7),
        R.id.button3 to listOf(R.id.button2, R.id.button4, R.id.button6, R.id.button7, R.id.button8),
        R.id.button4 to listOf(R.id.button3, R.id.button7, R.id.button8),
        R.id.button5 to listOf(R.id.button1, R.id.button2, R.id.button6, R.id.button9, R.id.button10),
        R.id.button6 to listOf(R.id.button1, R.id.button2, R.id.button3, R.id.button5, R.id.button7, R.id.button9, R.id.button10, R.id.button11),
        R.id.button7 to listOf(R.id.button2, R.id.button3, R.id.button4, R.id.button6, R.id.button8, R.id.button10, R.id.button11, R.id.button12),
        R.id.button8 to listOf(R.id.button3, R.id.button4, R.id.button7, R.id.button11, R.id.button12),
        R.id.button9 to listOf(R.id.button5, R.id.button6, R.id.button10, R.id.button13, R.id.button14),
        R.id.button10 to listOf(R.id.button5, R.id.button6, R.id.button7, R.id.button9, R.id.button11, R.id.button13, R.id.button14, R.id.button15),
        R.id.button11 to listOf(R.id.button6, R.id.button7, R.id.button8, R.id.button10, R.id.button12, R.id.button14, R.id.button15, R.id.button16),
        R.id.button12 to listOf(R.id.button7, R.id.button8, R.id.button11, R.id.button15, R.id.button16),
        R.id.button13 to listOf(R.id.button9, R.id.button10, R.id.button14),
        R.id.button14 to listOf(R.id.button9, R.id.button10, R.id.button11, R.id.button13, R.id.button15),
        R.id.button15 to listOf(R.id.button10, R.id.button11, R.id.button12, R.id.button14, R.id.button16),
        R.id.button16 to listOf(R.id.button11, R.id.button12, R.id.button15)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.board_fragment, container, false)
        displayWord = view.findViewById(R.id.display_word)
        setupGameBoard(view)
        return view
    }

    private fun setupGameBoard(view: View) {
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val consonants = ('A'..'Z').filterNot { it in vowels }
        val letters = mutableListOf<Char>()
        val alphabet = vowels + consonants
        var vowelCount = 0

        while (letters.size < 16) {
            val letter = if (letters.size >= 14 && vowelCount < 2) {
                vowels.random()
            } else {
                alphabet.random().also { if (it in vowels) vowelCount++ }
            }
            letters.add(letter)
        }

        // Shuffle the list to randomize the board
        letters.shuffle()

        for (i in 1..16) {
            val buttonId = resources.getIdentifier("button$i", "id", requireContext().packageName)
            view.findViewById<Button>(buttonId)?.let { button ->
                val letter = letters[i - 1].toString()
                button.text = letter
                button.setOnClickListener {
                    handleButtonClick(button, letter)
                }
            }
        }
    }

    private fun handleButtonClick(button: Button, letter: String) {
        if (selectedButtonIds.contains(button.id)) {
            Toast.makeText(context, "Letter already used", Toast.LENGTH_SHORT).show()
        } else if (selectedButtonIds.isEmpty() || selectedButtonIds.last() in mapOfButtons[button.id] ?: emptyList()) {
            selectLetter(button, letter)
        } else {
            Toast.makeText(context, "You may only select connected letters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectLetter(button: Button, letter: String) {
        val clickedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.alphabet_layer)
        button.background = clickedDrawable
        selectedLetters.append(letter)
        selectedButtonIds.add(button.id)
        displaySelectedLetters()
    }

    private fun displaySelectedLetters() {
        val spannable = SpannableString(selectedLetters)
        val highlightSpan = BackgroundColorSpan(ContextCompat.getColor(requireContext(), R.color.highlight))
        spannable.setSpan(highlightSpan, 0, selectedLetters.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        displayWord.text = spannable
    }
}
