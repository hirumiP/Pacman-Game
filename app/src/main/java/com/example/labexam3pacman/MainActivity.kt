package com.example.labexam3pacman


import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import kotlin.random.Random

class MainActivity : Activity() {

    private lateinit var board: RelativeLayout
    private lateinit var border: RelativeLayout
    private lateinit var lilu : LinearLayout
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var pauseButton: Button
    private lateinit var newgame: Button
    private lateinit var resume: Button
    private lateinit var playagain: Button
    private lateinit var score: Button
    private lateinit var score2: Button
    private lateinit var pacman: ImageView
    private lateinit var ghost: ImageView
    private val obstacles = mutableListOf<ImageView>()
    private val dots = mutableListOf<ImageView>()
    private val random = java.util.Random()
    private var scorex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        board = findViewById(R.id.board)
        border = findViewById(R.id.relativeLayout)
        lilu = findViewById(R.id.main)
        upButton = findViewById(R.id.up)
        downButton = findViewById(R.id.down)
        leftButton = findViewById(R.id.left)
        rightButton = findViewById(R.id.right)
        newgame = findViewById(R.id.new_game)
        resume = findViewById(R.id.resume)
        playagain = findViewById(R.id.playagain)
        score = findViewById(R.id.score)
        score2 = findViewById(R.id.score2)
        pacman = ImageView(this)
        ghost = ImageView(this)

        board.visibility = View.INVISIBLE
        playagain.visibility = View.INVISIBLE
        score.visibility = View.INVISIBLE
        score2.visibility = View.INVISIBLE
        newgame.setOnClickListener{
            setUpGame()
        }

    }

    private fun setUpGame(){
        board.visibility = View.VISIBLE
        newgame.visibility = View.INVISIBLE
        resume.visibility = View.INVISIBLE
        score2.visibility = View.VISIBLE
        //Reset score
        scorex = 0
        score2.text = "score : " + scorex.toString()

        //create Pac-Man
        pacman.setImageResource(R.drawable.pacman)
        pacman.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        board.addView(pacman)
        pacman.x = 20f
        pacman.y = 20f

        //create Ghost
        ghost.setImageResource(R.drawable.meat)
        ghost.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        board.addView(ghost)
        ghost.x = 380f
        ghost.y = 20f

        //create obstacles
        obstacles.clear()
        val obstacleSize = 300 //obstacle size
        val obstacleMargin = 20
        for (i in 0 until 4){
            val obstacle = ImageView(this)
            obstacle.setImageResource(R.drawable.obstacle1)
            obstacle.layoutParams = RelativeLayout.LayoutParams(obstacleSize, obstacleSize)
            val obstacleX = (board.width/2 - obstacleSize/2).toFloat()
            val obstacleY = (board.height/2 - obstacleSize/2).toFloat()
            obstacle.x = obstacleX + (if (i% 2 == 0) obstacleSize + obstacleMargin else - obstacleSize-obstacleMargin)
            obstacle.y = obstacleY + (if (i<2) obstacleSize + obstacleMargin else - obstacleSize- obstacleMargin)
            board.addView(obstacle)
            obstacles.add(obstacle)
        }
        obstacles[0].x = -200f
        obstacles[0].y = -150f

        obstacles[1].x = -200f
        obstacles[1].y = 250f

        obstacles[2].x = 250f
        obstacles[2].y = -150f

        obstacles[3].x = 250f
        obstacles[3].y = 250f

        createDotSets()

        startGameLopp()

    }
    private fun createDotSets(){
        val dotSize = 20

        for (i in 0 until 100){
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.dot)
            dot.layoutParams = RelativeLayout.LayoutParams(dotSize, dotSize)
            val randomX = random.nextInt(board.width- dotSize)
            val randomY = random.nextInt(board.height- dotSize)
            dot.x = randomX.toFloat()
            dot.y = randomY.toFloat()
            board.addView(dot)
            dots.add(dot)
        }
    }

    private fun startGameLopp(){
        GlobalScope.launch(Dispatchers.Main){
            while (true){
                updatePacmanPosition()

                moveGhostRandomly()

                checkCollosions()

                delay(100)
            }
        }
    }

    private var pacmanInitialX = 20f
    private  var pacmanInitialY = 20f

    private fun updatePacmanPosition(){
        leftButton.setOnClickListener{
            pacman.x -= 30
        }
        rightButton.setOnClickListener{
            pacman.x += 30
        }
        upButton.setOnClickListener{
            pacman.y -= 30
        }
        downButton.setOnClickListener{
            pacman.y += 30
        }

        if (pacman.x <0) pacman.x = 0f
        if (pacman.y < 0) pacman.y = 0f
        if (pacman.x > board.width - pacman.width)pacman.x = (board.width-pacman.width).toFloat()
        if (pacman.y > board.height - pacman.height) pacman.y = (board.height- pacman.height).toFloat()

        for(dot in dots){
            if (pacman.x < dot.x + dot.width &&
                pacman.x + pacman.width> dot.x &&
                pacman.y < dot.y + dot.height &&
                pacman.y + pacman.height> dot.y
                ){
                pacmanInitialX = pacman.x
                pacmanInitialY = pacman.y
                break
            }
        }
    }

    private fun moveGhostRandomly(){
        val randomDirection = random.nextInt(4) // 0: up, 1: down, 2: left, 3: right

        when(randomDirection) {
            0 -> { // Move up
                ghost.y -= 30
            }
            1 -> { // Move down
                ghost.y += 30
            }
            2 -> { // Move left
                ghost.x -= 30
            }
            3 -> { // Move right
                ghost.x += 30
            }
        }

        // If ghost reaches the border, change direction randomly
        if (ghost.x < 0 || ghost.x + ghost.width > board.width ||
            ghost.y < 0 || ghost.y + ghost.height > board.height) {
            moveGhostRandomly()
        }
    }

    private fun checkCollosions(){
        for (obstacle in obstacles){
            if (pacman.x < obstacle.x + obstacle.width &&
                pacman.x + pacman.width > obstacle.x &&
                pacman.y < obstacle.y + obstacle.height &&
                pacman.y + pacman.height > obstacle.y
                ){
                pacman.x -=10
                pacman.y -=10
            }
        }

        val iterator = dots.iterator()

        while (iterator.hasNext()){
            val dot = iterator.next()
            if (pacman.x < dot.x + dot.width &&
                pacman.x + pacman.width > dot.x &&
                pacman.y < dot.y + dot .height &&
                pacman.y + pacman.height > dot.y
                ){
                scorex++
                score2.text = "score : $scorex"
                board.removeView(dot)
                iterator.remove()

            }
        }
        if(pacman.x < ghost.x + ghost.width &&
            pacman.x + pacman.width > ghost.x &&
            pacman.y < ghost.y + ghost.height &&
            pacman.y + pacman.height > ghost.y
            ){
            endGame()
        }
    }

    private fun endGame(){
        pacman.visibility = View.INVISIBLE
        ghost.visibility = View.INVISIBLE

        for(obstacle in obstacles){
            obstacle.visibility = View.INVISIBLE
        }

        for (dot in dots){
            dot.visibility = View.INVISIBLE
        }
        border.setBackgroundColor(resources.getColor(R.color.red))
        playagain.visibility = View.VISIBLE
        lilu.visibility = View.INVISIBLE
        score.text = " Your score is  " + scorex.toString()
        score2.visibility = View.INVISIBLE

        val sharedPreferences = getSharedPreferences("MyPrefs" , 0)
        var highScore = sharedPreferences.getInt("highScore",0)

        if (scorex>highScore){
            highScore= scorex

            val editor = sharedPreferences.edit()
            editor.putInt("highScore", highScore)
            editor.apply()
        }
    }

    fun goToGameOverScreen(view: View){
        val earnedScore = findViewById<TextView>(R.id.score2).text.toString()
        val intent = Intent(this, GameOver::class.java).apply{
            putExtra("earnedScore", earnedScore)
        }
        startActivity(intent)
    }


}
