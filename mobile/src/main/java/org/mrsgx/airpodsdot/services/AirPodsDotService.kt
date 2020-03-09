package org.mrsgx.airpodsdot.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log

class AirPodsDotService : Service() {

    private val mChannelId: String = "AirPodsDotChannel"
    private val mHandlerThread: HandlerThread = HandlerThread("AirPodsDotMainThread")
    private val mBleCallback = object : BleCallback {
        override fun onDiscoveryFinished(intent: Intent) {
        }

        override fun onDiscoveryStarted(intent: Intent) {
        }

        override fun onDeviceFound(bleDevice: BluetoothDevice) {
            Log.e(AirPodsDotService::class.java.toString(), bleDevice.address)
        }
    }
    private val mAirPodsDotReceiver = AirPodsDotReceiver(mBleCallback)
    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mHandler: Handler
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel =
            NotificationChannel(mChannelId, packageName, NotificationManager.IMPORTANCE_NONE)
        channel.vibrationPattern = null
        channel.setShowBadge(false)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        startForeground(
            100, Notification.Builder(this, mChannelId)
                .setSmallIcon(android.R.drawable.star_on)
                .build()
        )
        return START_STICKY
    }

    private fun init() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mAirPodsDotReceiver, intentFilter)

        mBluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mHandler.post(this::scheduledTask)
    }

    private fun scheduledTask() {
        mBluetoothManager.adapter.getProfileProxy(
            this, object : BluetoothProfile.ServiceListener {
                override fun onServiceDisconnected(profile: Int) {

                }

                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                    if (proxy == null) return
                    val list = proxy.connectedDevices
                    if (list != null) {
                        for (i in list) {
                            if (checkDeviceType(i)) {
                                // Show ui and current battery stuff
                            }
                        }
                    }
                }
            },
            mBluetoothManager.adapter.getProfileConnectionState(BluetoothProfile.HEADSET)
        )
    }

    private fun checkDeviceType(device: BluetoothDevice?): Boolean {
        if (device == null) {
            return false
        }
        if (BluetoothClass.Device.Major.AUDIO_VIDEO == device.bluetoothClass.majorDeviceClass
            && BluetoothDevice.DEVICE_TYPE_DUAL == device.type
        ) {
            return true
        }
        return false
    }

    override fun onDestroy() {
        unregisterReceiver(mAirPodsDotReceiver)
        super.onDestroy()
    }

    interface BleCallback {
        fun onDiscoveryFinished(intent: Intent)
        fun onDiscoveryStarted(intent: Intent)
        fun onDeviceFound(bleDevice: BluetoothDevice)
    }

    class AirPodsDotReceiver(bleCallback: BleCallback) : BroadcastReceiver() {
        private var mBleCallback: BleCallback = bleCallback
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        val deviceType = device.bluetoothClass

                        Log.e(
                            AirPodsDotService::class.java.toString(),
                            deviceType.majorDeviceClass.toString()
                        )
                        if (deviceType.majorDeviceClass != BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
                            return
                        }
                        mBleCallback.onDeviceFound(device)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mBleCallback.onDiscoveryFinished(intent)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    mBleCallback.onDiscoveryStarted(intent)
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_ON -> {

                        }
                        BluetoothAdapter.STATE_CONNECTED -> {

                        }
                        BluetoothAdapter.STATE_DISCONNECTED -> {

                        }
                        BluetoothAdapter.STATE_OFF -> {

                        }
                    }
                }
            }
        }
    }

}
