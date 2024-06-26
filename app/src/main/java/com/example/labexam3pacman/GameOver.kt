package com.example.labexam3pacman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class GameOver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("highScore", 0)

        val tvHighest = findViewById<TextView>(R.id.tvHighest)
        tvHighest.text = highScore.toString()

        val earnedScore = intent.getStringExtra("earnedScore")

        val tvPoints = findViewById<TextView>(R.id.tvPoints)
        tvPoints.text = earnedScore
    }

    fun restart(view: View){

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View){
        finish()
    }

}