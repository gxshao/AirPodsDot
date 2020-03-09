package org.mrsgx.airpodsdot.activities

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.mrsgx.airpodsdot.R
import org.mrsgx.airpodsdot.services.AirPodsDotService

class MainActivity : AppCompatActivity() {
    private val mRequestEnableCode = 11002
    private lateinit var mBluetoothManager: BluetoothManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        requestEnableBluetooth()
    }


    private fun requestEnableBluetooth() {
        if (!mBluetoothManager.adapter.isEnabled) {
            val intentReqBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intentReqBluetooth, mRequestEnableCode)
        } else {
            startService()
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, AirPodsDotService::class.java)
        startForegroundService(serviceIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mRequestEnableCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    startService()
                } else {
                    // Update UI Button
                }
            }
        }
    }
}
