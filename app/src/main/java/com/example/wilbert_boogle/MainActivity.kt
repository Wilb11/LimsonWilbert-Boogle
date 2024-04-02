package com.example.wilbert_boogle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.hardware.*
import java.util.*
import kotlin.math.*
import android.content.*

class MainActivity : AppCompatActivity(), GameShare {

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)
            ?.registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
        val gameBoardFragment = BoardFragment()
        supportFragmentManager.beginTransaction().replace(R.id.gameFragment, gameBoardFragment).commit()
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // fetch x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 10) {
                resetGame()

            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun updateScore(score: Int) {
        val scoreFragment = supportFragmentManager.findFragmentById(R.id.scoreFragment) as? ScoreFragment
        scoreFragment?.displayScore(score)
    }

    override fun resetGame() {
        val gameBoardFragment = supportFragmentManager.findFragmentById(R.id.gameFragment) as? BoardFragment
        gameBoardFragment?.resetGame()
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

}