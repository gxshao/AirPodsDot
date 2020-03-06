package org.mrsgx.airpodsdot.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.mrsgx.airpodsdot.R
import org.mrsgx.airpodsdot.services.AirPodsDotService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService()
    }

    private fun startService() {
        val serviceIntent = Intent(this, AirPodsDotService::class.java)
        startService(serviceIntent)
    }
}
