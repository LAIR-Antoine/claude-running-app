package com.example.runningcalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var distanceInput: EditText
    private lateinit var timeHoursInput: EditText
    private lateinit var timeMinutesInput: EditText
    private lateinit var timeSecondsInput: EditText
    private lateinit var paceMinutesInput: EditText
    private lateinit var paceSecondsInput: EditText
    private lateinit var speedInput: EditText
    private lateinit var resultText: TextView
    private lateinit var calculatorGroup: RadioGroup

    private lateinit var distanceLayout: LinearLayout
    private lateinit var timeLayout: LinearLayout
    private lateinit var paceLayout: LinearLayout
    private lateinit var speedLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupCalculateButton()
        setupClearButton()
        setupRadioGroup()
        updateVisibleFields()
    }

    private fun initializeViews() {
        distanceInput = findViewById(R.id.distanceInput)
        timeHoursInput = findViewById(R.id.timeHoursInput)
        timeMinutesInput = findViewById(R.id.timeMinutesInput)
        timeSecondsInput = findViewById(R.id.timeSecondsInput)
        paceMinutesInput = findViewById(R.id.paceMinutesInput)
        paceSecondsInput = findViewById(R.id.paceSecondsInput)
        speedInput = findViewById(R.id.speedInput)
        resultText = findViewById(R.id.resultText)
        calculatorGroup = findViewById(R.id.calculatorGroup)

        distanceLayout = findViewById(R.id.distanceLayout)
        timeLayout = findViewById(R.id.timeLayout)
        paceLayout = findViewById(R.id.paceLayout)
        speedLayout = findViewById(R.id.speedLayout)
    }

    private fun setupCalculateButton() {
        findViewById<Button>(R.id.calculateButton).setOnClickListener {
            when (calculatorGroup.checkedRadioButtonId) {
                R.id.calculatePace -> calculatePace()
                R.id.calculateDistance -> calculateDistance()
                R.id.calculateTime -> calculateTime()
                R.id.convertPaceSpeed -> convertPaceSpeed()
            }
        }
    }

    private fun setupClearButton() {
        findViewById<Button>(R.id.clearButton).setOnClickListener {
            distanceInput.setText("")
            timeHoursInput.setText("")
            timeMinutesInput.setText("")
            timeSecondsInput.setText("")
            paceMinutesInput.setText("")
            paceSecondsInput.setText("")
            speedInput.setText("")
            resultText.text = ""
        }
    }

    private fun setupRadioGroup() {
        calculatorGroup.setOnCheckedChangeListener { _, _ ->
            updateVisibleFields()
            resultText.text = ""
        }
    }

    private fun updateVisibleFields() {
        when (calculatorGroup.checkedRadioButtonId) {
            R.id.calculatePace -> {
                distanceLayout.visibility = View.VISIBLE
                timeLayout.visibility = View.VISIBLE
                paceLayout.visibility = View.GONE
                speedLayout.visibility = View.GONE
            }
            R.id.calculateDistance -> {
                distanceLayout.visibility = View.GONE
                timeLayout.visibility = View.VISIBLE
                paceLayout.visibility = View.VISIBLE
                speedLayout.visibility = View.GONE
            }
            R.id.calculateTime -> {
                distanceLayout.visibility = View.VISIBLE
                timeLayout.visibility = View.GONE
                paceLayout.visibility = View.VISIBLE
                speedLayout.visibility = View.GONE
            }
            R.id.convertPaceSpeed -> {
                distanceLayout.visibility = View.GONE
                timeLayout.visibility = View.GONE
                paceLayout.visibility = View.VISIBLE
                speedLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun getTimeInSeconds(): Int {
        val hours = timeHoursInput.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
        val minutes = timeMinutesInput.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
        val seconds = timeSecondsInput.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
        return hours * 3600 + minutes * 60 + seconds
    }

    private fun getPaceInSeconds(): Int {
        val minutes = paceMinutesInput.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
        val seconds = paceSecondsInput.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
        return minutes * 60 + seconds
    }

    private fun calculatePace() {
        try {
            val distance = distanceInput.text.toString().toDouble()
            val totalSeconds = getTimeInSeconds()

            if (distance <= 0) throw IllegalArgumentException("Distance must be positive")
            if (totalSeconds <= 0) throw IllegalArgumentException("Time must be positive")

            val paceSeconds = totalSeconds / distance
            val paceMinutes = (paceSeconds / 60).toInt()
            val remainingSeconds = (paceSeconds % 60).toInt()

            resultText.text = getString(R.string.pace_result, paceMinutes, remainingSeconds)
        } catch (e: Exception) {
            resultText.text = getString(R.string.invalid_input)
        }
    }

    private fun calculateDistance() {
        try {
            val totalSeconds = getTimeInSeconds()
            val paceInSeconds = getPaceInSeconds()

            if (paceInSeconds <= 0) throw IllegalArgumentException("Pace must be positive")
            if (totalSeconds <= 0) throw IllegalArgumentException("Time must be positive")

            val distance = totalSeconds.toDouble() / paceInSeconds
            resultText.text = getString(R.string.distance_result, distance)
        } catch (e: Exception) {
            resultText.text = getString(R.string.invalid_input)
        }
    }

    private fun calculateTime() {
        try {
            val distance = distanceInput.text.toString().toDouble()
            val paceInSeconds = getPaceInSeconds()

            if (distance <= 0) throw IllegalArgumentException("Distance must be positive")
            if (paceInSeconds <= 0) throw IllegalArgumentException("Pace must be positive")

            val totalSeconds = (distance * paceInSeconds).toInt()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            resultText.text = getString(R.string.time_result, hours, minutes, seconds)
        } catch (e: Exception) {
            resultText.text = getString(R.string.invalid_input)
        }
    }

    private fun convertPaceSpeed() {
        try {
            if (speedInput.text.isNotEmpty()) {
                val speed = speedInput.text.toString().toDouble()
                if (speed <= 0) throw IllegalArgumentException("Speed must be positive")

                val paceInMinutes = 60.0 / speed
                val paceMinutes = paceInMinutes.toInt()
                val paceSeconds = ((paceInMinutes - paceMinutes) * 60).toInt()
                resultText.text = getString(R.string.pace_result, paceMinutes, paceSeconds)
            } else if (paceMinutesInput.text.isNotEmpty() || paceSecondsInput.text.isNotEmpty()) {
                val paceInSeconds = getPaceInSeconds()
                if (paceInSeconds <= 0) throw IllegalArgumentException("Pace must be positive")

                val paceInHours = paceInSeconds / 3600.0
                val speed = 1 / paceInHours
                resultText.text = getString(R.string.speed_result, speed)
            }
        } catch (e: Exception) {
            resultText.text = getString(R.string.invalid_input)
        }
    }
}