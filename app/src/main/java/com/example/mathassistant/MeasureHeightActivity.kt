package com.example.mathassistant

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_measure_height.*
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.tan

class MeasureHeightActivity : MenuOwnerActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    var accelerometer: Sensor? = null
    private var calculated = false
    private var angle = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure_height)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if(accelerometer != null) {
            loadPreviousState(savedInstanceState)

            btnStopMeasure.setOnClickListener {
                onStopMeasureClick()
            }
        } else {
            Toast.makeText(this,
                "Device does not have an accelerometer. Unable to perform any action", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onSaveInstanceState(activityInstanceState: Bundle) {
        super.onSaveInstanceState(activityInstanceState)
        activityInstanceState.putDouble("angle", angle)
        activityInstanceState.putBoolean("calculated", calculated)
        activityInstanceState.putString("altitude", txtAltitude.text.toString())
        activityInstanceState.putString("distance", txtDistance.text.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun loadPreviousState(savedInstanceState: Bundle?) {
        if(savedInstanceState != null) {
            angle = savedInstanceState.getDouble("angle")
            calculated = savedInstanceState.getBoolean("calculated")
            txtAltitude.setText(savedInstanceState.getString("altitude"))
            txtDistance.setText(savedInstanceState.getString("distance"))
            btnStopMeasure.text = "$angle°"
            if(calculated) {
                calculateTheHeight()
            }
        }
    }

    private fun onStopMeasureClick()  {
        val distValue = txtDistance.text.toString()
        val altitudeValue = txtDistance.text.toString()
        if(!calculated && properInput(distValue) && properInput(altitudeValue)) {
            sensorManager.unregisterListener(this)
            calculateTheHeight()
            calculated = true
        } else if(calculated) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            lblInformation.text = getString(R.string.information)
            calculated = false
        } else {
            Toast.makeText(this, "Inputs are incorrect!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTheHeight() {
        val angleInRadians = MathUtils.degreesToRadians(angle)
        val height = txtDistance.text.toString().toDouble()*tan(angleInRadians) +
                txtAltitude.text.toString().toDouble()
        lblInformation.text = "Object's height = ${MathUtils.round(height, 2)}\nTouch the circle to measure once again"
    }

    override fun onResume() {
        super.onResume();
        if(!calculated) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {  }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        val xVal = event.values[0]
        val yVal = event.values[1]
        val zVal = event.values[2]

        val denominator = sqrt(xVal*xVal + zVal*zVal)
        val tanValue = yVal/denominator
        angle = MathUtils.round(MathUtils.radiansToDegrees(atan(tanValue).toDouble()), 2)

        btnStopMeasure.text = "$angle°"

    }

    companion object{
        private fun properInput(input: String): Boolean {
            return input != "" && input != "."
        }
    }
}

