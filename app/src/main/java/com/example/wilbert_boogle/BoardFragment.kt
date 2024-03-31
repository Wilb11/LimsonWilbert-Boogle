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
    private val ChosenLetters = StringBuilder()
    private val selectedButtonIds = mutableListOf<Int>()
    private lateinit var showWord: TextView
    private lateinit var clearFunc: Button
    private lateinit var submitFunc: Button
    private var totalScore = 0
    private val repWords = mutableSetOf<String>()
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
        showWord = view.findViewById(R.id.display_word)
        setupGameBoard(view)
        clearFunc = view.findViewById(R.id.clear_button)
        clearFuncFunction(view)
        submitFunc = view.findViewById(R.id.submit_button)
        submitFuncFunction()
        return view
    }

    private fun submitFuncFunction() {
        submitFunc.setOnClickListener {
            val word = ChosenLetters.toString()
            val scoreChange = when {
                word.length < 4 -> {
                    showToast("Words must be at least 4 chars long", -10)
                    -10
                }
                word.count { it in "AEIOU" } < 2 -> {
                    showToast("All words must utilize a minimum of two vowels", -10)
                    -10
                }
                word in repWords -> {
                    showToast("You cannot generate the same word more than once, even if it’s from different letters", 0)
                    0 // No score change when word is already used
                }
                isWordInDictionary(word) -> {
                    repWords.add(word)
                    val pointsAwarded = computePoints(word)
                    showToast("That’s correct", pointsAwarded)
                    pointsAwarded
                }
                else -> {
                    showToast("That’s incorrect", -10)
                    -10
                }
            }
            updateTotalScore(scoreChange)
        }
    }

    private fun showToast(message: String, scoreChange: Int) {
        val sign = if (scoreChange >= 0) "+" else ""
        Toast.makeText(requireContext(), "$message, $sign$scoreChange", Toast.LENGTH_SHORT).show()
    }

    private fun updateTotalScore(scoreChange: Int) {
        totalScore = maxOf(0, totalScore + scoreChange)
        (activity as? GameShare)?.updateScore(totalScore)
    }

    private fun clearFuncFunction(view: View) {
        clearFunc.setOnClickListener {
            ChosenLetters.clear()
            selectedButtonIds.clear()
            showWord.text = ""
            resetButtons()
        }
    }
    private fun resetButtons() {
        val defaultButton = ContextCompat.getDrawable(requireContext(), R.drawable.individual_button)
        for (i in 1..16) {
            val buttonId = resources.getIdentifier("button$i", "id", context?.packageName)
            val button = view?.findViewById<Button>(buttonId)
            button?.background = defaultButton
        }
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

    private fun isWordInDictionary(word:String): Boolean {
        requireContext().assets.open("words.txt").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                if (word.equals(line, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun computePoints(word: String): Int {
        var points = 0
        val vowelSet = setOf('A', 'E', 'I', 'O', 'U')
        val highValueConsonants = setOf('S', 'Z', 'P', 'X', 'Q')
        var hasHighValueConsonant = false

        for (letter in word.uppercase()) {
            when (letter) {
                in vowelSet -> points += 5
                in highValueConsonants -> {
                    points += 1
                    hasHighValueConsonant = true
                }
                else -> points += 1
            }
        }

        if (hasHighValueConsonant) {
            points *= 2
        }

        return points
    }

    fun resetGame() {
        ChosenLetters.clear()
        selectedButtonIds.clear()
        showWord.text = ""
        totalScore = 0
        (activity as? GameShare)?.updateScore(totalScore)
        resetButtons()
        setupGameBoard(requireView())
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
        ChosenLetters.append(letter)
        selectedButtonIds.add(button.id)
        displayChosenLetters()
    }

    private fun displayChosenLetters() {
        val spannable = SpannableString(ChosenLetters)
        val highlightSpan = BackgroundColorSpan(ContextCompat.getColor(requireContext(), R.color.highlight))
        spannable.setSpan(highlightSpan, 0, ChosenLetters.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        showWord.text = spannable
    }
}
