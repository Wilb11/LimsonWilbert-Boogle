package com.example.wilbert_boogle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), GameShare {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameBoardFragment = BoardFragment()
        supportFragmentManager.beginTransaction().replace(R.id.gameFragment, gameBoardFragment).commit()
    }

    override fun resetGame() {
        val gameBoardFragment = supportFragmentManager.findFragmentById(R.id.gameFragment) as? BoardFragment
        gameBoardFragment?.resetGame()
    }

}