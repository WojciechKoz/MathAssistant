package com.example.mathassistant

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class MenuOwnerActivity: AppCompatActivity(), SensorEventListener {
    /**
     * abstract activity that contains menu with three buttons that are responsible for
     * dark mode. All activities in this app extends this activity but they not referring
     * to the menu at any point. This class is responsible for the right handling of the menu.
     */
    private lateinit var menu: Menu
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var lightSensorExists = false
    private var onErrorMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor != null) {
            lightSensorExists = true
        } else {
            lightSensorExists = false
            onErrorMessage = "Light Sensor does not exist!"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dark_mode_menu, menu)
        this.menu = menu!!

        DarkModeManager.initDarkModeMenu(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.itmLightMode -> {
                DarkModeManager.goLight()
                changeCheckButtonsState(0)
                sensorManager.unregisterListener(this)
                return false
            }
            R.id.itmDarkMode -> {
                DarkModeManager.goDark()
                changeCheckButtonsState(1)
                sensorManager.unregisterListener(this)
                return false
            }
            R.id.itmAutoMode -> {
                if(lightSensorExists) {
                    DarkModeManager.goAuto()
                    changeCheckButtonsState(2)
                    sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
                } else {
                    Toast.makeText(this, onErrorMessage, Toast.LENGTH_LONG).show()
                }
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeCheckButtonsState(indexOfClickedButton: Int) {
        for(i in 0 until menu.size()) {
            menu.getItem(i).isChecked = i == indexOfClickedButton
        }
    }

    override fun onResume() {
        super.onResume()
        // auto mode is enabled so lightSensor has to exist
        if(DarkModeManager.isAutoEnabled()) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {  }

    override fun onSensorChanged(event: SensorEvent) {
        val value = event.values[0]
        DarkModeManager.handleLightChange(value)
    }
}