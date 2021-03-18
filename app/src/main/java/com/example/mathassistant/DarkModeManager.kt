package com.example.mathassistant

import android.view.Menu
import androidx.appcompat.app.AppCompatDelegate

object DarkModeManager {
    private const val LIGHT_CONST = 1
    private const val DARK_CONST = 2
    private const val AUTO_CONST = 3
    private const val BIAS = 100.0
    private var state = LIGHT_CONST
    private var lightMode = true

    fun goLight(): Unit {
        if(state != LIGHT_CONST) {
            if(!lightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                lightMode = true
            }
            state = LIGHT_CONST
        }
    }

    fun goDark(): Unit {
        if(state != DARK_CONST) {
            if(lightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                lightMode = false
            }
            state = DARK_CONST
        }
    }

    fun goAuto(): Unit {
        state = AUTO_CONST
    }

    fun handleLightChange(light: Float): Unit {
        if(state == AUTO_CONST) {
            if(light > BIAS && !lightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                lightMode = true
            } else if(light < BIAS && lightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                lightMode = false
            }
        }
    }

    fun isAutoEnabled(): Boolean {
        return state == AUTO_CONST
    }

    fun isLight(): Boolean {
        return lightMode
    }

    fun initDarkModeMenu(menu: Menu) {
        menu.getItem(0).isChecked = state == LIGHT_CONST
        menu.getItem(1).isChecked = state == DARK_CONST
        menu.getItem(2).isChecked = state == AUTO_CONST
    }

}